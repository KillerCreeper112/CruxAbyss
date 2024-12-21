package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class EmberLeaperGoal extends CruxMobModeledGoal implements Listener {
    public EmberLeaperGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_SQUISH, 1.5f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_HURT, 1.5f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_DEATH, 1.5f);
            }
        });
    }

    public void updateStats(){
        int size = getSize();
        float scale = .7f + (size * .17f);
        AbyssWorld world = CruxCore.inst().worldManager().getWorldOrNull(mob.getWorld().getUID(), AbyssWorld.class);
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(
            (CruxMath.random(4D, 6D) * (world == null ? 1D : world.getDifficulty())) * scale
        ));
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D * scale));
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-6));
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(12 * scale));
        CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(1.8D * scale));
    }

    public int getSize(){
        return CruxTag.get(mob, "size", PersistentDataType.INTEGER, 0);
    }

    public void updateSize(){
        if(!CruxTag.has(mob, "size", PersistentDataType.INTEGER)){
            setSize(CruxMath.random(0, 3));
            return;
        }
        setSize(getSize());
    }

    public void setSize(int size){
        //0.7 - 1.2
        float x = .7f + (size * .17f);
        applyModel(model -> model.setScale(x));
        CruxTag.set(mob, "size", PersistentDataType.INTEGER, size);
        updateStats();
    }

    protected int deathTime = 0;
    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        if(!died) return;
        Crux.getServer().getScheduler().runTaskTimer(Crux.getMainPlugin(), task ->{
            deathTime++;
            if(deathTime >= 20){
                task.cancel();
                explode();
                return;
            }
            if(deathTime % 5 == 0){
                CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_DEATH, CruxMath.random(1f, 2f)).playAt(mob);
            }
        }, 0L, 1L);
    }

    public void explode(){
        Location center = mob.getLocation();
        generateExplosionVectors(50, 10).forEach(v ->{
            new ParticleBuilder(Particle.FLAME)
                .location(center)
                .offset(v.getX(), v.getY(), v.getZ())
                .extra(.1)
                .count(0)
                .spawn()
            ;
        });

        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 2f).playAt(mob);
        CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_DEATH, CruxMath.random(1f, 2f)).playAt(mob);

        double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 2.5D;
        if(range <= 0D) return;
        attack(getNearbyTargets(range).toArray(new Entity[0]));
    }

    private static Vector generateRandomDirection() {
        Random rand = new Random();

        // Randomly generate spherical coordinates
        double theta = rand.nextDouble() * 2 * Math.PI; // Azimuthal angle (0 to 2π)
        double phi = Math.acos(2 * rand.nextDouble() - 1); // Polar angle (0 to π)

        // Convert to Cartesian coordinates
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);

        return new Vector(x, y, z);
    }

    // Generate random explosion vectors
    public static List<Vector> generateExplosionVectors(int numParticles, double maxMagnitude) {
        List<Vector> explosionVectors = new ArrayList<>();
        Random rand = CruxMath.random();

        for (int i = 0; i < numParticles; i++) {
            // Generate random direction
            Vector direction = generateRandomDirection();

            // Generate random magnitude (random scale for each particle's speed)
            double magnitude = rand.nextDouble() * maxMagnitude;

            // Scale direction by magnitude
            direction = new Vector(direction.getX() * magnitude, direction.getY() * magnitude, direction.getZ() * magnitude);

            explosionVectors.add(direction);
        }
        return explosionVectors;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setCancelled(true);
            stopAnimation("jump");
            playAnimation("land", true);
            CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_SQUISH, 1.5f).playAt(mob);
            onFall();
        }
    }

    public void onFall(){
        Location center = mob.getLocation();
        generateExplosionVectors(CruxMath.random(20, 30), 10).forEach(v ->{
            new ParticleBuilder(Particle.FLAME)
                .location(center)
                .offset(v.getX(), CruxMath.random(0, .1), v.getZ())
                .extra(.2)
                .count(0)
                .spawn()
            ;
        });
        new ParticleBuilder(Particle.LAVA)
            .location(center)
            .offset(.1, .1, .1)
            .extra(.1)
            .count(CruxMath.random(4, 6))
            .spawn()
        ;

        double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 1.6D;
        if(range <= 0D) return;
        attack(getNearbyTargets(range).toArray(new Entity[0]));
    }

    @Override
    protected int getFindCooldownMax() {
        return CruxMath.random(50, 120);
    }

    protected int prepareJumpTicks = 0;
    protected int jumpCooldown = 0;
    protected int spewCooldown = 0;
    protected int maxSpewTime = 0;
    protected int spewTicks = 0;

    @Override
    public void tick() {
        super.tick();
        if(target == null) return;
        targetTick();
    }

    public int getAttackFireTicks(){
        return CruxMath.random(30, 60);
    }

    @Override
    protected void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(CruxMath.testChance(35)) return;
        int fireTicks = getAttackFireTicks();
        Entity victim = event.getEntity();
        if(victim.getFireTicks() >= fireTicks) return;
        victim.setFireTicks(victim.getFireTicks() + fireTicks);
    }

    @Override
    protected void targetLogic() {
        if(target == null) return;
        double distance = this.getDistanceFromTarget();
        if (distance > this.getForgetTargetDistance()) {
            this.setTarget(null);
            return;
        }
        spewTick();
    }

    public void spewTick(){
        if(spewCooldown > 0){
            spewCooldown--;
            return;
        }
        if(maxSpewTime == 0){
            maxSpewTime = CruxMath.random(80, 200);
            spewTicks = 0;
        }

        if(CruxMath.testChance(15)) return;
        spewTicks++;

        if(!CruxMath.random().nextBoolean() || spewTicks % 2 == 0){
            spewLava(getSpoutLocation(), 1);
        }
        targetAttackNearbyLogic();

        if(spewTicks >= maxSpewTime){
            spewCooldown = CruxMath.random(60, 160);
            maxSpewTime = 0;
        }
    }

    public void targetAttackNearbyLogic(){
        if(attackCooldown > 0){
            attackCooldown--;
            return;
        }
        this.attackCooldown = (int)Math.ceil(CruxAttribute.ATTACK_SPEED.get(this.mob));
        double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE);
        if(range <= 0D) return;
        attack(getNearbyTargets(range).toArray(new Entity[0]));
    }

    public Collection<LivingEntity> getNearbyTargets(double range){
        return new GetEntityNear<>(DynamicLocation.createEntity(mob), LivingEntity.class)
            .filter(this::isValidNaturalTarget)
            .range(range)
            .find();
    }

    public void targetTick(){
        if(jumpCooldown > 0){
            jumpCooldown--;
            return;
        }

        if(isPreparingJump()){
            prepareJumpTicks++;
            if(prepareJumpTicks == getAnimationLengthTicks("prepare_jump")/2){
                prepareJumpTicks = 0;
                jumpCooldown = CruxMath.random(60, 120);
                jumpToTarget(target.getLocation());
            }
            return;
        }

        prepareJumpTicks = 1;
        playAnimation("prepare_jump", true);
        CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_SQUISH, 2f).playAt(mob);
    }

    public void spewLava(Location spout, int amount){
        while(amount > 0){
            amount--;
            Vector dir = new Vector(CruxMath.random(-1, 1), 1, CruxMath.random(-1, 1));
            new ParticleBuilder(Particle.LAVA)
                .offset(dir.getX(), dir.getY(), dir.getZ())
                .count(0)
                .extra(.2)
                .location(spout)
                .spawn()
            ;
            new ParticleBuilder(Particle.FLAME)
                .offset(dir.getX(), dir.getY(), dir.getZ())
                .count(0)
                .extra(.2)
                .location(spout)
                .spawn()
            ;
        }
    }

    public Location getSpoutLocation(){
        return getModel().getBone("spout").orElseThrow().getLocation();
    }

    public void jumpToTarget(Location target){
        stopAnimation("prepare_jump");
        playAnimation("jump", true);
        Vector vel = CruxMath.parabolicMotion(mob.getLocation().toVector(), target.toVector(), CruxMath.random(1, 5), CruxMath.random(1.6, 1.7));//gravity=1.65
        mob.setVelocity(vel);
        CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_JUMP, 1.5f).playAt(mob);
    }

    public boolean isPreparingJump(){
        return prepareJumpTicks > 0;
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}

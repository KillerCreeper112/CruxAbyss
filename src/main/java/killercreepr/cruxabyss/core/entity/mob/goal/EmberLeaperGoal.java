package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        if(!died) return;

    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setCancelled(true);
            stopAnimation("jump");
            playAnimation("land", true);
            CreateSound.sound(Sound.ENTITY_MAGMA_CUBE_SQUISH, 1.5f).playAt(mob);
        }
    }

    @Override
    protected int getFindCooldownMax() {
        return CruxMath.random(50, 120);
    }

    protected int prepareJumpTicks = 0;
    protected int jumpCooldown = 0;
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
        spewLava(getSpoutLocation(), 1);
        targetAttackNearbyLogic();
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
        Vector vel = CruxMath.parabolicMotion(mob.getLocation().toVector(), target.toVector(), CruxMath.random(3, 5), 1.65);
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

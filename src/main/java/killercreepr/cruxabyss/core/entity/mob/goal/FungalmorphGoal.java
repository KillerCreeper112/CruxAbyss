package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.core.entity.mob.goal.vilder.VilderGoal;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class FungalmorphGoal extends CruxMobModeledGoal implements Listener {
    public static final Key STRONG_ATTACK_KEY = VilderGoal.STRONG_ATTACK_KEY;
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    public FungalmorphGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, .6f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public int ambientMin() {
                return 100;
            }

            @Override
            public int ambientMax() {
                return 160;
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_SLIME_HURT, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_SLIME_DEATH, .6f);
            }
        });
    }

    protected int strongAttackCooldown;
    public boolean canUseStrongAttack(){
        return strongAttackCooldown < 1 && !isUsingStrongAttack();
    }

    public boolean isUsingStrongAttack(){
        return maxAttackTime > 0;
    }

    public void combatTick(){
        if(!isUsingStrongAttack()){
            combatNotUsingStrongAttackTick();
            return;
        }
        combatUsingStrongAttackTick();
    }

    public void combatNotUsingStrongAttackTick(){
        if(strongAttackCooldown > 0){
            strongAttackCooldown--;
        }
    }

    public void onHitAtTime(){
        this.attemptAttack();
    }

    public void onCombatStrongAttackComplete(){
        combatStrongFinish(currentAttackID);
        maxAttackTime = 0;
        hitAt = 0;
        currentAttackID = 0;
        CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_DAMAGE, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_KNOCKBACK, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_AOE, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_RANGE, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_KNOCKBACK_UP, STRONG_ATTACK_KEY);
    }

    public void combatStrongFinish(int id){
        switch (id){
            case 1 ->{
                CreateSound.sound(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2f).playAt(mob);
                CreateSound.sound(Sound.ENTITY_SLIME_JUMP, .4f).playAt(mob);

                double range = 2.5D;
                new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                    .location(mob.getLocation().add(0, mob.getHeight()/2, 0))
                    .offset(range/2, range/2, range/2)
                    .extra(.3)
                    .count(CruxMath.random(15, 25))
                    .colorTransition(
                        Color.fromRGB(0xC3FF87),
                        Color.fromRGB(0x3AA4D8),
                        1.2f
                    )
                    .spawn();

                new GetEntityNear<>(LivingEntity.class)
                    .center(mob)
                    .filter(this::isValidNaturalTarget)
                    .range(range)
                    .find().forEach(hit ->{
                        hit.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
                        EntityDamager.entityDamager(hit, mob)
                            .attack();
                    });
            }
        }
    }

    protected int deathTime = 0;
    @Override
    protected void onRemovalOrDeath(boolean died) {
        super.onRemovalOrDeath(died);
        if(!died) return;
        Location loc = mob.getLocation();
        Crux.getServer().getScheduler().runTaskTimer(Crux.getMainPlugin(), task ->{
            deathTime++;
            if(deathTime >= 10){
                task.cancel();
                explode(loc);
                return;
            }
            checkIfEntityStandingOnTick(loc);
        }, 0L, 2L);
    }

    public void checkIfEntityStandingOnTick(Location loc){
        loc.getWorld().getNearbyEntities(mob.getBoundingBox(),
            e -> e instanceof LivingEntity d && this.isValidNaturalTarget(d)).forEach(hit ->{
                EntityDamager.entityDamager(hit, mob)
                    .attack(EntityDamager.getDamage(mob),
                        CruxAttribute.get(mob, CruxAttribute.ATTACK_KNOCKBACK) * 2,
                        (CruxAttribute.get(mob, CruxAttribute.ATTACK_KNOCKBACK_UP) * 1.5) + 15);
        });
    }

    public void explode(Location loc){
        new GetEntityNear<>(LivingEntity.class)
            .center(loc)
            .range(4)
            .filter(this::isValidNaturalTarget)
            .find().forEach(hit ->{
                EntityDamager.entityDamager(hit, mob)
                    .attack(EntityDamager.getDamage(mob),
                        CruxAttribute.get(mob, CruxAttribute.ATTACK_KNOCKBACK) * 2,
                        (CruxAttribute.get(mob, CruxAttribute.ATTACK_KNOCKBACK_UP) * 1.5) + 15);
            });
    }

    public void combatUsingStrongAttackTick(){
        attackTime++;
        if(hitAt == attackTime){
            onHitAtTime();
            hitAt = 0;
        }
        if(attackTime >= maxAttackTime){
            onCombatStrongAttackComplete();
            return;
        }
        onCombatUsingStrongAttackTick();
    }
    public void onCombatUsingStrongAttackTick(){
        switch (currentAttackID){
            case 1 ->{
                CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, 2f).playAt(mob);
            }
        }
    }

    public int generateStrongAttackID(){
        return 1;//CruxMath.random(1,2);
    }

    public int getHitAtTime(int attackID){
        return switch(attackID){
            default -> 9;
        };
    }

    public void onUseStrongAttack(int attackID){
        switch(attackID){
            case 1 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -3D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
            }
        }
    }

    public void onUseStrongAttackApplyAllAttributes(int attackID){
        CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
            CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));
    }

    public int useStrongAttack(){
        strongAttackCooldown = CruxMath.random(30, 80);
        int atck = generateStrongAttackID();
        String id = "attack_strong_" + atck;
        playAnimation(id, true);
        this.maxAttackTime = (int) Math.ceil(getAnimationLengthTicks(id) / 2f);
        this.attackTime = 0;

        this.hitAt = getHitAtTime(atck);
        onUseStrongAttackApplyAllAttributes(atck);
        onUseStrongAttack(atck);
        return atck;
    }

    protected int attackTime = 0;
    protected int maxAttackTime = 0;
    protected int hitAt = 0;
    protected int currentAttackID;
    @Override
    public boolean preAttemptAttack() {
        if(canUseStrongAttack()){
            currentAttackID = useStrongAttack();
            return false;
        }
        if(isUsingStrongAttack() && hitAt != attackTime) return false;
        return super.preAttemptAttack();
    }

    @Override
    protected void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(isUsingStrongAttack()) return;
        String id = generateAttackAnimationID();
        if(id == null) return;
        playAnimation(id, true);
    }


    public String generateAttackAnimationID(){
        return "attack_" + CruxMath.random(1,3);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    @Override
    public double getFindTargetRange() {
        return 8D;
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
        if(moveSpeed > 0D){
            swimmer.tick();
        }
    }

    @Override
    public void tick() {
        super.tick();
        combatTick();
        movementTick();
    }
}

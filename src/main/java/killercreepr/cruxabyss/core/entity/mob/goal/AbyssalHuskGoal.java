package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.goal.vilder.VilderGoal;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class AbyssalHuskGoal extends CruxMobModeledGoal implements Listener {
    public static final Key STRONG_ATTACK_KEY = VilderGoal.STRONG_ATTACK_KEY;
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    public AbyssalHuskGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_HUSK_AMBIENT, .6f);
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
                return CreateSound.sound(Sound.ENTITY_HUSK_HURT, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_HUSK_DEATH, .6f);
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
    public void onCombatUsingStrongAttackTick(){}

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

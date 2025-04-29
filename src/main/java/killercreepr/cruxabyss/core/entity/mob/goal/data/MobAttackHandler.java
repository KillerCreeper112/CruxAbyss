package killercreepr.cruxabyss.core.entity.mob.goal.data;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;

import java.util.*;

public class MobAttackHandler {
    public static final Key STRONG_ATTACK_KEY = Crux.key("strong_attack");
    protected final Mob mob;
    protected final CruxMobModeledGoal goal;

    protected final List<MobAttack> attacks;
    protected final List<MobAttack> cooldownAttacks;
    protected final Map<UUID, Long> lastAttacked = new HashMap<>();
    public MobAttackHandler(Mob mob, CruxMobModeledGoal goal, List<MobAttack> attacks, List<MobAttack> cooldownAttacks) {
        this.mob = mob;
        this.goal = goal;
        this.attacks = attacks;
        this.cooldownAttacks = cooldownAttacks;
    }

    public void hit(Entity e){
        lastAttacked.put(e.getUniqueId(), System.currentTimeMillis());
    }

    public boolean wasHitWithin(Entity e, int ticks){
        Long time = lastAttacked.get(e.getUniqueId());
        return time != null && CruxMath.hasOccurredWithin(time, ticks);
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
        goal.attemptAttack();
    }

    public void onCombatStrongAttackComplete(){
        maxAttackTime = 0;
        hitAt = 0;
        if(currentAttack != null) currentAttack.onFinish();
        currentAttack = null;
        CruxAttribute.removeModifiers(mob, STRONG_ATTACK_KEY);
        /*CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_DAMAGE, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_KNOCKBACK, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_AOE, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_RANGE, STRONG_ATTACK_KEY);
        CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_KNOCKBACK_UP, STRONG_ATTACK_KEY);*/
    }

    public void combatUsingStrongAttackTick(){
        attackTime++;
        if(hitAt == attackTime){
            onHitAtTime();
            hitAt = 0;
        }
        if(currentAttack != null) currentAttack.onTick();
        if(currentAttack == null || attackTime >= maxAttackTime || !goal.isPlayingAnimation(currentAttack.getAnimationID())){
            onCombatStrongAttackComplete();
            return;
        }
        onCombatUsingStrongAttackTick();
    }
    public void onCombatUsingStrongAttackTick(){}

    public MobAttack generateStrongAttack(List<MobAttack> attacks){
        if(attacks.isEmpty()) return null;

        List<MobAttack> list = new ArrayList<>(attacks);
        while(!list.isEmpty()){
            MobAttack got = CruxCollection.getRandom(list);
            if(got.canUseAttack()) return got;
            list.remove(got);
        }
        return null;
    }

    public void onUseStrongAttack(MobAttack attack){
        attack.onUse();
    }

    public void onUseStrongAttackApplyAllAttributes(MobAttack attack){
    }

    public MobAttack useStrongAttack(List<MobAttack> attacks){
        MobAttack attack = generateStrongAttack(attacks);
        if(attack == null) return null;
        int setCooldown = attack.getCooldown();
        strongAttackCooldown = setCooldown < 0 ? calculateStrongAttackCooldown() : setCooldown;
        String id = attack.getAnimationID();
        goal.playAnimation(id, true);
        this.maxAttackTime = (int) Math.ceil((float) goal.getAnimationLengthTicks(id) / 2f);
        this.attackTime = 0;

        this.hitAt = (int) Math.ceil((float) attack.getHitTime() / 2f);
        onUseStrongAttackApplyAllAttributes(attack);
        onUseStrongAttack(attack);
        return attack;
    }

    public int calculateStrongAttackCooldown(){
        return CruxMath.random(30, 80);
    }

    protected int attackTime = 0;
    protected int maxAttackTime = 0;
    protected int hitAt = 0;
    protected MobAttack currentAttack;

    public boolean preAttemptAttack() {
        if(canUseStrongAttack()){
            currentAttack = useStrongAttack(attacks);
            return false;
        }
        if(isUsingStrongAttack() && hitAt != attackTime) return false;
        return true;
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
    }

    public void cooldownAttacksTick(){
        if(isUsingStrongAttack()) return;
        if(!canUseStrongAttack()) return;
        currentAttack = useStrongAttack(cooldownAttacks);
    }

    public void tick() {
        combatTick();
        if(goal.getTarget() != null && !cooldownAttacks.isEmpty()){
            cooldownAttacksTick();
        }
        movementTick();
    }
}

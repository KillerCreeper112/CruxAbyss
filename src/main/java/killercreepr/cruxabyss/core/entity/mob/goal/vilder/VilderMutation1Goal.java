package killercreepr.cruxabyss.core.entity.mob.goal.vilder;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VilderMutation1Goal extends VilderGoal{
    public VilderMutation1Goal(@NotNull Mob mob) {
        super(mob);
    }

    @Override
    public int generateStrongAttackID(){
        return CruxMath.random(1,3);
    }

    @Override
    public String generateAttackAnimationID() {
        return "attack_" + CruxMath.random(1,3);
    }

    public Location getMiddleArmTopPos(){
        return getModel().getBone("middle_arm_top_pos").orElse(null).getLocation();
    }

    public BoundingBox getMiddleArmTopBox(Location pos){
        double range = .7D;
        return BoundingBox.of(pos, range, range, range);
    }

    @Override
    public int getHitAtTime(int attackID) {
        return -1;
    }

    @Override
    public void onUseStrongAttack(int attackID) {
        switch(attackID){
            case 1 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .8D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK_UP,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 14D));
            }
            case 2 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1.2D, CruxAttribute.Operation.MULTIPLY));
            }
            case 3 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1.5D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK_UP,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 7D));
            }
        }
    }

    protected final Map<UUID, Long> lastHit = new HashMap<>();
    public void hit(Entity e){
        lastHit.put(e.getUniqueId(), System.currentTimeMillis());
    }
    public boolean hasHitWithin(Entity e, int ticks){
        Long time = lastHit.get(e.getUniqueId());
        if(time == null) return false;
        return CruxMath.hasOccurredWithin(time, ticks);
    }

    @Override
    public void onCombatStrongAttackComplete() {
        super.onCombatStrongAttackComplete();
        lastHit.clear();
    }

    @Override
    public void onCombatUsingStrongAttackTick() {
        super.onCombatUsingStrongAttackTick();
        switch (currentAttackID){
            case 1, 2, 3 -> {
                Location pos = getMiddleArmTopPos();
                BoundingBox box = getMiddleArmTopBox(pos);
                attack(
                    mob.getWorld().getNearbyEntities(box, e ->{
                        if(!(e instanceof LivingEntity d)) return false;
                        if(hasHitWithin(e, 5)) return false;
                        if(!(isValidNaturalTarget(d) || d.equals(target))) return false;
                        hit(e);
                        return true;
                    })
                );
            }
        }
    }
}

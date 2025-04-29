package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.goal.data.MobAttackHandler;
import killercreepr.cruxabyss.core.entity.mob.goal.data.StrongMobAttack;
import killercreepr.cruxabyss.core.entity.mob.goal.vilder.VilderGoal;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbyssalHuskGoal extends CruxMobModeledGoal implements Listener {
    public static final Key STRONG_ATTACK_KEY = VilderGoal.STRONG_ATTACK_KEY;
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final MobAttackHandler attackHandler;
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

        attackHandler = new MobAttackHandler(mob, this, List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 9;
                }
            }
        ));
    }

    @Override
    public boolean preAttemptAttack() {
        if(attackHandler.preAttemptAttack()){
            return super.preAttemptAttack();
        }
        return false;
    }

    @Override
    public void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(attackHandler.isUsingStrongAttack()) return;
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
        if(moveSpeed > 0D){
            swimmer.tick();
        }
    }

    @Override
    public void tick() {
        super.tick();
        attackHandler.tick();
        movementTick();
    }
}

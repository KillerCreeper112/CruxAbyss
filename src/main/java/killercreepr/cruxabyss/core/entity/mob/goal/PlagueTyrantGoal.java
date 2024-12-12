package killercreepr.cruxabyss.core.entity.mob.goal;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlagueTyrantGoal extends CruxMobModeledGoal implements Listener {
    public PlagueTyrantGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VINDICATOR_AMBIENT, .2f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VINDICATOR_HURT, .2f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_VINDICATOR_DEATH, .2f);
            }
        });
    }

    protected int attackTime = -1;
    protected int attackSwingCooldown = CruxMath.random(60, 100);
    @Override
    public boolean preAttemptAttack() {
        if (this.attackTime == -1) {
            String attackID = "attack_" + CruxMath.random(1, 3);
            this.attackTime = getAnimationLengthTicks(attackID) / 2;
            this.playAnimation(attackID, true);
            CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f).playAt(mob);
            return false;
        }
        return super.preAttemptAttack();
    }

    public void attackTick() {
        if(this.attackTime < 1) return;
        this.attackTime--;
        if (this.attackTime < 1) {
            this.attemptAttack();
            attackTime = -1;
        }
    }

    protected static final Key targetSpeed = Crux.key("target_speed");
    protected boolean hadTargetLastTick = false;
    @Override
    public void tick() {
        super.tick();
        if(target != null) targetTick();
        attackTick();

        if(isPlayingAnimation("attack_swing")){
            Location hand = getRightHandLocation();
            hand.getWorld().getNearbyEntities(hand, 2D, 2D, 2D, e -> e instanceof LivingEntity dd && isValidNaturalTarget(dd))
                .forEach(this::attack);
        }else CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, Crux.key("swing"));
        movementTick();
    }

    public void movementTick(){
        boolean hasTarget = target != null;
        if(target == null){
            if(hadTargetLastTick){
                CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, targetSpeed);
                //applyAnimation("walk", a -> a.setSpeed(1D));
            }
        }else{
            if(!hadTargetLastTick){
                CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.modifier(
                    targetSpeed, .1, CruxAttribute.Operation.MULTIPLY
                ));
            }
        }
        if(hasTarget){
            if(isPlayingAnimation("walk")){
                stopAnimation("walk", true);
                playAnimation("vicious_run", true);
            }else stopAnimation("vicious_run");
        }
        if(hasTarget == hadTargetLastTick) return;
        hadTargetLastTick = hasTarget;

        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
    }

    public void targetTick(){
        if(attackSwingCooldown > 0){
            attackSwingCooldown--;
            return;
        }
        if(attackTime > 0) return;
        double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 1.3D;
        if(getSquaredDistanceFromTarget() <= (range*range)){
            attackSwingCooldown = CruxMath.random(100, 260);
            attackTime = getAnimationLengthTicks("attack_swing") / 2;
            CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.modifier(Crux.key("swing"), -1D, CruxAttribute.Operation.MULTIPLY));
            playAnimation("attack_swing", true);
            CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, .5f).playAt(mob);
        }
    }

    public Location getRightHandLocation(){
        ModelBone hand = getModel().getBone("right_hand").orElseThrow();
        return hand.getLocation();
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}

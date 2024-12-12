package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.mount.controller.MountControllerTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxedBoundingBox;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PlagueTyrantGoal extends CruxMobModeledGoal implements Listener {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
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
    protected int throwCooldown = CruxMath.random(60, 100);
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

    @Override
    public boolean shouldConstantlyLookAtTarget() {
        return true;
    }

    public void animationPropertyTick(){
        ActiveModel model = getModel();
        if(model ==null) return;
        AnimationHandler.DefaultProperty current = model.getAnimationHandler().getDefaultProperty(ModelState.WALK);
        AnimationHandler.DefaultProperty should = getDefaultWalk();
        if(current == should) return;
        stopAnimation(current.getAnimation());
        model.getAnimationHandler().setDefaultProperty(should);
    }

    protected static final Key targetSpeed = Crux.key("target_speed");
    protected boolean hadTargetLastTick = false;
    protected boolean swungLastTick = false;
    @Override
    public void tick() {
        animationPropertyTick();
        if(isPlayingAnimation("pickup_and_throw")){
            if(target != null) mob.lookAt(target);
            int time = getPlayingAnimationTimeTicks("pickup_and_throw");
            if(time < 0) return;
            //grab time
            if(time >= 15 && time <= 25){
                MountManager mountManager = getModel().getMountManager().orElseThrow();
                mountManager.setCanRide(true);

                applyHandLocations(hand ->{
                    BoundingBox hitbox = CruxedBoundingBox.boundingBox(hand, 2D);
                    hand.getWorld().getNearbyEntities(hitbox, e -> e instanceof LivingEntity dd && isValidNaturalTarget(dd))
                        .forEach(e ->{
                            mountManager.mountPassenger("right_hand", e,
                                (entity, mount) -> MountControllerTypes.WALKING_FORCE.createController(e, mount));
                        });
                });
                return;
            }
            if(time >= 25 && time <= 30){
                MountManager mountManager = getModel().getMountManager().orElseThrow();
                if(mountManager.getSeat("right_hand").orElseThrow().getPassengers().isEmpty()){
                    stopAnimation("pickup_and_throw");
                    return;
                }
            }
            //throw time
            if(time >= 35){
                MountManager mountManager = getModel().getMountManager().orElseThrow();
                mountManager.getSeat("right_hand").orElseThrow().getPassengers().forEach(e ->{
                    mountManager.dismountPassenger(e);
                    Vector dir = mob.getEyeLocation().getDirection()
                        .multiply(1.5);
                    dir.setY(1.4);
                    e.setVelocity(dir);
                });
                CreateSound.sound(Sound.ENTITY_ENDER_DRAGON_FLAP, .8f).playAt(mob);
                return;
            }
            return;
        }

        super.tick();
        if(target != null) targetTick();
        attackTick();

        if(isPlayingAnimation("attack_swing")){
            if(getPlayingAnimationProgress("attack_swing") > .5f){
                if(!swungLastTick){
                    swungLastTick = true;
                    CreateSound.sound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, .5f).playAt(mob);
                }
                applyHandLocations(hand ->{
                    BoundingBox hitbox = CruxedBoundingBox.boundingBox(hand, 2.5D);
                    hand.getWorld().getNearbyEntities(hitbox, e -> e instanceof LivingEntity dd && isValidNaturalTarget(dd))
                        .forEach(this::attack);
                });
                new ParticleBuilder(Particle.CLOUD)
                    .location(getRightHandLocation())
                    .extra(.1)
                    .count(CruxMath.random(2, 3))
                    .offset(1, 1, 1)
                    .spawn()
                ;
            }else swungLastTick = false;
        }else CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, Crux.key("swing"));
        movementTick();
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
        boolean hasTarget = target != null;

        if(moveSpeed > 0D){
            swimmer.tick();
        }

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
        /*if(hasTarget){
            if(isPlayingAnimation("walk")){
                stopAnimation("walk", true);
                playAnimation("vicious_run", false);
            }else if(isPlayingAnimation("idle")){
                stopAnimation("vicious_run");
            }
        }*/
        if(hasTarget == hadTargetLastTick) return;
        hadTargetLastTick = hasTarget;

        /*if(hasTarget){
            if(previousMoveProperty == null){
                previousMoveProperty = getModel().getAnimationHandler().getDefaultProperty(ModelState.WALK);
                getModel().getAnimationHandler().setDefaultProperty(new AnimationHandler.DefaultProperty(
                    ModelState.WALK, "vicious_run", .2D, .2D, 1D
                ));
            }
        }else{
            if(previousMoveProperty != null){
                getModel().getAnimationHandler().setDefaultProperty(previousMoveProperty);
                previousMoveProperty = null;
            }
        }*/
    }

    protected final AnimationHandler.DefaultProperty WALK_VICIOUS_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "vicious_run", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_SWIM = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "swim", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_NORMAL = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "walk", .2D, .2D, 1D
    );

    public AnimationHandler.DefaultProperty getDefaultWalk(){
        if(swimmer.isSwimming()) return WALK_SWIM;
        if(target == null) return WALK_NORMAL;
        return WALK_VICIOUS_RUN;
    }

    public void targetTick(){
        if(throwCooldown > 0){
            throwCooldown--;
        }
        if(attackSwingCooldown > 0){
            attackSwingCooldown--;
        }
        if(attackTime > 0) return;
        if(throwCooldown > 0 && attackSwingCooldown > 0) return;
        double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 1.3D;
        if(throwCooldown < 1){
            if(getSquaredDistanceFromTarget() <= (range*range)){
                throwCooldown = CruxMath.random(60, 100);
                CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.modifier(Crux.key("swing"), -5D, CruxAttribute.Operation.MULTIPLY));
                playAnimation("pickup_and_throw", true);
                CreateSound.sound(Sound.ENTITY_VINDICATOR_AMBIENT, .1f).playAt(mob);
            }
            return;
        }
        if(attackSwingCooldown < 1){
            if(getSquaredDistanceFromTarget() <= (range*range)){
                attackSwingCooldown = CruxMath.random(100, 260);
                attackTime = getAnimationLengthTicks("attack_swing") / 2;
                CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.modifier(Crux.key("swing"), -5D, CruxAttribute.Operation.MULTIPLY));
                playAnimation("attack_swing", true);
                CreateSound.sound(Sound.ENTITY_VINDICATOR_AMBIENT, .1f).playAt(mob);
            }
            return;
        }
    }

    public void applyHandLocations(Consumer<Location> consumer){
        consumer.accept(getRightHandLocation());
        consumer.accept(getRightHand2Location());
    }

    public Location getRightHandLocation(){
        ModelBone hand = getModel().getBone("right_hand").orElseThrow();
        return hand.getLocation();
    }

    public Location getRightHand2Location(){
        ModelBone hand = getModel().getBone("right_hand2").orElseThrow();
        return hand.getLocation();
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}

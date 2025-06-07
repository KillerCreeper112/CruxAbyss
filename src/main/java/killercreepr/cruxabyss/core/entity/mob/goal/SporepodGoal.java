package killercreepr.cruxabyss.core.entity.mob.goal;

import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import org.bukkit.*;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SporepodGoal extends CruxMobModeledGoal implements Listener {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final MobAttackHandler attackHandler;
    public SporepodGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_DROWNED_AMBIENT,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
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
                return CreateSound.sound(Sound.ENTITY_DROWNED_HURT_WATER,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_DROWNED_DEATH_WATER,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }
        });

        attackHandler = new MobAttackHandler(mob, this, List.of(
            /*new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    mob.getAttribute(Attribute.MOVEMENT_SPEED).addTransientModifier(
                        new AttributeModifier(STRONG_ATTACK_KEY, -5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
                    );
                    *//*CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));*//*

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
            }*/
        ), List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.8D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .3D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 13;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    double distance = CruxAttribute.ATTACK_RANGE.get(mob) * 1.35D;
                    return getSquaredDistanceFromTargetHitbox() <= (distance*distance);
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

        if(CruxMath.testChance(60)){
            run();
        }
    }


    @Override
    public void entityDamageMob(EntityDamageByEntityEvent event) {
        super.entityDamageMob(event);

        if(!event.getEntity().equals(mob)) return;
        if(runTicks < 1 && CruxMath.testChance(50)){
            run();
        }
    }

    public String generateAttackAnimationID(){
        return "attack_" + CruxMath.random(1,1);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        if(moveSpeed > 0D){
            swimmer.tick();
        }
    }

    /*@Override
    public void moveTo(double speed) {
        if (this.target != null) {
            if (this.lastKnownTargetLocation != null && this.lostTarget >= this.followLostTargetTicks()) {
                if (this.lostTarget < this.searchLostTargetTicks()) {
                    moveAwayFromTarget(this.target, speed);
                } else if (this.lostTarget >= this.searchLostTargetTicks() && this.lostTarget <= this.searchLostTargetTicks() + 5) {
                    this.moveTo(this.mob.getLocation(), speed);
                }
            } else {
                moveAwayFromTarget(this.target, speed);
            }

        }
    }

    public void moveAwayFromTarget(Entity target, double speed){

    }*/
    protected final AnimationHandler.DefaultProperty WALK_RUN = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "run", .2D, .2D, 1D
    );
    protected final AnimationHandler.DefaultProperty WALK_NORMAL = new AnimationHandler.DefaultProperty(
        ModelState.WALK, "walk", .2D, .2D, 1D
    );

    public AnimationHandler.DefaultProperty getDefaultWalk(){
        return runTicks > 0 ? WALK_RUN : WALK_NORMAL;
    }

    public void animationPropertyTick(){
        ActiveModel model = getModel();
        if(model ==null) return;
        AnimationHandler.DefaultProperty current = model.getAnimationHandler().getDefaultProperty(ModelState.WALK);
        AnimationHandler.DefaultProperty should = getDefaultWalk();
        if(current.equals(should)) return;
        stopAnimation(current.getAnimation());
        model.getAnimationHandler().setDefaultProperty(should);
        playAnimation(should.getAnimation(), false);
    }

    public void run(){
        mob.getPathfinder().stopPathfinding();
        updateRunTarget();
        runTicks = CruxMath.random(60, 100);
    }

    public void updateRunTarget(){
        runCenter = runCenter == null ? mob.getLocation().add(CruxMath.random(-5, 5), 0, CruxMath.random(-5, 5)) :
            runCenter.add(CruxMath.random(-5, 5), 0, CruxMath.random(-5, 5));
        runTo = runCenter.clone().add(CruxMath.randomSigned(2, 5), 0, CruxMath.randomSigned(2, 5));
        CraftMob e = (CraftMob)this.mob;
        e.getHandle().getMoveControl().setWantedPosition(runTo.getX(), runTo.getY(), runTo.getZ(), 2f);
    }

    protected int runTicks = 0;
    protected Location runTo;
    protected Location runCenter;
    @Override
    public void tick() {
        animationPropertyTick();
        if(runTicks > 0){
            runTicks--;
            if(runTo != null){
                CraftMob e = (CraftMob)this.mob;
                if(e.getHandle().getNavigation().isDone()){
                    updateRunTarget();
                    return;
                }

                e.getHandle().getMoveControl().setWantedPosition(runTo.getX(), runTo.getY(), runTo.getZ(), (double)1.0F);
            }
            return;
        }

        super.tick();
        attackHandler.tick();
        movementTick();
    }
}

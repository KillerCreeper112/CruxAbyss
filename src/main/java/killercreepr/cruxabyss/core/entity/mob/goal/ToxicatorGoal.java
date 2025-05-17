package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.entity.mob.goal.OutpostTargeterGoal;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ToxicatorGoal extends CruxMobModeledGoal implements Listener, PathTargetMobGoal, OutpostTargeterGoal {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final PathTargetMobGoal pathTarget = PathTargetMobGoal.pathTargetMobGoal(this, 1.1D);
    public ToxicatorGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VINDICATOR_AMBIENT, 0.4f, .6f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VINDICATOR_HURT, 0.4f, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_VINDICATOR_DEATH, 0.4f, .6f);
            }
        });
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        switch (event.getCause()){
            case FIRE, FIRE_TICK, HOT_FLOOR, CAMPFIRE ->{
                event.setCancelled(true);
            }
        }
    }

    @Override
    protected boolean findAndSetTarget(@Nullable Predicate<Entity> targetCheck) {
        if(hasPath() && !isWithinTargetedOutpost()){
            return false;
        }
        return super.findAndSetTarget(targetCheck);
    }

    public boolean isWithinTargetedOutpost(){
        if(targetOutpost == null) return false;
        return targetOutpost.getOrDefault(StoredStructureComponents.OUTER_BOX, targetOutpost.getBoundingBox()).contains(mob.getLocation().toVector());
    }

    protected GoalPath lastPath;
    @Override
    public @Nullable GoalPath getPath() {
        return pathTarget.getPath();
    }

    @Override
    public void setPath(@Nullable GoalPath goalPath) {
        pathTarget.setPath(goalPath);
        if(goalPath != null) lastPath = goalPath;
    }
    public void structureTick(){
        if(targetOutpost == null || lastPath == null || pathTarget.hasPath()) return;
        if(isWithinTargetedOutpost()) return;
        setPath(GoalPath.goalPath(lastPath.getNodes()));
    }

    public void mountTick(){
        if(mob.getVehicle() != null){
            playAnimation("mounted", false);
        }else if(isPlayingAnimation("mounted")){
            stopAnimation("mounted");
        }
    }

    @Override
    public void tick() {
        swimmer.tick();
        super.tick();

        structureTick();
        if(target == null) pathTarget.tick();
        mountTick();
        /*if(locationTarget != null && target == null){
            moveTo(locationTarget, 1.1D);
        }*/
    }

    @Override
    public double getFindTargetRange() {
        return getFollowDistance();
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(mob)) return;
        String attackID = "attack_" + CruxMath.random(1, 3);
        playAnimation(attackID, true);
    }

    protected StoredStructure targetOutpost;
    @Override
    public StoredStructure getOutpostTarget() {
        return targetOutpost;
    }

    @Override
    public void setOutpostTarget(StoredStructure structure) {
        this.targetOutpost = structure;
    }

    /*@Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }

    protected static final Key targetSpeed = Crux.key("target_speed");
    protected boolean hadTargetLastTick = false;
    @Override
    public void tick() {
        super.tick();

        boolean hasTarget = target != null;
        if(target == null){
            if(hadTargetLastTick){
                CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, targetSpeed);
                applyAnimation("walk", a -> a.setSpeed(1D));
            }
        }else{
            if(!hadTargetLastTick){
                CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.modifier(
                    targetSpeed, .35, CruxAttribute.Operation.MULTIPLY
                ));
            }
        }
        if(hasTarget){
            applyAnimation("walk", moveAnimationSpeed);
        }
        if(hasTarget == hadTargetLastTick) return;
        hadTargetLastTick = hasTarget;
        double move = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(move);
    }
    protected final Consumer<IAnimationProperty> moveAnimationSpeed = a ->{
        a.setSpeed(1.5D);
    };*/
}

package killercreepr.cruxabyss.core.entity.mob.goal;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.path.CruxGoalPathTargetMobGoal;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import net.minecraft.world.level.pathfinder.PathType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoidDwellerGoal extends CruxMobModeledGoal implements Listener, PathTargetMobGoal {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final MobAttackHandler attackHandler;
    protected final PathTargetMobGoal pathTarget = new CruxGoalPathTargetMobGoal(this, 1.1D, false){
        @Override
        public void onCurrentNodeTick(GoalNode node) {
            Vector target = new Vector(node.x(), node.y(), node.z());
            Vector moveVec = target.subtract(mob.getLocation().toVector()).normalize().multiply(moveSpeed);

            Location lookAt = CruxLoc.lookAt(mob.getEyeLocation(), target.toLocation(mob.getWorld()));
            mob.setRotation(lookAt.getYaw(), lookAt.getPitch());
            mob.setVelocity(moveVec.add(knockback));
        }
    };
    public VoidDwellerGoal(@NotNull Mob mob) {
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

    @Override
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityKnockback(EntityKnockbackEvent event) {
        if(!event.getEntity().equals(mob)) return;
        knockback = event.getKnockback().multiply(1.2D);
    }


    public void moveAwayFromTarget(Entity target, double speed){

    }

    protected int wanderCooldown = 0;
    protected double knockbackResistance = 0.8D;

    protected Vector knockback = new Vector();
    protected final double moveSpeed = 0.3D;

    public void handleWandering() {
        if(hasValidPath()) return;
        if (wanderCooldown-- <= 0) {
            generateWanderTarget();
            wanderCooldown = CruxMath.random(60, 120); // ticks before next wander (3–6 seconds)
        }
    }

    public boolean hasValidPath(){
        return pathTarget.hasPath() && !pathTarget.getPath().hasFinished();
    }

    public void generateWanderTarget() {
        if (!hasValidPath()) {
            Location center = mob.getLocation();
            PathType currentPathType = pickRandomPathType(); // e.g. random from enum
            double radius = CruxMath.random(4.0, 10.0);
            int steps = CruxMath.random(10, 16);
            GoalPath path = generatePath(currentPathType, center, steps, radius);
            setPath(path);
        }
    }

    public boolean isValidWanderLocation(Location loc){
        int minY = loc.getWorld().getMinHeight() - 5;
        return loc.getY() < minY;
    }

    public double pathNodeCloseEnoughDistance = 1.5D;
    public GoalPath generatePath(PathType type, Location origin, int steps, double radius) {
        List<GoalNode> points = new ArrayList<>();

        switch (type) {
           /* case RANDOM -> {
                for (int i = 0; i < steps; i++) {
                    double dx = CruxMath.random(-radius, radius);
                    double dy = CruxMath.random(-radius / 2, radius / 2);
                    double dz = CruxMath.random(-radius, radius);
                    points.add(GoalNode.distanceGoalNode(origin.clone().add(dx, dy, dz), pathNodeCloseEnoughDistance));
                }
            }*/

            case CIRCLE -> {
                for (int i = 0; i < steps; i++) {
                    double angle = (2 * Math.PI * i) / steps;
                    double dx = radius * Math.cos(angle);
                    double dz = radius * Math.sin(angle);
                    Location loc = origin.clone().add(dx, 0, dz);
                    if(!isValidWanderLocation(loc)) break;
                    points.add(GoalNode.distanceGoalNode(loc, pathNodeCloseEnoughDistance));
                }
            }

            case SPIRAL -> {
                double heightStep = radius / steps; // vertical rise
                for (int i = 0; i < steps; i++) {
                    double angle = (2 * Math.PI * i) / steps;
                    double spiralRadius = radius * (i / (double) steps);
                    double dx = spiralRadius * Math.cos(angle);
                    double dz = spiralRadius * Math.sin(angle);
                    double dy = heightStep * i;
                    Location loc = origin.clone().add(dx, dy, dz);
                    if(!isValidWanderLocation(loc)) break;
                    points.add(GoalNode.distanceGoalNode(loc, pathNodeCloseEnoughDistance));
                }
            }
            case WAVE -> {
                Vector forward = origin.getDirection().setY(0).normalize();
                if (forward.lengthSquared() == 0) forward = new Vector(1, 0, 0);

                Vector sideways = new Vector(-forward.getZ(), 0, forward.getX()); // perpendicular horizontal axis

                double waveHeight = 1.2; // max vertical Y oscillation
                double tiltAmplitude = 1.5; // how far it tilts sideways
                double waveLength = 2.0; // spacing between wave nodes

                for (int i = 0; i < steps; i++) {
                    double progress = i / (double) steps;
                    double t = i * waveLength;

                    double verticalOffset = Math.sin(progress * 2 * Math.PI) * waveHeight;
                    double sidewaysOffset = Math.cos(progress * 2 * Math.PI) * tiltAmplitude;

                    Vector pathOffset = forward.clone().multiply(t)
                        .add(sideways.clone().multiply(sidewaysOffset))
                        .add(new Vector(0, verticalOffset, 0));

                    Location loc = origin.clone().add(pathOffset);
                    if(!isValidWanderLocation(loc)) break;
                    points.add(GoalNode.distanceGoalNode(loc, pathNodeCloseEnoughDistance));
                }
            }
        }

        if(points.isEmpty()) return null;
        return GoalPath.goalPath(points);
    }

    protected GoalPath lastPath;
    @Nullable
    @Override
    public GoalPath getPath() {
        return pathTarget.getPath();
    }

    @Override
    public void setPath(@Nullable GoalPath goalPath) {
        pathTarget.setPath(goalPath);
        if(goalPath != null) lastPath = goalPath;
    }

    public PathType pickRandomPathType() {
        PathType[] types = PathType.values();
        return types[CruxMath.random(0, types.length - 1)];
    }

    public void aboveMinHeightTick(){
        aboveMinHeightTicks++;

        if(!isAboveMinHeightTooLong()) return;
        if(wanderCooldown > 0){
            if(hasValidPath()) return;
            wanderCooldown--;
            return;
        }

        generateAboveHeightTarget();
    }

    public void generateAboveHeightTarget() {
        Location center = mob.getLocation();
        center.setY(mob.getWorld().getMinHeight() - 6);
        PathType currentPathType = pickRandomPathType();
        double radius = CruxMath.random(4.0, 10.0);
        int steps = CruxMath.random(10, 16);
        GoalPath path = generatePath(currentPathType, center, steps, radius);
        setPath(path);
    }

    public void heightLogicTick(){
        if(isAboveMinHeight()){
            aboveMinHeightTick();
        }else{
            if(aboveMinHeightTicks > 0) aboveMinHeightTicks--;
        }
    }

    public boolean isAboveMinHeight(){
        int minY = mob.getWorld().getMinHeight() - 2;
        return mob.getY() > minY;
    }

    public boolean isAboveMinHeightTooLong(){
        return aboveMinHeightTicks > 100;
    }

    public int aboveMinHeightTicks = 0;

    @Override
    public void tick() {
        super.tick();
        attackHandler.tick();
        movementTick();

        heightLogicTick();

        if (target == null) {
            handleWandering();
        }

        if(!knockback.isZero()) knockback.multiply(knockbackResistance);
        pathTarget.tick();
    }

    public enum PathType {
        SPIRAL,
        CIRCLE,
        WAVE
    }
}

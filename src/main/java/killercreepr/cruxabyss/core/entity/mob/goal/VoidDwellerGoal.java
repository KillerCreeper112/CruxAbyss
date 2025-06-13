package killercreepr.cruxabyss.core.entity.mob.goal;

import io.papermc.paper.event.entity.EntityKnockbackEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.path.CruxGoalPathTargetMobGoal;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoidDwellerGoal extends CruxMobModeledGoal implements Listener, PathTargetMobGoal {
    protected final MobAttackHandler attackHandler;
    protected final PathTargetMobGoal pathTarget = new CruxGoalPathTargetMobGoal(this, 1.1D, false){
        @Override
        public void onCurrentNodeTick(GoalNode node) {
            Vector target = new Vector(node.x(), node.y(), node.z());
            Vector moveVec = target.subtract(mob.getLocation().toVector()).normalize().multiply(
                CruxAttribute.get(mob, CruxAttribute.FLYING_SPEED)
            );
            ((CraftMob) mob).getHandle().getMoveControl().setWantedPosition(node.x(), node.y(), node.z(),
                CruxAttribute.get(mob, CruxAttribute.FLYING_SPEED));
            /*try{
                mob.setVelocity(moveVec.add(knockback));
            }catch (Exception ignored){}*/

            try{
                /*Location loc = CruxLoc.lookAt(mob.getEyeLocation(), target.toLocation(mob.getWorld()));
                mob.setRotation(loc.getYaw(), loc.getPitch());*/

                ((CraftMob) mob).getHandle().getLookControl().setLookAt(node.x(), node.y(), node.z(), 180f, 180f);
                //mob.lookAt(node.x(), node.y(), node.z(), mob.getHeadRotationSpeed(), 180);
            }catch (Exception ignored){}
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
        ), List.of(
            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.FLYING_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .6D, CruxAttribute.Operation.MULTIPLY));
                    generateTargetPath(() ->{
                        if(target == null) return mob.getLocation();
                        return target.getLocation().add(0, target.getHeight()/2, 0);
                    });
                }

                @Override
                public int getCooldown() {
                    return CruxMath.random(70, 200);
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    if(hasValidPath()) return false;
                    return true;
                }

                public void generateTargetPath(Holder<Location> to) {
                    PathType currentPathType = PathType.pickRandomTargetType();
                    double radius = CruxMath.random(4D, 10D);
                    int steps = CruxMath.random(10, 16);
                    GoalPath path = generatePath(currentPathType, to.value(),
                        GoalNode.builder()
                            .buildDynamicDistance(to, pathNodeCloseEnoughDistance*2.5),
                        steps, radius);
                    setPath(path);
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
        return "attack_" + CruxMath.random(1,1);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityKnockback(EntityKnockbackEvent event) {
        if(!event.getEntity().equals(mob)) return;
        knockback = event.getKnockback().multiply(1.2D);
    }

    protected int wanderCooldown = 0;
    protected double knockbackResistance = 0.8D;

    protected Vector knockback = new Vector();

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
            PathType currentPathType = PathType.pickRandomWanderType();
            double radius = CruxMath.random(4.0, 10.0);
            int steps = CruxMath.random(10, 16);
            GoalPath path = generatePath(currentPathType, center, steps, radius);
            setPath(path);
        }
    }

    public void generateMoveToTarget(Location to) {
        PathType currentPathType = PathType.pickRandomWanderType();
        double radius = CruxMath.random(4.0, 10.0);
        int steps = CruxMath.random(10, 16);
        GoalPath path = generatePath(currentPathType, to, steps, radius);
        setPath(path);
    }

    public boolean isValidWanderLocation(Location loc){
        int minY = loc.getWorld().getMinHeight() - 5;
        return loc.getY() < minY;
    }

    public double pathNodeCloseEnoughDistance = 1.5D;
    public GoalPath generatePath(PathType type, Location origin, int steps, double radius){
        return generatePath(type, Holder.direct(origin), steps, radius);
    }

    public GoalPath generatePath(PathType type, Location origin, GoalNode endingNode, int steps, double radius){
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
            case HELIX -> {
                double spacing = radius / steps;
                for (int i = 0; i < steps; i++) {
                    double angle = i * Math.PI / 4;
                    double dx = radius * Math.cos(angle);
                    double dz = radius * Math.sin(angle);
                    double dy = i * spacing * 0.5;
                    points.add(GoalNode.distanceGoalNode(origin.clone().add(dx, dy, dz), pathNodeCloseEnoughDistance));
                }
            }
            case STRAFE -> {
                double angleStep = (2 * Math.PI) / steps;
                for (int i = 0; i < steps; i++) {
                    double angle = angleStep * i;
                    double dx = radius * Math.cos(angle);
                    double dz = radius * Math.sin(angle);
                    points.add(GoalNode.distanceGoalNode(origin.clone().add(dx, 0, dz), pathNodeCloseEnoughDistance));
                }
                points.add(endingNode);
            }
            case DIVEBOMB -> {
                for (int i = 0; i < steps; i++) {
                    double progress = i / (double) steps;
                    double angle = progress * 2 * Math.PI;
                    double spiralRadius = radius * (1 - progress);
                    double dx = spiralRadius * Math.cos(angle);
                    double dz = spiralRadius * Math.sin(angle);
                    double dy = radius - (radius * progress);
                    points.add(GoalNode.distanceGoalNode(origin.clone().add(dx, dy, dz), pathNodeCloseEnoughDistance));
                }
                points.add(endingNode);
            }
            case BOUNCE_AROUND -> {
                for (int i = 0; i < steps; i++) {
                    double dx = CruxMath.random(-radius, radius);
                    double dy = CruxMath.random(-radius / 2, radius / 2);
                    double dz = CruxMath.random(-radius, radius);
                    points.add(GoalNode.distanceGoalNode(origin.clone().add(dx, dy, dz), pathNodeCloseEnoughDistance));
                }
                points.add(endingNode);
            }
            case ORBIT_TARGET -> {
                for (int i = 0; i < steps; i++) {
                    double angle = 2 * Math.PI * i / steps;
                    double dx = radius * Math.cos(angle);
                    double dz = radius * Math.sin(angle);
                    points.add(GoalNode.distanceGoalNode(origin.clone().add(dx, 0, dz), pathNodeCloseEnoughDistance));
                }
                points.add(endingNode);
            }
            case ZIGZAG_TOWARD -> {
                Location originLoc = origin.clone();
                Vector toTarget = target.getLocation().toVector().subtract(originLoc.toVector()).normalize();
                Vector side = toTarget.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(radius / 2);
                for (int i = 0; i < steps; i++) {
                    double offset = (i % 2 == 0 ? 1 : -1);
                    Vector zigzagPoint = toTarget.clone().multiply(i * (radius / steps)).add(side.clone().multiply(offset));
                    Location pathLoc = originLoc.clone().add(zigzagPoint);
                    points.add(GoalNode.distanceGoalNode(pathLoc, pathNodeCloseEnoughDistance));
                }
                points.add(endingNode);
            }
            case RETREAT_AND_CHARGE ->{
                Location mobLoc = mob.getLocation();
                Vector awayVec = mobLoc.toVector().subtract(origin.toVector()).normalize().multiply(radius);

                // Step 1: Retreat points (going away from player)
                Location retreatStart = mobLoc.clone();
                for (int i = 0; i < steps / 3; i++) {
                    double fraction = i / (double)(steps / 3);
                    Vector retreatPoint = awayVec.clone().multiply(fraction);
                    points.add(GoalNode.distanceGoalNode(retreatStart.clone().add(retreatPoint), pathNodeCloseEnoughDistance));
                }

                // Step 2: Hover or pause (optional — flat movement)
                Location hoverLoc = retreatStart.clone().add(awayVec);
                for (int i = 0; i < steps / 6; i++) {
                    points.add(GoalNode.distanceGoalNode(hoverLoc.clone(), pathNodeCloseEnoughDistance));
                }

                // Step 3: Charge toward player
                Vector toPlayerVec = origin.toVector().subtract(hoverLoc.toVector()).normalize();
                for (int i = 0; i < steps / 2; i++) {
                    double fraction = i / (double)(steps / 2);
                    Vector chargePoint = toPlayerVec.clone().multiply(fraction * radius);
                    points.add(GoalNode.distanceGoalNode(hoverLoc.clone().add(chargePoint), pathNodeCloseEnoughDistance));
                }
                points.add(endingNode);
            }
        }

        if(points.isEmpty()) return null;
        return GoalPath.goalPath(points);
    }

    public GoalPath generatePath(PathType type, Holder<Location> originHolder, int steps, double radius) {
        return generatePath(type, originHolder.value(), GoalNode.dynamicDistanceGoalNode(originHolder, pathNodeCloseEnoughDistance), steps, radius);
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
        PathType currentPathType = PathType.pickRandomWanderType();
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

    protected int moveToCooldown = 0;
    @Override
    public void moveTo(@Nullable Location target, double speed) {
        if(target == null){
            super.moveTo(target, speed);
            return;
        }
        if(hasValidPath()) return;
        if(moveToCooldown > 0) return;
        moveToCooldown = CruxMath.random(80, 120);
        wanderCooldown = CruxMath.random(60, 100);
        generateMoveToTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        attackHandler.tick();
        //movementTick();

        if(moveToCooldown > 0) moveToCooldown--;

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
        WAVE,
        HELIX,

        STRAFE,
        DIVEBOMB,
        BOUNCE_AROUND,
        ORBIT_TARGET,
        ZIGZAG_TOWARD,
        RETREAT_AND_CHARGE,
        ;

        public static final List<PathType> TARGET = List.of(
            STRAFE,
            DIVEBOMB,
            BOUNCE_AROUND,
            ORBIT_TARGET,
            ZIGZAG_TOWARD,
            RETREAT_AND_CHARGE
        );
        public static final List<PathType> WANDER = List.of(
            SPIRAL,
            CIRCLE,
            WAVE,
            HELIX
        );

        public static PathType pickRandomTargetType(){
            return CruxCollection.getRandom(TARGET);
        }
        public static PathType pickRandomWanderType(){
            return CruxCollection.getRandom(WANDER);
        }
    }
}

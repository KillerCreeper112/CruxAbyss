package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxabyss.core.entity.mob.WarpedParticleBeam;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateWarpedLine;
import killercreepr.cruxpotions.api.entity.PotionHolder;
import killercreepr.cruxpotions.core.entity.memory.SimplePotionHolder;
import killercreepr.usurvive.core.entity.mob.goals.RangedAttackGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttack;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.potion.USurvivePotions;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SporeweaverGoal extends CruxMobModeledGoal implements Listener {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final MobAttackHandler attackHandler;
    public SporeweaverGoal(@NotNull Mob mob) {
        super(mob);
        rangedGoal = new RangedAttackGoal(mob, 1.9D, 5, 8, () -> getTarget());
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.BLOCK_SCULK_SPREAD,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.15f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.BLOCK_SCULK_BREAK,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.BLOCK_SCULK_SHRIEKER_BREAK,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.3f);
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
            new MobAttack() {
                @Override
                public String getAnimationID() {
                    return "prepare_spell_sporelink";
                }

                protected ShapeScheduler d;
                @Override
                public void onUse() {
                    MobAttack.super.onUse();
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(MobAttackHandler.STRONG_ATTACK_KEY, -0.7D, CruxAttribute.Operation.MULTIPLY));

                    d = ShapeScheduler.builder()
                        .locationTick(ctx ->{
                            new ParticleBuilder(Particle.ELECTRIC_SPARK)
                                .location(ctx.getLocation().toLocation(mob.getWorld()))
                                .offset(0, 0, 0)
                                .extra(0)
                                .spawn();
                        })
                        .shape(CreateWarpedLine.builder()
                            .start(() -> CruxLocation.location(getRightHandPos()))
                            .end(() -> CruxLocation.location(target.getLocation().add(0, target.getHeight()/2, 0)))
                            .spacing(0.5D)
                            .warpStrength(0.5D)
                            .tickOffset(NumberProvider.holder(() ->{
                                return (System.currentTimeMillis() / 50L) % 10000;
                            }))
                            .build())
                        .build();

                }

                @Override
                public void onTick() {
                    MobAttack.super.onTick();
                    if(attackHandler.getAttackTime() == 33){ //65 / 2 = 33 rounded up
                    }else if(attackHandler.getAttackTime() < 33){
                    }

                    d.scheduleAsync(0);
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    double distance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 4;
                    return getSquaredDistanceFromTarget() < (distance*distance);
                }
            }
        ));
    }

    public Map<String, List<LivingEntity>> splitLeftRight(Collection<LivingEntity> targets) {
        List<LivingEntity> left = new ArrayList<>();
        List<LivingEntity> right = new ArrayList<>();

        Vector forward = mob.getLocation().getDirection().normalize();
        Location mobLoc = mob.getLocation();

        for (LivingEntity entity : targets) {
            Vector toEntity = entity.getLocation().toVector().subtract(mobLoc.toVector()).normalize();
            double crossY = forward.clone().crossProduct(toEntity).getY();

            if (crossY >= 0) {
                left.add(entity);
            } else {
                right.add(entity);
            }
        }

        Map<String, List<LivingEntity>> result = new HashMap<>();
        result.put("left", left);
        result.put("right", right);
        return result;
    }

    public Collection<LivingEntity> getNearbySporelinkValidTargets(){
        return new GetEntityNear<>(LivingEntity.class)
            .center(mob)
            .range(16D)
            .amount(8)
            .filter(e ->{
                if(!isValidNaturalTarget(e)) return false;
                PotionHolder holder = EntityMemory.getDataHolder(e, SimplePotionHolder.class);
                if(holder != null && holder.hasPotion(USurvivePotions.SPORELINK)) return false;
                return true;
            })
            .operation(GetNear.Operation.NEAREST)
            .find();
    }

    public Location getLeftHandPos(){
        return getModel().getBone("left_hand_pos").get().getLocation();
    }
    public Location getRightHandPos(){
        return getModel().getBone("right_hand_pos").get().getLocation();
    }
    public Location getHeadTopPos(){
        return getModel().getBone("head_top_pos").get().getLocation();
    }

    public void spawnWarpedParticles(World world, Location start, Location end, int steps) {
        Vector dir = end.toVector().subtract(start.toVector());
        double length = dir.length();
        dir.normalize();

        Vector up = new Vector(0, 1, 0);
        Vector side = dir.clone().crossProduct(up).normalize(); // perpendicular sideways vector

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vector point = start.toVector().clone().add(dir.clone().multiply(length * t));

            // Add warping using sine or noise
            double warpStrength = 0.5;
            double wobble = Math.sin(t * Math.PI * 4) * warpStrength;
            point.add(side.clone().multiply(wobble));

            // Add a slight vertical variation for more "spore-like" movement
            double verticalWobble = Math.cos(t * Math.PI * 3) * warpStrength * 0.5;
            point.setY(point.getY() + verticalWobble);

            world.spawnParticle(Particle.DUST, point.toLocation(world), 0, 0, 0, 0, 0,
                new Particle.DustOptions(Color.GREEN, .8f));
        }
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

    public Location getOozePos(){
        return getModel().getBone("ooze_pos").get().getLocation();
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

    public void moveAwayFromTarget(Entity target, double speed){

    }

    protected final RangedAttackGoal rangedGoal;
    @Override
    public void tick() {
        super.tick();
        attackHandler.tick();
        movementTick();
        rangedGoal.tick();
    }
}

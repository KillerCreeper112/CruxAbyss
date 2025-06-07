package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.persistence.CruxPersist;
import killercreepr.crux.core.util.*;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
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
import killercreepr.cruxpotions.core.potions.inflictor.EntityInflictor;
import killercreepr.usurvive.core.entity.mob.goals.RangedAttackGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttack;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import killercreepr.usurvive.core.entity.mob.goals.data.StrongMobAttack;
import killercreepr.usurvive.core.potion.USurvivePotions;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

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
        ), List.of(
            new MobAttack() {
                @Override
                public String getAnimationID() {
                    return "prepare_spell_sporelink";
                }

                @Override
                public void onUse() {
                    MobAttack.super.onUse();
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(MobAttackHandler.STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public void onFinish() {
                    MobAttack.super.onFinish();
                    lastSporelink = System.currentTimeMillis();
                }

                @Override
                public void onTick() {
                    MobAttack.super.onTick();
                    if(attackHandler.getAttackTime() == 48){
                        sporeLinkComplete();
                        return;
                    }
                    if(attackHandler.getAttackTime() > 48) return;

                    if(attackHandler.getAttackTime() >= 33){
                        sporeLinkingTick();
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    if(CruxMath.hasOccurredWithin(lastSporelink, 600)) return false;
                    if(CruxMath.testChance(25)) return false;
                    double minDistance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 3;
                    double maxDistance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 7;
                    double distance = getSquaredDistanceFromTargetHitbox();
                    return distance > (minDistance*minDistance) && distance < (maxDistance*maxDistance);
                }
            },

            new MobAttack() {
                @Override
                public String getAnimationID() {
                    return "summon_sporepod";
                }

                @Override
                public void onUse() {
                    MobAttack.super.onUse();
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(MobAttackHandler.STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public void onFinish() {
                    MobAttack.super.onFinish();
                    lastSporepod = System.currentTimeMillis();
                }

                @Override
                public void onTick() {
                    MobAttack.super.onTick();
                    if(attackHandler.getAttackTime() == 8){
                        summonSporepod();
                        return;
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    if(!CruxMath.hasOccurredWithin(lastSporelink, 200)) return false;
                    if(CruxMath.hasOccurredWithin(lastSporepod, 300)) return false;
                    if(CruxMath.testChance(10)) return false;
                    double minDistance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 3;
                    double maxDistance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 7;
                    double distance = getSquaredDistanceFromTargetHitbox();
                    return distance > (minDistance*minDistance) && distance < (maxDistance*maxDistance);
                }
            },

            new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.9D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .75D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .7D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 10;
                }

                @Override
                public boolean canUseAttack() {
                    double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 1.75D;
                    return getSquaredDistanceFromTarget() <= (range*range);
                }
            },
            new StrongMobAttack(2) {
                @Override
                public void onUse() {
                    CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -0.75D, CruxAttribute.Operation.MULTIPLY));

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 0.4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .5D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .5D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public void onTick() {
                    super.onTick();
                    if(attackHandler.getAttackTime() == attackHandler.getHitAt()){
                        onHit();
                    }
                }

                public void onHit(){
                    Location head = getHeadTopPos();
                    new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                        .location(head)
                        .offset(.4, .4, .4)
                        .count(CruxMath.random(9,12))
                        .extra(.2)
                        .colorTransition(Color.fromRGB(0xC0FF00), Color.fromRGB(0xE8BE3E),
                            CruxMath.random(0.7f, 1f))
                        .spawn()
                    ;
                    CreateSound.sound(Sound.ENTITY_PUFFER_FISH_BLOW_OUT, 1.3f).playAt(mob);

                    mob.getWorld().getNearbyEntities(CruxedBoundingBox.boundingBox(head, .9, .9, .9), e ->{
                        return  e instanceof LivingEntity ll && isValidNaturalTarget(ll);
                    }).forEach(hit ->{
                        attack(hit);
                        if(!(hit instanceof LivingEntity h)) return;
                        h.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                    });
                }

                @Override
                public boolean canUseAttack() {
                    double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 1.5D;
                    return getSquaredDistanceFromTargetHitbox() <= (range*range);
                }

                @Override
                public int getHitTime() {
                    return 22;
                }
            }
        ));
    }

    protected long lastSporelink;
    protected long lastSporepod;
    protected final Map<UUID, SporeLinkedEntity> sporeLinked = new HashMap<>();

    public boolean canSpawnSporepod(Location loc){
        Block b = loc.getBlock();
        if(b.isSolid()) return false;
        if(b.getRelative(BlockFace.UP).isSolid()) return false;
        return true;
    }

    public Entity summonSporepod(){
        Location l = mob.getEyeLocation();
        l.setPitch(0f);
        Location spawn = CruxLoc.shift(l, .5, 0, 0);

        if(!canSpawnSporepod(spawn)) return null;

        Entity sporepod = AbyssMob.SPOREPOD.spawn(spawn, e ->{
            Vector vel = l.getDirection().normalize().setY(1.2f);
            e.setVelocity(vel);
            CruxPersist.OWNER.set(e, mob.getUniqueId());
        });

        CreateSound.sound(Sound.ENTITY_EVOKER_CAST_SPELL, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 2f)
            .playAt(mob);

        new ParticleBuilder(Particle.FLASH)
            .location(spawn)
            .extra(.2)
            .count(1)
            .spawn();
        new ParticleBuilder(Particle.ELECTRIC_SPARK)
            .location(spawn)
            .offset(.3, .3, .3)
            .extra(.2)
            .count(CruxMath.random(6, 9))
            .spawn();

        PotionHolder holder = EntityMemory.getOrCreateDataHolder(sporepod, SimplePotionHolder.class, SimplePotionHolder::new);
        if(holder == null) return sporepod;
        holder.addPotion(USurvivePotions.SPORELINK.create(sporepod, CruxMath.random(1200, 2400),
            (int) CruxMath.randomSkewed(0f, 1.3f, 0.4f)));

        return sporepod;
    }

    public void sporeLinkComplete(){
        getNearbySporelinkValidTargets().forEach(e ->{
            SporeLinkedEntity linked = sporeLinked.get(e.getUniqueId());
            if(linked == null){
                PotionHolder holder = EntityMemory.getOrCreateDataHolder(e, SimplePotionHolder.class);
                if(holder == null) return;
                holder.addPotion(USurvivePotions.SPORELINK.create(e, 100, 0, new EntityInflictor(mob)));
                return;
            }
            long ticksGotted = (System.currentTimeMillis() - linked.time) / 50L;
            float progress = (ticksGotted / 30f) + 0.1f;

            int duration = (int) (1800 * progress);

            PotionHolder holder = EntityMemory.getOrCreateDataHolder(e, SimplePotionHolder.class);
            if(holder == null) return;
            holder.addPotion(USurvivePotions.SPORELINK.create(e, duration, 0, new EntityInflictor(mob)));

            ShapeScheduler.builder()
                .locationTick(ctx ->{
                    new ParticleBuilder(Particle.ELECTRIC_SPARK)
                        .location(ctx.getLocation().toLocation(mob.getWorld()))
                        .offset(0, 0, 0)
                        .extra(0)
                        .spawn();
                })
                .shape(CreateWarpedLine.builder()
                    .start(() -> CruxLocation.location(getLeftHandPos()))
                    .end(() -> CruxLocation.location(e.getLocation().add(0, e.getHeight()/2, 0)))
                    .spacing(1D)
                    .warpStrength(0.5D)
                    .tickOffset(NumberProvider.holder(() ->{
                        return (System.currentTimeMillis() / 50L) % 10000;
                    }))
                    .build())
                .build().scheduleAsync(0);
        });
        sporeLinked.clear();

        new ParticleBuilder(Particle.ELECTRIC_SPARK)
            .location(getLeftHandPos())
            .offset(.3, .3, .3)
            .extra(.2)
            .count(CruxMath.random(6, 9))
            .spawn();
    }

    public void sporeLinkingTick(){
        if(tick % 6 == 0){
            sporeLinked.values().removeIf(e -> !isValidSporeLinkEntity(e.entity()));

            getNearbySporelinkValidTargets().forEach(hit ->{
                if(sporeLinked.containsKey(hit.getUniqueId())) return;
                sporeLinked.put(hit.getUniqueId(),new SporeLinkedEntity(hit, System.currentTimeMillis()));
            });
        }

        if(tick % 3 == 0){
            sporeLinked.values().removeIf(e -> !isValidSporeLinkEntity(e.entity()));
            Map<String, List<SporeLinkedEntity>> split = splitLeftRight(sporeLinked.values(), linked ->{
                Entity e = linked.entity;
                PotionHolder holder = EntityMemory.getOrCreateDataHolder(e, SimplePotionHolder.class);
                if(holder == null) return;
                holder.addPotion(USurvivePotions.SPORELINK.create(e, 100, 0, new EntityInflictor(mob)));
            });
            Collection<SporeLinkedEntity> left = split.get("left");
            Collection<SporeLinkedEntity> right = split.get("right");

            if(!left.isEmpty()){
                var target = CruxCollection.getFirst(left).entity;
                ShapeScheduler.builder()
                    .locationTick(ctx ->{
                        new ParticleBuilder(Particle.ELECTRIC_SPARK)
                            .location(ctx.getLocation().toLocation(mob.getWorld()))
                            .offset(0, 0, 0)
                            .extra(0)
                            .spawn();
                    })
                    .shape(CreateWarpedLine.builder()
                        .start(() -> CruxLocation.location(getLeftHandPos()))
                        .end(() -> CruxLocation.location(target.getLocation().add(0, target.getHeight()/2, 0)))
                        .spacing(1D)
                        .warpStrength(0.5D)
                        .tickOffset(NumberProvider.holder(() ->{
                            return (System.currentTimeMillis() / 50L) % 10000;
                        }))
                        .build())
                    .build().scheduleAsync(0);
            }
            if(!right.isEmpty()){
                var target = CruxCollection.getFirst(right).entity;
                ShapeScheduler.builder()
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
                        .spacing(1D)
                        .warpStrength(0.5D)
                        .tickOffset(NumberProvider.holder(() ->{
                            return (System.currentTimeMillis() / 50L) % 10000;
                        }))
                        .build())
                    .build().scheduleAsync(0);
            }
        }
    }

    public static final double sporeLinkDistance = 24D;
    public boolean isValidSporeLinkEntity(LivingEntity e){
        if(!isValidNaturalTarget(e)) return false;
        if(!CruxEntityUtil.isValid(e)) return false;
        if(e.getLocation().distanceSquared(mob.getLocation()) > (sporeLinkDistance * sporeLinkDistance)) return false;
        return true;
    }

    public Map<String, List<SporeLinkedEntity>> splitLeftRight(Collection<SporeLinkedEntity> targets, Consumer<SporeLinkedEntity> consumer) {
        List<SporeLinkedEntity> left = new ArrayList<>();
        List<SporeLinkedEntity> right = new ArrayList<>();

        Vector forward = mob.getLocation().getDirection().normalize();
        Location mobLoc = mob.getLocation();

        for (SporeLinkedEntity entity : targets) {
            if(consumer != null) consumer.accept(entity);
            Vector toEntity = entity.entity.getLocation().toVector().subtract(mobLoc.toVector()).normalize();
            double crossY = forward.clone().crossProduct(toEntity).getY();

            if (crossY >= 0) {
                left.add(entity);
            } else {
                right.add(entity);
            }
        }

        Map<String, List<SporeLinkedEntity>> result = new HashMap<>();
        result.put("left", left);
        result.put("right", right);
        return result;
    }

    public Collection<LivingEntity> getNearbySporelinkValidTargets(){
        return new GetEntityNear<>(LivingEntity.class)
            .center(mob)
            .range(sporeLinkDistance)
            .amount(8)
            .filter(e ->{
                if(!isValidNaturalTarget(e)) return false;
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

    protected int tick = 0;
    protected final RangedAttackGoal rangedGoal;
    @Override
    public void tick() {
        tick++;
        super.tick();
        attackHandler.tick();
        movementTick();
        rangedGoal.tick();
    }

    public record SporeLinkedEntity(LivingEntity entity, long time){

    }
}

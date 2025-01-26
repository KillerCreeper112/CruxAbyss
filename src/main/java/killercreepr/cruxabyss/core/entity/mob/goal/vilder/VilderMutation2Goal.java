package killercreepr.cruxabyss.core.entity.mob.goal.vilder;

import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.core.entity.ability.ShockWave;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateCircle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VilderMutation2Goal extends VilderGoal{
    public VilderMutation2Goal(@NotNull Mob mob) {
        super(mob);
    }

    @Override
    public int generateStrongAttackID(){
        return CruxMath.random(1,4);
    }

    @Override
    public String generateAttackAnimationID() {
        return "attack_" + CruxMath.random(1,3);
    }

    public Location getLeftArmTopPos(){
        return getModel().getBone("left_arm_top_pos").orElse(null).getLocation();
    }

    public BoundingBox getLeftArmTopBox(Location pos){
        double range = .7D;
        return BoundingBox.of(pos, range, range, range);
    }

    @Override
    public int getHitAtTime(int attackID) {
        return switch(attackID){
            case 2 -> 4;
            case 3 -> 7;
            case 4 -> 12;
            default -> 5;
        };
    }

    @Override
    public void onUseStrongAttack(int attackID) {
        switch(attackID){
            case 1 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1D, CruxAttribute.Operation.MULTIPLY));
            }
            case 2 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .8D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -2D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
            }
            case 3 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1.5D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
            }
            case 4 ->{
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, 1.2D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -3D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                    CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .15D, CruxAttribute.Operation.MULTIPLY));
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
    public void onHitAtTime() {
        if(currentAttackID == 4){
            Location shockLoc = mob.getLocation();
            shockLoc.setPitch(0f);
            shockLoc = CruxLoc.shift(shockLoc, .7, 0, 0);
            LocationHolder shockLocHolder = DynamicLocation.createStatic(shockLoc);

            ShockWave shockWave = new ShockWave(6D, 0D, 1D);
            ShapeScheduler scheduler = ShapeScheduler.builder()
                .locationTick(ctx ->{
                    Location loc = ctx.getLocation().toLocation(mob.getWorld());
                    loc.getWorld().spawnParticle(Particle.CRIT, loc, 0);
                })
                .tick(ctx ->{
                    double radius = shockWave.value().doubleValue();

                    double damage = EntityDamager.getDamage(mob);
                    double kb = CruxAttribute.get(mob, CruxAttribute.ATTACK_KNOCKBACK);
                    double kbUp = CruxAttribute.get(mob, CruxAttribute.ATTACK_KNOCKBACK_UP);

                    new GetEntityNear<>(shockLocHolder, LivingEntity.class)
                        .range(radius)
                        .filter(e ->{
                            if(!(isValidHitTarget(e) && isValidNaturalTarget(e))) return false;
                            if(hasHitWithin(e, 5)) return false;
                            hit(e);
                            return true;
                        })
                        .find().forEach(hit ->{
                            EntityDamager.entityDamager(hit, mob)
                                .attack(damage, kb, kbUp, shockLocHolder.value());
                        });
                })
                .endTask(lastHit::clear)
                .shape(
                    CreateCircle.builder()
                        .radius(shockWave)
                        .center(CruxLocation.location(shockLoc))
                        .type(CreateCircle.Type.HOLLOW)
                        .build()
                )
                .build();

            shockWave.start(scheduler);
            return;
        }
        super.onHitAtTime();
    }

    @Override
    public void onCombatUsingStrongAttackTick() {
        super.onCombatUsingStrongAttackTick();
        switch (currentAttackID){
            case 4 -> {
                int time = getPlayingAnimationTimeTicks("attack_strong_" + currentAttackID);
                if(time < 16) return;
                Location pos = getLeftArmTopPos();
                BoundingBox box = getLeftArmTopBox(pos);
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

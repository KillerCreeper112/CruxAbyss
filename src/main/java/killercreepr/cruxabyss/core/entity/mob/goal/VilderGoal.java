package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.crux.core.persistence.CruxPersistence;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.safezone.AbyssSafeZoneData;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

//mob npcs which will not leave the safe zone unless they are targeting something
//if they get too far away from the safe zone, they will attempt to go back to it
//
public class VilderGoal extends CruxMobModeledGoal implements Listener {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected StoredStructure cachedSafeZone;
    public VilderGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VILLAGER_AMBIENT, .6f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VILLAGER_HURT, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_VILLAGER_DEATH, .6f);
            }
        });
    }

    protected int strongAttackCooldown;
    public boolean canUseStrongAttack(){
        return strongAttackCooldown < 1 && !isUsingStrongAttack();
    }

    public boolean isUsingStrongAttack(){
        return maxAttackTime > 0;
    }

    public void combatTick(){
        if(!isUsingStrongAttack()){
            if(strongAttackCooldown > 0){
                strongAttackCooldown--;
            }
            return;
        }
        attackTime++;
        if(hitAt == attackTime){
            this.attemptAttack();
            hitAt = 0;
        }
        if(attackTime >= maxAttackTime){
            maxAttackTime = 0;
            hitAt = 0;
            CruxAttribute.removeModifier(mob, CruxAttribute.MOVEMENT_SPEED, Crux.key("strong_attack"));
            CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_DAMAGE, Crux.key("strong_attack"));
            CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_KNOCKBACK, Crux.key("strong_attack"));
            CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_AOE, Crux.key("strong_attack"));
            CruxAttribute.removeModifier(mob, CruxAttribute.ATTACK_RANGE, Crux.key("strong_attack"));
        }
    }

    public int useStrongAttack(){
        strongAttackCooldown = CruxMath.random(20, 50);
        int atck = CruxMath.random(1,2);
        String id = "attack_strong_" + atck;
        playAnimation(id, true);
        this.maxAttackTime = (int) Math.ceil(getAnimationLengthTicks(id) / 2f);
        this.attackTime = 0;
        //1 = 9, 2 = 8;
        this.hitAt = atck == 1 ? 5 : 4;
        CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
            CruxAttributeModifier.modifier(Crux.key("strong_attack"), -5D, CruxAttribute.Operation.MULTIPLY));
        if(hitAt == 1){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .5D, CruxAttribute.Operation.MULTIPLY));
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .2D, CruxAttribute.Operation.MULTIPLY));
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .1D, CruxAttribute.Operation.MULTIPLY));
        }else if(hitAt == 2){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .3D, CruxAttribute.Operation.MULTIPLY));
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .15D, CruxAttribute.Operation.MULTIPLY));
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .1D, CruxAttribute.Operation.MULTIPLY));
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                CruxAttributeModifier.modifier(Crux.key("strong_attack"), .1D, CruxAttribute.Operation.MULTIPLY));
        }
        return atck;
    }

    protected int attackTime = 0;
    protected int maxAttackTime = 0;
    protected int hitAt = 0;
    @Override
    public boolean preAttemptAttack() {
        if(canUseStrongAttack()){
            useStrongAttack();
            return false;
        }
        if(isUsingStrongAttack() && hitAt != attackTime) return false;
        return super.preAttemptAttack();
    }

    @Override
    protected void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(isUsingStrongAttack()) return;
        playAnimation("attack_" + CruxMath.random(1,3), true);
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    @Override
    public double getFindTargetRange() {
        return 8D;
    }

    //prevent mob from leaving safe zone if they are not permitted to.
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPathfind(EntityPathfindEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(canLeaveSafeZone()) return;
        StoredStructure safeZone = getStoredSafeZone();
        if(safeZone == null) return;
        Location loc = event.getLoc();
        BoundingBox box = safeZone.getOrDefault(StoredStructureComponents.OUTER_BOX, safeZone.getBoundingBox());
        if(box.contains(loc.getX(), loc.getY(), loc.getZ())) return;
        event.setCancelled(true);
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(moveSpeed);
        if(moveSpeed > 0D){
            swimmer.tick();
        }
    }

    @Override
    public void tick() {
        super.tick();
        combatTick();
        movementTick();
        if(!hasSafeZone()){
            findSafeZoneTick();
            return;
        }

        if(isInSafeZone()){
            inSafeZoneTick();
        }else outOfSafeZoneTick();
    }

    protected int timeOutOfSafeZone;
    protected int findSafeZoneCooldown = CruxMath.random(10, 20);
    protected long returningToSafeZone;
    public void findSafeZoneTick(){
        if(findSafeZoneCooldown > 0){
            findSafeZoneCooldown--;
            return;
        }
        findSafeZoneCooldown = CruxMath.random(40, 160);
        StoredStructure safeZone = findSafeZone(mob.getLocation());
        if(safeZone == null) return;
        CruxPosition pos = safeZone.getPosition();
        setSafeZoneLocation(BlockPos.at(pos.blockX(), pos.blockY(), pos.blockZ()));
    }

    public StoredStructure findSafeZone(Location at){
        CruxWorld world = CruxCore.core().worldManager().getWorld(at.getWorld().getUID());
        if(world == null) return null;
        StructureWorldModule module = world.getModule(StructureWorldModule.class);
        if(module == null) return null;

        Vector vec = at.toVector();
        StoredStructure stored = CruxCollection.getFirst(module.getStored(check ->{
            BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
            return box.contains(vec);
        }));

        if(stored == null) return null;
        if(!stored.has(AbyssComponents.ABYSS_SAFE_ZONE_DATA)) return null;
        return stored;
    }

    public void inSafeZoneTick(){
        timeOutOfSafeZone = 0;
        if(isMovingToSafeZone()){
            mob.getPathfinder().stopPathfinding();
        }
    }

    public boolean isMovingToSafeZone(){
        Pathfinder.PathResult result = mob.getPathfinder().getCurrentPath();
        if(result == null) return false;
        Location finalPoint = result.getFinalPoint();
        if(finalPoint == null) return false;
        CruxPosition safeZonePos = cachedSafeZone == null ? getSafeZoneLocation() : cachedSafeZone.getPosition();
        if(safeZonePos == null) return false;
        return safeZonePos.distanceSquared(CruxPosition.precise(finalPoint)) < (1.3D*1.3D);
    }

    public void outOfSafeZoneTick(){
        timeOutOfSafeZone++;
        if(!shouldFallBackToSafeZone()) return;
        else returningToSafeZone = -1;
        setTarget(null);
        if(returningToSafeZone == -1) returningToSafeZone = System.currentTimeMillis();
        returnToSafeZoneTick();
    }

    public void returnToSafeZoneTick(){
        CruxPosition pos = cachedSafeZone == null ? getSafeZoneLocation() : cachedSafeZone.getPosition();
        if(pos == null) return;
        moveTo(pos.toLocation(mob.getWorld()), 1.3D);
    }

    public boolean canLeaveSafeZone(){
        return target != null;
    }

    public boolean shouldFallBackToSafeZone(){
        if(target == null) return true;
        if(CruxMath.hasOccurredWithin(returningToSafeZone, 80)){
            if(target != null){
                //if the mob is out of the safe zone too long and has a target,
                //then it might as well stay out of the safe zone to hopefully
                //kill its target
                if(timeOutOfSafeZone > 600) return true;
                setTarget(null);
            }
            return true;
        }
        double distance = getDistanceSquaredFromSafeZone();
        double max = getFallBackSafeZoneDistance();
        return distance >= (max*max);
    }

    public double getFallBackSafeZoneDistance(){
        return 82D;//64
    }

    /**
     * @return -1 if mob does not have a set safe zone
     */
    public double getDistanceSquaredFromSafeZone(){
        CruxPosition pos = cachedSafeZone == null ? getSafeZoneLocation() : cachedSafeZone.getPosition();
        if(pos == null) return -1D;
        return pos.distanceSquared(CruxPosition.precise(mob.getLocation()));
    }

    public boolean isInSafeZone(){
        StoredStructure structure = getStoredSafeZone();
        if(structure == null) return false;
        BoundingBox box = structure.getOrDefault(StoredStructureComponents.OUTER_BOX, structure.getBoundingBox());
        return box.contains(mob.getLocation().toVector());
    }

    public CruxWorld getCruxWorld(){
        return CruxCore.core().worldManager().getWorld(mob.getWorld().getUID());
    }

    public boolean hasSafeZone(){
        return getStoredSafeZone() != null;
    }

    public StoredStructure getStoredSafeZone(){
        if(cachedSafeZone != null) return cachedSafeZone;
        CruxPosition pos = getSafeZoneLocation();
        if(pos == null) return null;

        CruxWorld world = getCruxWorld();
        if(world == null) return null;
        StructureWorldModule module = world.getModule(StructureWorldModule.class);
        if(module == null) return null;

        Vector vec = pos.toVector();
        StoredStructure stored = CruxCollection.getFirst(module.getStored(check ->{
            BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
            return box.contains(vec);
        }));
        if(stored == null) return null;
        AbyssSafeZoneData safeZone = stored.get(AbyssComponents.ABYSS_SAFE_ZONE_DATA);
        if(safeZone == null) return null;
        return cachedSafeZone = stored;
    }

    public void setSafeZoneLocation(BlockPos pos){
        CruxTag.set(mob, "safe_zone_location", CruxPersistence.BLOCK_POS, pos);
        cachedSafeZone = null;
    }

    public BlockPos getSafeZoneLocation(){
        return CruxTag.get(mob, "safe_zone_location", CruxPersistence.BLOCK_POS, null);
    }
}

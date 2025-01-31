package killercreepr.cruxabyss.core.world.abyss.event;

import com.destroystokyo.paper.entity.ai.Goal;
import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.boss.CreateBossBar;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.entity.mob.goal.OutpostTargeterGoal;
import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxabyss.core.block.active.ActiveAbyssConquestNode;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import killercreepr.cruxblocks.core.component.PlacedCustomBlocksComponent;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.mob.goal.CruxGoalBase;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.BoundingBox;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

public class OutpostInvasionEvent implements WorldEvent, Listener {
    protected final CruxWorld world;
    protected final StoredStructure targetStructure;
    protected final StructureWorldModule structures;
    protected final NaturalEntitySpawnGroup spawns;
    protected final int maxWave;
    protected final float maxCaptureTime;
    protected float captureTime;
    protected int currentWave;
    protected int totalEntitiesSpawned;
    protected int totalEntitiesSpawnedThisWave;

    protected final Map<UUID, Reference<Entity>> spawnedEntities = new HashMap<>();
    protected long lastSpawnedWave;

    protected final Map<UUID, Float> participants = new HashMap<>();

    public OutpostInvasionEvent(CruxWorld world, StoredStructure targetStructure, NaturalEntitySpawnGroup spawns, int maxWave, float maxCaptureTime) {
        this.world = world;
        this.structures = world.getModule(StructureWorldModule.class);
        this.targetStructure = targetStructure;
        this.spawns = spawns;
        this.maxWave = maxWave;
        this.maxCaptureTime = maxCaptureTime;

        this.normalTick = () ->{
            Collection<Entity> withinWalls = getSpawnedWithinWalls();
            if(withinWalls.isEmpty()){
                attackingOutpost = false;
                captureTime = Math.max(0f, captureTime - CruxMath.random(20f, 40f));
                return;
            }
            attackingOutpost = true;
            captureTime += Math.min(
                calculateCaptureTickAmount(withinWalls, targetStructure.getPosition()), maxCaptureTime
            );

            if(captureTime >= maxCaptureTime){
                capturedTick();
            }
        };
    }

    public Map<UUID, Reference<Entity>> updateSpawnedEntities(){
        spawnedEntities.values().removeIf(d -> !CruxEntityUtil.isValid(d.get()));
        return spawnedEntities;
    }

    public boolean hasSpawned(Entity e){
        return spawnedEntities.containsKey(e.getUniqueId());
    }

    public Collection<Entity> getSpawnedEntities(){
        Collection<Entity> list = new HashSet<>();
        spawnedEntities.values().forEach(e -> {
            Entity entity = e.get();
            if(entity != null && entity.isValid()) list.add(entity);
        });
        return list;
    }

    protected UPlayer cachedOwner;
    public UPlayer getOwner(){
        if(cachedOwner != null) return cachedOwner;
        AbyssOutpostData data = targetStructure.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(data == null || data.owner == null) return null;
        return cachedOwner = USurvivePlugin.inst().getPlayerManager().getPlayer(data.owner);
    }

    public boolean isOwner(Entity e){
        if(cachedOwner != null) return cachedOwner.getUUID().equals(e.getUniqueId());
        AbyssOutpostData data = targetStructure.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        return data != null && e.getUniqueId().equals(data.owner);
    }

    public Player getOnlineOwner(){
        AbyssOutpostData data = targetStructure.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(data== null) return null;
        return Bukkit.getPlayer(data.owner);
    }

    public Collection<Player> getOnlineOutpostMembers(){
        AbyssOutpostData data = targetStructure.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(data== null) return Set.of();
        Collection<Player> list = new HashSet<>();
        data.getMembers().forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null) list.add(p);
        });
        return list;
    }

    public Collection<Player> getOnlineOutpostMembersAndOwner(){
        AbyssOutpostData data = targetStructure.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(data== null) return Set.of();
        Collection<Player> list = new HashSet<>();
        data.getMembersAndOwner().forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null) list.add(p);
        });
        return list;
    }

    @Override
    public void started() {
        Crux.getServer().getPluginManager().registerEvents(this, Crux.getMainPlugin());
        Bukkit.broadcastMessage("invasion started!");
        MergedTagContainer tags = TagContainer.merged()
            .hook(targetStructure.getPosition());
        getOnlineOutpostMembersAndOwner().forEach(p ->{
            if(isOwner(p)){
                Lang.ABYSS_OUTPOST_INVASION_START_OWNER.use(p, tags);
                return;
            }
            Lang.ABYSS_OUTPOST_INVASION_START_MEMBER.use(p, tags);
        });
    }

    @Override
    public void stopped() {
        HandlerList.unregisterAll(this);
        Bukkit.broadcastMessage("invasion stopped. thats enough");
    }

    public float calculateCaptureTickAmount(Collection<Entity> entities, CruxPosition center) {
        float amount = 0f;
        float maxContribution = 10f; // Maximum contribution from an entity
        float falloffRate = 10f;     // Adjust for smoother drop-off (higher = slower falloff)

        for (Entity e : entities) {
            float distanceSquared = (float) center.distanceSquared(CruxPosition.precise(e.getLocation()));
            if (distanceSquared == 0) {
                // Assume maximum contribution if the entity is exactly at the center
                amount += maxContribution;
            }

            // Use square root for smoother drop-off
            float distance = (float) Math.sqrt(distanceSquared);

            // Calculate contribution with smoother falloff
            float addedTime = maxContribution / (1 + distance / falloffRate);

            amount += addedTime;
        }

        return amount;
    }

    public Collection<Entity> getSpawnedWithinWalls(){
        BoundingBox box = getTargetStructureBox();
        return world.toBukkitWorld().getNearbyEntities(box, this::hasSpawned);
    }

    public Collection<Entity> getNearbyEntities(){
        BoundingBox box = getNearbyBox();
        return world.toBukkitWorld().getNearbyEntities(box);
    }

    public Collection<Entity> getNearbyEntities(Predicate<Entity> filter){
        BoundingBox box = getNearbyBox();
        return world.toBukkitWorld().getNearbyEntities(box, filter);
    }

    public void onDefeated(){
        MergedTagContainer tags = TagContainer.merged()
            .hook(targetStructure.getPosition());
        Player owner = getOnlineOwner();
        if(owner != null){
            Lang.ABYSS_OUTPOST_INVASION_DEFEATED.use(owner, tags);
        }
        getNearbyEntities(e -> e instanceof Player).forEach(p ->{
            if(p.equals(owner)) return;
            Lang.ABYSS_OUTPOST_INVASION_DEFEATED.use(p, tags);
        });
    }

    protected int tick = 0;
    protected boolean attackingOutpost;
    protected boolean defeated = false;
    @Override
    public void tick(){
        tick++;
        if(tick % 20 != 0) return;

        if(hasBeenDefeated()){
            if(defeated) return;
            defeated = true;
            onDefeated();
            return;
        }

        updateSpawnedEntities();
        if(shouldNextWave()){
            nextWave();
        }
        Crux.scheduler().runTask(normalTick);
        tickPlayers();
    }
    protected boolean captured = false;

    public void onCaptured(){
        Bukkit.broadcastMessage("Outpost has been captured!");
        ActiveAbyssConquestNode node = getConquestNode();
        if(node == null){
            Crux.log(Level.SEVERE, "OutpostInvasionEvent: " + targetStructure.getPosition() + " has no conquest node! " + world.getName());
            return;
        }
        ActiveStructure active = getActive();
        ActiveAbyssOutpost outpost = active.get(AbyssComponents.ACTIVE_ABYSS_OUTPOST);
        outpost.invasion();
        node.update();

        getSpawnedEntities().forEach(e ->{
            if(!(e instanceof Mob mob)) return;
            if(!(CruxGoalUtil.getGoal(mob, Goal.class, CruxGoalBase.defaultKey()) instanceof PathTargetMobGoal goal)){
                return;
            }
            goal.setPath(null);
            if(goal instanceof OutpostTargeterGoal g) g.setOutpostTarget(null);
        });

        MergedTagContainer tags = TagContainer.merged()
            .hook(targetStructure.getPosition());
        Player owner = getOnlineOwner();
        if(owner != null){
            Lang.ABYSS_OUTPOST_INVASION_OVERTOOK.use(owner, tags);
        }
        getNearbyEntities(e -> e instanceof Player).forEach(p ->{
            if(p.equals(owner)) return;
            Lang.ABYSS_OUTPOST_INVASION_OVERTOOK.use(p, tags);
        });
    }

    public void capturedTick(){
        if(captured) return;
        captured = true;
        onCaptured();
    }

    public final Runnable normalTick;

    public boolean isOutpostBeingAttacked(){
        return attackingOutpost;
    }

    public BoundingBox getNearbyBox(){
        return getTargetStructureBox().clone().expand(16D);
    }

    public void tickPlayers(){
        World world = this.world.toBukkitWorld();
        BoundingBox box = getNearbyBox();
        Crux.scheduler().runTask(() ->{
            for(Entity e : world.getNearbyEntities(box, e -> e instanceof Player)){
                Player p = (Player) e;
                tickPlayer(p);
            }
        });
    }

    public void tickPlayer(Player p){
        MergedTagContainer tags = TagContainer.merged(
            Tag.string("spawned_entities", (args, ctx) -> spawnedEntities.size() + ""),
            Tag.string("total_spawned_entities", (args, ctx) -> (totalEntitiesSpawned == 0 ? 1 : totalEntitiesSpawned) + ""),
            Tag.string("capture_time", (args, ctx) -> captureTime + ""),
            Tag.string("max_capture_time", (args, ctx) -> maxCaptureTime + "")
        );
        Lang.ABYSS_OUTPOST_INVASION_TICK.use(p, tags);
    }

    public boolean hasBeenDefeated(){
        if(captured) return false;
        int amount = (int) (totalEntitiesSpawnedThisWave * .2f);
        return currentWave >= maxWave && spawnedEntities.size() <= amount;
    }

    @Override
    public boolean shouldStop() {
        return defeated || captured || currentWave >= maxWave && spawnedEntities.isEmpty();
    }

    public boolean shouldNextWave(){
        if(currentWave >= maxWave) return false;
        if(currentWave == 0) return true;
        if(spawnedEntities.isEmpty()) return true;
        return !CruxMath.hasOccurredWithin(lastSpawnedWave, 3600);
    }

    public void nextWave(){
        currentWave++;
        lastSpawnedWave = System.currentTimeMillis();
        Crux.scheduler().runTask(() -> spawnWave(currentWave));
    }

    public void spawnWave(int wave){
        totalEntitiesSpawnedThisWave = 0;
        spawnEntities(
            3, 2, 5,
            16D, 32D
        );
    }

    public boolean isValidGround(Block b){
        return b.isSolid();
    }

    public static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public boolean isSurroundedBySolid(Block b){
        int checks = 0;
        for(BlockFace face : FACES){
            if(b.getRelative(face).isSolid()){
                checks++;
                if(checks >= 3) return true;
            }
        }
        return false;
    }

    public boolean isBlockedOff(Block spawn) {
        return isSurroundedBySolid(spawn) && isSurroundedBySolid(spawn.getRelative(BlockFace.UP));
    }

    public Block findValidSpawn(Block b){
        if(isValidSpawn(b)) return b;
        int range = 84;
        for(int y = 1; y <= range; y++){
            Block check = b.getRelative(0, y, 0);
            if(isValidSpawn(check)) return check;
            check = b.getRelative(0, -y, 0);
            if(isValidSpawn(check)) return check;
        }
        return null;
    }

    public boolean isValidSpawn(Block b){
        if(!b.isEmpty() && !b.isPassable()) return false;
        Block check = b.getRelative(BlockFace.DOWN);
        if(!isValidGround(check)) return false;
        for(int y = 1; y <= 5; y++){
            check = b.getRelative(0, y, 0);
            if(!check.isEmpty() && !check.isPassable()) return false;
        }
        if(isBlockedOff(b)) return false;
        return true;
    }

    /*public boolean isValidGroupSpawn(Block b){
        int range = 1;
        int amountChecked = 0;
        for(int x = -range; x <= range; x++){
            for(int z = -range; z <= range; z++){
                Block check = b.getRelative(x, 0, z);
                check = findGround(check);
                if(check == null) continue;
                if(isValidSpawn(check)) amountChecked++;
            }
        }
        return amountChecked > 3;
    }*/

    public Block findSpawn(Block b){
        if(isValidSpawn(b)) return b;
        int range = 1;
        for(int x = -range; x <= range; x++){
            for(int z = -range; z <= range; z++){
                Block check = b.getRelative(x, 0, z);
                check = findValidSpawn(check);
                if(check == null) continue;
                return b;
            }
        }
        return null;
    }

    public Location findNearbySpawn(Location center, double range){
        Location check = center.clone().add(
            CruxMath.random(-range, range), 0, CruxMath.random(-range, range)
        );
        Block ground = findValidSpawn(check.getBlock());
        if(ground == null) return center;
        return ground.getLocation().toCenterLocation().subtract(0, .3, 0);
    }

    public Location findSpawn(BoundingBox box, double y, double minSpawnDistance, double maxSpawnDistance,
                              int maxAttempts){
        while(maxAttempts > 0){
            maxAttempts--;
            Location spawn = findRandomSpawnPoint(box, minSpawnDistance, maxSpawnDistance);
            spawn.setY(y);
            Block b = findSpawn(spawn.getBlock());
            if(b == null) continue;
            return b.getLocation().toCenterLocation().subtract(0, .3, 0);
        }
        return null;
    }

    public Collection<Entity> spawnEntities(
        int groupAmount, int minAmount, int maxAmount,
        double minSpawnDistance, double maxSpawnDistance
    ){
        Collection<Entity> list = new HashSet<>();
        BoundingBox box = getTargetStructureBox();
        CruxPosition pos = targetStructure.getPosition();/*getFinalTargetLocationFromStructure();
        if(targetLoc == null){
            Crux.log(Level.SEVERE, "No node is found on structure! " + targetStructure.getPosition() + ", " + targetStructure.getChunk());
            return list;
        }*/

        double distance = targetStructure.getBoundingBox().getWidthX()*.6;
        while(groupAmount > 0){
            groupAmount--;
            Location spawn = findSpawn(
                box, targetStructure.getPosition().y(), minSpawnDistance, maxSpawnDistance, 8
            );
            if(spawn == null) continue;
            int amount = CruxMath.random(minAmount, maxAmount);

            while(amount > 0){
                amount--;
                Location mobSpawn = findNearbySpawn(spawn, 3);
                SpawnContext ctx = SpawnContext.simple(mobSpawn.getBlock(), CruxMath.random());
                spawns.selectRandom(1,ctx).forEach(e ->{
                    if(!(e.spawn(ctx) instanceof Mob mob)) return;
                    if(!(CruxGoalUtil.getGoal(mob, Goal.class, CruxGoalBase.defaultKey()) instanceof PathTargetMobGoal goal)){
                        Crux.log(Level.SEVERE, "Goal from " + mob.getName() + " is not a PathTargetMobGoal!");
                        return;
                    }
                    goal.setPath(GoalPath.goalPath(List.of(GoalNode.distanceGoalNode(pos, distance))));
                    if(goal instanceof OutpostTargeterGoal g) g.setOutpostTarget(targetStructure);
                    onEntitySpawn(mob);
                });
            }
        }
        return list;
    }

    public void onEntitySpawn(Entity e){
        totalEntitiesSpawned++;
        totalEntitiesSpawnedThisWave++;
        spawnedEntities.put(e.getUniqueId(),new WeakReference<>(e));
    }

    public boolean isActive(){
        return structures.isActive(targetStructure);
    }

    public ActiveStructure getActive(){
        return structures.getActive(targetStructure);
    }

    public BoundingBox getTargetStructureBox(){
        return targetStructure.getOrDefault(StoredStructureComponents.OUTER_BOX, targetStructure.getBoundingBox());
    }

    /*public GoalPath buildGoalPath(){
        Location finalTarget = getFinalTargetLocationFromStructure();
        CruxPosition base = targetStructure.getPosition();
        return GoalPath.goalPath(List.of(
            GoalNode.distanceGoalNode(base.add(13, 1, 0).rotateAroundY(base, targetStructure.getRotation()), 2D),
            GoalNode.distanceGoalNode(base.add(-5, 5, 7).rotateAroundY(base, targetStructure.getRotation()), 2D),
            GoalNode.distanceGoalNode(base.add(0, 12, -6).rotateAroundY(base, targetStructure.getRotation()), 2D),
            GoalNode.distanceGoalNode(base.add(4, 21, -8).rotateAroundY(base, targetStructure.getRotation()), 2D),
            GoalNode.distanceGoalNode(base.add(-6, 35, 1).rotateAroundY(base, targetStructure.getRotation()), 2D),
            GoalNode.distanceGoalNode(finalTarget, 2D)
        ));
    }*/

    public ActiveAbyssConquestNode getConquestNode(){
        PlacedCustomBlocksComponent placed = targetStructure.get(CruxBlockComponents.PLACED_CUSTOM_BLOCKS);
        World world = this.world.toBukkitWorld();
        for (CruxPosition pos : placed.getPlacedBlocks()) {
            Block block = pos.getBlock(world);
            ActiveCruxBlock active = CruxCore.core().cruxBlocks().getActiveBlock(block);
            if(!(active instanceof ActiveAbyssConquestNode node)) continue;
            return node;
        }
        return null;
    }

    public Location getFinalTargetLocationFromStructure(){
        PlacedCustomBlocksComponent placed = targetStructure.get(CruxBlockComponents.PLACED_CUSTOM_BLOCKS);
        World world = this.world.toBukkitWorld();
        for (CruxPosition pos : placed.getPlacedBlocks()) {
            Block block = pos.getBlock(world);
            ActiveCruxBlock active = CruxCore.core().cruxBlocks().getActiveBlock(block);
            if(!(active instanceof ActiveAbyssConquestNode node)) continue;
            return block.getLocation().toCenterLocation();
        }

        return null;
    }

    public Location findRandomSpawnPoint(BoundingBox box, double minDistance, double maxDistance){
        double minX = box.getMinX();
        double maxX = box.getMaxX();
        double minZ = box.getMinZ();
        double maxZ = box.getMaxZ();

        Random rand = CruxMath.random();
        int side = rand.nextInt(4); // 0 = North, 1 = South, 2 = East, 3 = West

        double distance = CruxMath.random(minDistance, maxDistance);
        double spawnX, spawnZ;

        spawnZ = switch (side) {
            case 0 -> {
                spawnX = minX + (maxX - minX) * rand.nextDouble(); // Random X within box width
                yield maxZ + distance;
            }
            case 1 -> {
                spawnX = minX + (maxX - minX) * rand.nextDouble(); // Random X within box width
                yield minZ - distance;
            }
            case 2 -> {
                spawnX = maxX + distance; // Place outside, east of the box
                yield minZ + (maxZ - minZ) * rand.nextDouble();
            }
            case 3 -> {
                spawnX = minX - distance; // Place outside, west of the box
                yield minZ + (maxZ - minZ) * rand.nextDouble();
            }
            default -> throw new IllegalStateException("Unexpected side value: " + side);
        };

        return new Location(world.toBukkitWorld(), spawnX, 64D, spawnZ);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(EntityDamager.getOwner(event.getDamager()) instanceof Player p)) return;
        Entity e = event.getEntity();
        if(!hasSpawned(e)) return;
        float damage = (float) event.getFinalDamage();
        if(damage <= 0f) return;
        participants.compute(p.getUniqueId(), (u, f) -> f == null ? damage : f + damage);
    }

    public StoredStructure getTargetStructure() {
        return targetStructure;
    }
}

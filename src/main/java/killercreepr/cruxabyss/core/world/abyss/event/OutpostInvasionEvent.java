package killercreepr.cruxabyss.core.world.abyss.event;

import com.destroystokyo.paper.entity.ai.Goal;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxabyss.core.block.active.ActiveAbyssConquestNode;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import killercreepr.cruxblocks.core.component.PlacedCustomBlocksComponent;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.api.entity.mob.goal.LocationTargetMobGoal;
import killercreepr.cruxentities.entity.mob.goal.CruxGoalBase;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.util.BoundingBox;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;

public class OutpostInvasionEvent implements WorldEvent {
    protected final CruxWorld world;
    protected final StoredStructure targetStructure;
    protected final StructureWorldModule structures;
    protected final NaturalEntitySpawnGroup spawns;
    protected final int maxWave;
    protected int currentWave;

    protected final Collection<Reference<Entity>> spawnedEntities = new HashSet<>();
    protected long lastSpawnedWave;

    public OutpostInvasionEvent(CruxWorld world, StoredStructure targetStructure, NaturalEntitySpawnGroup spawns, int maxWave) {
        this.world = world;
        this.structures = world.getModule(StructureWorldModule.class);
        this.targetStructure = targetStructure;
        this.spawns = spawns;
        this.maxWave = maxWave;
    }

    public Collection<Reference<Entity>> updateSpawnedEntities(){
        spawnedEntities.removeIf(d -> d.get() == null);
        return spawnedEntities;
    }

    public Collection<Entity> getSpawnedEntities(){
        Collection<Entity> list = new HashSet<>();
        spawnedEntities.forEach(e -> {
            Entity entity = e.get();
            if(entity != null) list.add(entity);
        });
        return list;
    }

    protected int tick = 0;
    @Override
    public void tick(){
        tick++;
        if(tick % 20 != 0) return;

        updateSpawnedEntities();
        if(shouldNextWave()){
            nextWave();
        }
    }

    @Override
    public boolean shouldStop() {
        return currentWave >= maxWave;
    }

    public boolean shouldNextWave(){
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
        spawnEntities(
            3, 2, 5,
            16D, 32D
        );
    }

    public boolean isValidGround(Block b){
        return b.isSolid();
    }

    public Block findGround(Block b){
        if(isValidGround(b)) return b;
        int range = 32;
        for(int y = 1; y <= range; y++){
            Block check = b.getRelative(0, y, 0);
            if(isValidGround(check)) return check;
            check = b.getRelative(0, -y, 0);
            if(isValidGround(check)) return check;
        }
        return null;
    }

    public boolean isValidSpawn(Block b){
        Block check = b.getRelative(BlockFace.DOWN);
        if(!isValidGround(check)) return false;
        for(int y = 1; y <= 5; y++){
            check = b.getRelative(0, y, 0);
            if(!check.isEmpty() && !check.isPassable()) return false;
        }
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
                check = findGround(check);
                if(check == null) continue;
                if(isValidSpawn(b)) return b;
            }
        }
        return null;
    }

    public Location findNearbySpawn(Location center, double range){
        Location check = center.clone().add(
            CruxMath.random(-range, range), 0, CruxMath.random(-range, range)
        );
        Block ground = findGround(check.getBlock());
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
        Location targetLoc = getTargetLocationFromStructure();
        if(targetLoc == null){
            Crux.log(Level.SEVERE, "No node is found on structure! " + targetStructure.getPosition() + ", " + targetStructure.getChunk());
            return list;
        }

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
                    if(!(CruxGoalUtil.getGoal(mob, Goal.class, CruxGoalBase.defaultKey()) instanceof LocationTargetMobGoal goal)){
                        Crux.log(Level.SEVERE, "Goal from " + mob.getName() + " is not a LocationTargetMobGoal!");
                        return;
                    }
                    goal.setTargetLocation(targetLoc);
                });
            }
        }
        return list;
    }

    public boolean isActive(){
        return structures.isActive(targetStructure);
    }

    public BoundingBox getTargetStructureBox(){
        return targetStructure.getOrDefault(StoredStructureComponents.OUTER_BOX, targetStructure.getBoundingBox());
    }

    public Location getTargetLocationFromStructure(){
        PlacedCustomBlocksComponent placed = targetStructure.get(CruxBlockComponents.PLACED_CUSTOM_BLOCKS);
        World world = targetStructure.getChunk().toBukkitWorld();
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
}

package killercreepr.cruxabyss.core.world.abyss.event;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class OutpostInvasionEvent implements WorldEvent {
    protected final CruxWorld world;
    protected final StoredStructure targetStructure;
    protected final StructureWorldModule structures;
    protected final int maxWave;
    protected int currentWave;

    public OutpostInvasionEvent(CruxWorld world, StoredStructure targetStructure, int maxWave) {
        this.world = world;
        this.structures = world.getModule(StructureWorldModule.class);
        this.targetStructure = targetStructure;
        this.maxWave = maxWave;
    }

    @Override
    public void tick(){

    }

    @Override
    public boolean shouldStop() {
        return false;
    }

    public void nextWave(){
        currentWave++;
        spawnWave(currentWave);
    }

    public void spawnWave(int wave){

    }

    public boolean isValidGround(Block b){
        return b.isSolid();
    }

    public Block findGround(Block b){
        if(isValidGround(b)) return b;
        for(int y = 1; y <= 3; y++){
            Block check = b.getRelative(0, y, 0);
            if(isValidGround(check)) return check;
        }

        for(int y = 1; y <= 3; y++){
            Block check = b.getRelative(0, -y, 0);
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

    public boolean isValidGroupSpawn(Block b){
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
    }

    public Block findGroupSpawn(Block b){
        if(isValidGroupSpawn(b)) return b;
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

    public Block findSpawn(Block b){
        if(isValidSpawn(b)) return b;
        for(int y = 1; y <= 5; y++){
            Block check = b.getRelative(0, y, 0);
            if(isValidSpawn(check)) return check;
        }
        return null;
    }

    public Location findNearbySpawn(Location center, double range){
        Location check = center.clone().add(
            CruxMath.random(-range, range), 0, CruxMath.random(-range, range)
        );
        Block ground = findGround(check.getBlock());
        if(ground == null) return center;
        if(isValidSpawn(ground)) return check;
        return center;
    }

    public Collection<Entity> spawnEntities(
        int groupAmount, int minAmount, int maxAmount,
        double minSpawnDistance, double maxSpawnDistance
    ){
        Collection<Entity> list = new HashSet<>();
        BoundingBox box = getTargetStructureBox();
        while(groupAmount > 0){
            groupAmount--;
            Location spawn = findRandomSpawnPoint(
                box, minSpawnDistance, maxSpawnDistance
            );
            Block b = findGroupSpawn(spawn.getBlock());
            if(b == null) continue;
            spawn = b.getLocation().toCenterLocation().subtract(0, .3, 0);
            int amount = CruxMath.random(minAmount, maxAmount);
            while(amount > 0){
                amount--;
                Location mobSpawn = findNearbySpawn(spawn, 3);
                AbyssMob.TOXICATOR.spawn(mobSpawn);
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

    public Location findRandomSpawnPoint(BoundingBox box, double minDistance, double maxDistance){
        double minX = box.getMinX();
        double maxX = box.getMaxX();
        double minY = box.getMinY();
        double maxY = box.getMaxY();
        double minZ = box.getMinZ();
        double maxZ = box.getMaxZ();

        // Randomly choose a side to spawn outside the box (0 = North, 1 = South, 2 = East, 3 = West, 4 = Top, 5 = Bottom)
        Random rand = CruxMath.random();
        int side = rand.nextInt(4);//rand.nextInt(6);

        // Random distance within the specified range
        double distance = CruxMath.random(minDistance, maxDistance);

        double spawnX = 0;
        double spawnY = 0;
        double spawnZ = 0;

        // Handle each side of the bounding box
        switch (side) {
            case 0: // North side (y is greater than maxY)
                spawnX = minX + (maxX - minX) * rand.nextDouble(); // Random X within box width
                spawnY = maxY + distance; // Place outside, above the box
                spawnZ = minZ + (maxZ - minZ) * rand.nextDouble(); // Random Z within box depth
                break;
            case 1: // South side (y is less than minY)
                spawnX = minX + (maxX - minX) * rand.nextDouble(); // Random X within box width
                spawnY = minY - distance; // Place outside, below the box
                spawnZ = minZ + (maxZ - minZ) * rand.nextDouble(); // Random Z within box depth
                break;
            case 2: // East side (x is greater than maxX)
                spawnY = minY + (maxY - minY) * rand.nextDouble(); // Random Y within box height
                spawnZ = minZ + (maxZ - minZ) * rand.nextDouble(); // Random Z within box depth
                spawnX = maxX + distance; // Place outside, right of the box
                break;
            case 3: // West side (x is less than minX)
                spawnY = minY + (maxY - minY) * rand.nextDouble(); // Random Y within box height
                spawnZ = minZ + (maxZ - minZ) * rand.nextDouble(); // Random Z within box depth
                spawnX = minX - distance; // Place outside, left of the box
                break;
            case 4: // Top side (z is greater than maxZ)
                spawnX = minX + (maxX - minX) * rand.nextDouble(); // Random X within box width
                spawnY = minY + (maxY - minY) * rand.nextDouble(); // Random Y within box height
                spawnZ = maxZ + distance; // Place outside, above the box
                break;
            case 5: // Bottom side (z is less than minZ)
                spawnX = minX + (maxX - minX) * rand.nextDouble(); // Random X within box width
                spawnY = minY + (maxY - minY) * rand.nextDouble(); // Random Y within box height
                spawnZ = minZ - distance; // Place outside, below the box
                break;
        }

        // Return the location object in the same world as the bounding box
        return new Location(world.toBukkitWorld(), spawnX, spawnY, spawnZ);
    }
}

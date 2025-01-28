package killercreepr.cruxabyss.core.world.abyss.event;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.Random;

public class OutpostInvasionEvent implements WorldEvent {
    protected final CruxWorld world;
    protected final StoredStructure targetStructure;
    protected final StructureWorldModule structures;

    public OutpostInvasionEvent(CruxWorld world, StoredStructure targetStructure) {
        this.world = world;
        this.structures = world.getModule(StructureWorldModule.class);
        this.targetStructure = targetStructure;
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

    @Override
    public void tick(){

    }
}

package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.crux.api.block.CruxBlockWrapper;
import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.usurvive.core.block.USurviveBlocks;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ActiveAbyssOutpostLootHolder implements ManagedTicked {
    protected final ActiveStructure active;
    protected final AbyssOutpostLootHolderData data;
    public ActiveAbyssOutpostLootHolder(@NotNull ActiveStructure active) {
        this.active = active;
        this.data = active.getData().get(AbyssComponents.ABYSS_OUTPOST_LOOT_HOLDER_DATA);
        Objects.requireNonNull(data, "No!");
    }

    protected int tick = 0;
    @Override
    public void tick() {
        tick++;
        if(tick < 100) return;
        tick = 0;
        if(System.currentTimeMillis() < data.nextGeneration) return;
        data.lastGenerated = System.currentTimeMillis();
        data.nextGeneration = System.currentTimeMillis() + (50L * CruxMath.random(200, 300));
        generate();
    }

    public void generate(){
        BoundingBox box = active.getData().get(StoredStructureComponents.OUTER_BOX);
        Crux.scheduler().runTask(() ->{
            generateOre(active.getChunk().getWorld(), box, CruxMath.random());
        });
    }

    public void generateOre(World world, BoundingBox box, Random random) {
        int minX = (int) Math.floor(box.getMinX());
        int minY = (int) Math.floor(box.getMinY());
        int minZ = (int) Math.floor(box.getMinZ());
        int maxX = (int) Math.floor(box.getMaxX() - .5);
        int maxY = (int) Math.floor(box.getMaxY() - .5);
        int maxZ = (int) Math.floor(box.getMaxZ() - .5);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    // Calculate ore generation probability for this position
                    double probability = calculateOreProbability(x, y, z, box);

                    // Only generate ore if a random number is less than the probability
                    if (random.nextDouble() < probability) {
                        Block b = world.getBlockAt(x, y, z);
                        // Ensure the block is supported (there must be a block beneath it)
                        if (isBlockSupported(b)) {
                            placeOreBlock(b);
                        }
                    }
                }
            }
        }
    }

    private double calculateOreProbability(int x, int y, int z, BoundingBox box) {
        double centerY = box.getMinY() + (box.getMaxY() - box.getMinY()) / 2;
        double distanceFromCenter = Math.abs(centerY - y);

        // Use a linear gradient for probability (adjust as needed)
        double gradient = 1.0 - (distanceFromCenter / (box.getMaxY() - box.getMinY()));
        return Math.max(0.1, gradient); // Ensure at least some probability
    }

    private boolean isBlockSupported(Block b) {
        Block ground = b.getRelative(BlockFace.DOWN);
        return ground.isSolid();
    }

    protected final static List<CruxBlockWrapper> ORES = List.of(
        wrapper(Material.IRON_ORE),
        wrapper(Material.GOLD_ORE),
        wrapper(USurviveBlocks.ORBIT_ORE.key())
    );
    private static CruxBlockWrapper wrapper(Material m){
        return wrapper(m.key());
    }
    private static CruxBlockWrapper wrapper(Key m){
        return Crux.handlers().block().getBlockWrapper(m);
    }
    private void placeOreBlock(Block b) {
        CruxBlockWrapper block = CruxCollection.getRandom(ORES);
        block.setBlock(b,false);
    }
}

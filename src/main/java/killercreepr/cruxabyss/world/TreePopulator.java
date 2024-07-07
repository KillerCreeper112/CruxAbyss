package killercreepr.cruxabyss.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TreePopulator extends BlockPopulator {
    private final HashMap<Biome, List<TreeType>> biomeTrees = new HashMap<>() {{
        put(Biome.PLAINS, List.of());
        put(Biome.FOREST, List.of(TreeType.BIRCH));
        put(Biome.DARK_FOREST, List.of(TreeType.DARK_OAK));
    }};

    private final HashMap<Biome, List<GrimTree.Type>> biomeGrimTrees = new HashMap<>() {{
        put(Biome.PLAINS, List.of(GrimTree.Type.TEST));
    }};

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        int x = random.nextInt(16) + chunkX * 16;
        int z = random.nextInt(16) + chunkZ * 16;
        int y = worldInfo.getMaxHeight()-1;
        while(limitedRegion.getType(x, y, z).isAir() && y > -64) y--;

        Location location = new Location(Bukkit.getWorld(worldInfo.getUID()), x, y, z);
        List<TreeType> trees = biomeTrees.getOrDefault(limitedRegion.getBiome(location), List.of(TreeType.TREE, TreeType.BIRCH));

        if (trees.size() > 0 && limitedRegion.getType(x, y - 1, z).isSolid()) {
            limitedRegion.generateTree(location, random, trees.get(random.nextInt(trees.size())));
        }

        for(GrimTree.Type t : biomeGrimTrees.getOrDefault(limitedRegion.getBiome(location), new ArrayList<>())){
            GrimTree tree = t.tree(worldInfo, random, limitedRegion);
            if(tree.canGrow(x, y, z)){
                tree.grow(x, y, z);
            }
        }
    }
}

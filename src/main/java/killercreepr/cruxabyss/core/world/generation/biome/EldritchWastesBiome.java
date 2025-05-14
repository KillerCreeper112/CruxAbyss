package killercreepr.cruxabyss.core.world.generation.biome;

import com.destroystokyo.paper.MaterialTags;
import killercreepr.cruxabyss.core.world.biome.BiomeManager;
import killercreepr.cruxabyss.core.world.generation.populator.GrimPopulator;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EldritchWastesBiome extends GrimBiome {
        public EldritchWastesBiome(@NotNull GrimPopulator master) {
        super(master);
    }

    public void acceptBiomeSet(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setBiome(BiomeManager.ELDRITCH_WASTES, limitedRegion, x,y,z);
    }

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        acceptBiomeSet(worldInfo, random, limitedRegion, x, y, z);
        Material type = b.getType();
        if(!b.isSolid() || type == Material.BEDROCK) return;

        if(Tag.LOGS.isTagged(type) || Tag.LEAVES.isTagged(type)){
            limitedRegion.setType(x, y, z, Material.AIR);

            if(limitedRegion.isInRegion(x,y+1,z)){
                Material aboveType = limitedRegion.getType(x,y+1,z);
                if(aboveType == Material.SNOW || !limitedRegion.getBlockState(x,y+1,z).getBlock().isSolid()){
                    limitedRegion.setType(x, y+1, z, Material.AIR);
                }
            }
            return;
        }
    }


}

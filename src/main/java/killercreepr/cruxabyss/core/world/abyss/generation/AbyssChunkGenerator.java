package killercreepr.cruxabyss.core.world.abyss.generation;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * No longer used at the moment
 */
@Deprecated
public class AbyssChunkGenerator extends ChunkGenerator {
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
    }

    @Override
    public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        //super.generateCaves(worldInfo, random, chunkX, chunkZ, chunkData);
        /*for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight() ; y++){
                    if(MaterialSetTag.LEAVES.isTagged(chunkData.getBlockData(x, y, z).getMaterial())){
                        chunkData.setBlock(x, y, z, Material.AIR);
                    }
                }
            }
        }*/
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return true;
    }
}

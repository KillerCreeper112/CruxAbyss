package killercreepr.cruxabyss.world.generation.populator;

import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.block.BlockState;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class AbyssOverworldPopulator extends GrimPopulator{
    protected final CruxNoise seep = CruxNoise.fast()
        .frequency(.02f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(2)
        ;
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion){
        float s = seep.noise(chunkX, chunkZ);
        if(s > .3 && s < .5){
            spawnAbyssSeep(worldInfo, random, chunkX, chunkZ, limitedRegion);
        }
    }

    public void spawnAbyssSeep(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion region){
        int x = CruxMath.random(0, 15) + (chunkX * 16);
        int z = CruxMath.random(0, 15) + (chunkZ * 16);
        int y = CruxMath.random(getMinHeight(worldInfo), getMaxHeight(worldInfo));
        BlockState block = region.getBlockState(x, y, z);
        if(!block.getType().isSolid()) return;
        if(getSolidNearby(region, 3, x, y, z) < 16) return;
        spawnAbyssSeep(worldInfo, random, x, y, z, region);
    }

    public int getSolidNearby(@NotNull LimitedRegion region, int range, int x, int y, int z){
        int amount = 0;
        for(int xRange = -range; xRange <= range; xRange++){
            for(int zRange = -range; zRange <= range; zRange++){
                for(int yRange = -range; yRange <= range; yRange++){
                    if(xRange == 0 && yRange == 0 && zRange == 0) continue;
                    BlockState block = region.getBlockState(x + xRange, y + yRange, z + zRange);
                    if(block.getType().isSolid()) amount++;
                }
            }
        }
        return amount;
    }

    public void spawnAbyssSeep(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int y, int z, @NotNull LimitedRegion region){
        cluster(
            CruxMath.random(6, 16), CruxMath.random(3, 6),
            CruxMath.random(2, 4), x, y, z,
            (xx, yy, zz) ->{
                AbyssBlocks.PLAGUE_MOSS.getBaseBlock().setBlock(
                    region, xx, yy, zz
                );
            }
        );
    }

    public void cluster(int amount, int range, int yRange, int x, int y, int z, @NotNull TriConsumer<Integer, Integer, Integer> consumer){
        while(amount > 0){
            amount--;
            int xx = x + CruxMath.random(-range, range);
            int yy = y + CruxMath.random(-yRange, yRange);
            int zz = z + CruxMath.random(-range, range);
            consumer.accept(xx, yy, zz);
        }
    }

    @Override
    public int getMinHeight(@NotNull WorldInfo info) {
        return super.getMinHeight(info) + 32;
    }

    @Override
    public int getMaxHeight(@NotNull WorldInfo info) {
        return super.getMaxHeight(info) - 32;
    }
}

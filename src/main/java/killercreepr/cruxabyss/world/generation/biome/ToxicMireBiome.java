package killercreepr.cruxabyss.world.generation.biome;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.generation.decoration.ToxicMireTreePopulator;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ToxicMireBiome extends GrimBiome {
    private final CruxNoise noise = CruxNoise.fast()
        .frequency(0.005f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(5)
        ;

    public ToxicMireBiome(@NotNull GrimPopulator master) {
        super(master);
    }

    public void acceptBiomeSet(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setBiome(BiomeManager.TOXIC_MIRE, limitedRegion, x,y,z);
    }

    public final ToxicMireTreePopulator treePopulator = new ToxicMireTreePopulator();

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Material m = limitedRegion.getType(x,y,z);
        acceptBiomeSet(worldInfo, random, limitedRegion, x, y, z);
        if(m == Material.BEDROCK) return;
        if(MaterialSetTag.LOGS.isTagged(m)){
            limitedRegion.setType(x, y, z, Material.AIR);
            /*CruxBlock block = AbyssBlocks.PLAGUE_STEM.getBlock(orientable.getAxis());
            if(block==null) return;
            block.setBlock(limitedRegion, x, y, z);*/
            if(limitedRegion.isInRegion(x, y-1, z)){
                BlockState state = limitedRegion.getBlockState(x, y-1, z);
                if(state.isCollidable() && random.nextBoolean()){
                    treePopulator.place(
                        worldInfo, random, limitedRegion,
                        new Location(null, x, y, z)
                    );
                }
            }
            return;
        }else if(MaterialSetTag.LEAVES.isTagged(m)){
            limitedRegion.setType(x, y, z, Material.AIR);
            //AbyssBlocks.PLAGUE_WART.getBaseBlock().setBlock(limitedRegion, x, y, z);
            //limitedRegion.setType(x,y,z, Material.NETHER_WART_BLOCK);
            if(limitedRegion.isInRegion(x,y+1,z) && limitedRegion.getType(x,y+1,z) == Material.SNOW){
                limitedRegion.setType(x,y+1,z, Material.AIR);
            }
            return;
        }else if(MaterialSetTag.SMALL_FLOWERS.isTagged(m)){
            flower(limitedRegion,x,y,z);
            return;
        }else if(MaterialSetTag.TALL_FLOWERS.isTagged(m)){
            //todo add something here
            limitedRegion.setType(x,y,z,Material.AIR);
            return;
        }
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(!b.isSolid()) return;

        //float n = noise.GetNoise(x,y,z);
        if(limitedRegion.isInRegion(x,y+1,z) && MaterialSetTag.SMALL_FLOWERS.isTagged(limitedRegion.getType(x,y,z))){
            flower(limitedRegion,x,y+1,z);
            return;
        }
        switch (m){
            default ->{
                if(CruxBlocksRegistries.BLOCKS.getByBlockData(limitedRegion.getBlockData(x, y, z)) != null) return;
                if(y >= 62 && limitedRegion.isInRegion(x,y+1,z) && limitedRegion.getType(x,y+1,z) == Material.AIR){
                    AbyssBlocks.PLAGUE_MOSS.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                    return;
                }else if(y >= 58){
                    AbyssBlocks.PLAGUE_MOSS_DIRT.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                } else{
                    AbyssBlocks.PLAGUE_STONE.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                    return;
                }
            }
        }
    }

    private void flower(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        limitedRegion.setType(x,y,z, Material.AIR);
        //limitedRegion.setType(x,y,z, CruxMath.random(1, 100) <= 50 ? Material.CRIMSON_FUNGUS : Material.CRIMSON_ROOTS);
    }
}

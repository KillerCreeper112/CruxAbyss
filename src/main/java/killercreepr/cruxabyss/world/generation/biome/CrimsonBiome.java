package killercreepr.cruxabyss.world.generation.biome;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.world.FastNoiseLite;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.geysermc.api.Geyser;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CrimsonBiome extends GrimBiome {
    private final FastNoiseLite noise = new FastNoiseLite();

    public CrimsonBiome(@NotNull GrimPopulator master) {
        super(master);
        noise.SetFrequency(0.005f);
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(5);
    }

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Material m = limitedRegion.getType(x,y,z);
        setBiome(BiomeManager.CRIMSON, limitedRegion, x,y,z,
                -2, y > 61 ? worldInfo.getMaxHeight() : 0,
                2, y > 61 ? worldInfo.getMaxHeight() : 2);
        if(m == Material.BEDROCK) return;
        if(MaterialSetTag.LOGS.isTagged(m) && limitedRegion.getBlockData(x,y,z) instanceof Orientable orientable){
            limitedRegion.setType(x,y,z,Material.CRIMSON_STEM);
            BlockData d = limitedRegion.getBlockData(x,y,z);
            if(d instanceof Orientable o){
                o.setAxis(orientable.getAxis());
                limitedRegion.setBlockData(x,y,z,o);
            }
            return;
        }else if(MaterialSetTag.LEAVES.isTagged(m)){
            limitedRegion.setType(x,y,z, Material.NETHER_WART_BLOCK);
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
        Material type;
        switch (m){
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> type = Material.NETHER_GOLD_ORE;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> type = Material.NETHER_QUARTZ_ORE;
            default ->{
                if(y > 61 && limitedRegion.isInRegion(x,y+1,z) && limitedRegion.getType(x,y+1,z) == Material.AIR){
                    AbyssBlocks.PLAGUE_MOSS.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                    return;
                }else type = Material.NETHERRACK;
            }
        }
        if(limitedRegion.isInRegion(x,y+1,z) && MaterialSetTag.SMALL_FLOWERS.isTagged(limitedRegion.getType(x,y,z))){
            flower(limitedRegion,x,y+1,z);
        }
        limitedRegion.setType(x,y,z,type);
    }

    private void flower(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        limitedRegion.setType(x,y,z, CruxMath.random(1, 100) <= 50 ? Material.CRIMSON_FUNGUS : Material.CRIMSON_ROOTS);
    }
}

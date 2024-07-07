package killercreepr.cruxabyss.world.generation.populator;

import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.world.FastNoiseLite;
import killercreepr.cruxabyss.world.generation.BlockContainer;
import killercreepr.cruxabyss.world.generation.biome.CharredBiome;
import killercreepr.cruxabyss.world.generation.biome.CorruptBiome;
import killercreepr.cruxabyss.world.generation.biome.CrimsonBiome;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MasterPopulator extends GrimPopulator{
    private final Vector crimsonCenter;
    private final CharredBiome charredBiome = new CharredBiome(this);
    private final CrimsonBiome crimsonBiome = new CrimsonBiome(this);
    private final CorruptBiome corruptBiome = new CorruptBiome(this);

    private final FastNoiseLite temperature = new FastNoiseLite();
    private final FastNoiseLite humidity = new FastNoiseLite();
    private final FastNoiseLite continental = new FastNoiseLite();

    public MasterPopulator() {
        crimsonCenter = new Vector(CruxMath.random(-300D, 300D), 0D, CruxMath.random(-300D, 300D));
        temperature.SetFrequency(0.005f);
        temperature.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        temperature.SetFractalType(FastNoiseLite.FractalType.FBm);
        temperature.SetFractalOctaves(5);

        humidity.SetFrequency(0.002f);
        humidity.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        humidity.SetFractalType(FastNoiseLite.FractalType.FBm);
        humidity.SetFractalOctaves(7);

        continental.SetFrequency(0.01f);
        continental.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        continental.SetFractalType(FastNoiseLite.FractalType.FBm);
        continental.SetFractalOctaves(3);
    }

    @Override
    public void populateXYZ(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion,
                            int x, int y, int z) {
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(b.getType() == Material.WATER || (b.getBlockData() instanceof Waterlogged w && w.isWaterlogged()) ||
                b.getType() == Material.KELP_PLANT || b.getType() == Material.KELP){
            limitedRegion.setType(x,y,z, Material.LAVA);
            return;
        }else if(b.isReplaceable() && b.getType() != Material.SNOW){
            limitedRegion.setType(x,y,z, Material.AIR);
            return;
        }else if(b.getType() == Material.ICE) limitedRegion.setType(x,y,z, Material.PACKED_ICE);

        double crimsonDistance = BlockContainer.distanceSquared(x,y,z, crimsonCenter.getX(), crimsonCenter.getY(), crimsonCenter.getZ());
        if(crimsonDistance != -1D){
            if(crimsonDistance < (125D*125D)){
                crimsonBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
                return;
            }else if(CruxMath.random(1, 100) <= 25 && crimsonDistance < (135D*135D)){
                crimsonBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
                return;
            }
        }

        if(!b.isSolid()) return;
        float t = temperature.GetNoise(x,y,z);
        float h = humidity.GetNoise(x,y,z);
        float c = continental.GetNoise(x,y,z);

        //charred
        if(t > .3f && h < 0f){
            charredBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
        }else if(t < 0f && (h < -.02f || h > .02f) && c > .3f){ //corrupt
            corruptBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
        }/*else if(crimsonContainer.isEmpty() && t >= 0f && t <= .5f && h > .05f && c > .3f){ //crimson
            crimsonBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
            crimsonContainer.addBlock(limitedRegion.getBlockState(x,y,z).getBlock(),-1);
        }*/
    }

    private boolean isLava(int x, int y, int z, @NotNull LimitedRegion region){
        return region.isInRegion(x,y,z) && region.getType(x,y,z) == Material.LAVA;
    }
}

package killercreepr.cruxabyss.world.generation.populator;

import killercreepr.cruxabyss.world.FastNoiseLite;
import killercreepr.cruxabyss.world.generation.biome.CharredBiome;
import killercreepr.cruxabyss.world.generation.biome.CorruptBiome;
import killercreepr.cruxabyss.world.generation.biome.EldritchWastesBiome;
import killercreepr.cruxabyss.world.generation.biome.ToxicMireBiome;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class AbyssPopulator extends GrimPopulator{
   //protected final Vector crimsonCenter;
   // protected final double crimsonRange = (125D*125D);
    //protected final double crimsonRandomRange = (135D*135D);
    protected final CharredBiome charredBiome = new CharredBiome(this);
    protected final ToxicMireBiome toxicMireBiome = new ToxicMireBiome(this);
    protected final CorruptBiome corruptBiome = new CorruptBiome(this);
    protected final EldritchWastesBiome eldritchWastesBiome = new EldritchWastesBiome(this);

    protected final FastNoiseLite temperature = new FastNoiseLite();
    protected final FastNoiseLite humidity = new FastNoiseLite();
    protected final FastNoiseLite continental = new FastNoiseLite();

    public AbyssPopulator() {
        //crimsonCenter = new Vector(CruxMath.random(-300D, 300D), 0D, CruxMath.random(-300D, 300D));
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

    public void defaultBiome(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion,
                             int x, int y, int z){
        toxicMireBiome.acceptBiomeSet(
            worldInfo, random, limitedRegion, x, y, z
        );
    }

    @Override
    public void populateXYZ(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion,
                            int x, int y, int z) {
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(b.getType() == Material.BEDROCK){
            limitedRegion.setType(x,y,z, Material.AIR);
            defaultBiome(worldInfo, random, limitedRegion, x, y, z);
            return;
        }
        if(b.getType() == Material.WATER || (b.getBlockData() instanceof Waterlogged w && w.isWaterlogged()) ||
                b.getType() == Material.KELP_PLANT || b.getType() == Material.KELP){
            //limitedRegion.setType(x,y,z, Material.LAVA);
        }else if(b.isReplaceable() && b.getType() != Material.SNOW){
            limitedRegion.setType(x,y,z, Material.AIR);
        }

        switch (b.getType()){
            case ICE -> limitedRegion.setType(x,y,z, Material.PACKED_ICE);
            case DEEPSLATE_DIAMOND_ORE -> limitedRegion.setType(x,y,z,Material.DEEPSLATE);
            case DIAMOND_ORE -> limitedRegion.setType(x,y,z,Material.STONE);
        }

        float t = temperature.GetNoise(x,y,z);
        float h = humidity.GetNoise(x,y,z);
        float c = continental.GetNoise(x,y,z);

        //charred
        if(t > .3f && h < 0f){
            charredBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
            return;
        }else if(t < 0f && (h < -.02f || h > .02f) && c > .3f){ //corrupt
            corruptBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
            return;
        }else if(t < .5f && h < 0f){
            eldritchWastesBiome.accept(worldInfo, random, chunkX, chunkZ, limitedRegion, x, y, z);
            return;
        }
        toxicMireBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
    }

    private boolean isLava(int x, int y, int z, @NotNull LimitedRegion region){
        return region.isInRegion(x,y,z) && region.getType(x,y,z) == Material.LAVA;
    }
}

package killercreepr.cruxabyss.core.world.generation.populator;

import com.destroystokyo.paper.MaterialTags;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.world.generation.biome.CharredBiome;
import killercreepr.cruxabyss.core.world.generation.biome.EldritchWastesBiome;
import killercreepr.cruxabyss.core.world.generation.biome.FungalGroveBiome;
import killercreepr.cruxabyss.core.world.generation.biome.ToxicMireBiome;
import killercreepr.cruxblocks.api.block.group.CruxBlockGroup;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

public class AbyssPopulator extends GrimPopulator{
    public static final Predicate<BlockData> GENERAL_DO_NOT_REPLACE = data ->{
        Material type = data.getMaterial();
        if(MaterialTags.RAILS.isTagged(type)) return true;
        if(type == Material.CHEST || type == Material.ENDER_CHEST || type == Material.TRAPPED_CHEST) return true;
        if(type == Material.CRAFTER || type == Material.CRAFTING_TABLE || type == Material.FURNACE ||
        type == Material.SMOKER || type == Material.BLAST_FURNACE || type == Material.LOOM ||
        type == Material.FLETCHING_TABLE) return true;
        if(MaterialTags.FENCE_GATES.isTagged(type)) return true;
        if(MaterialTags.FENCES.isTagged(type)) return true;
        if(MaterialTags.DOORS.isTagged(type)) return true;
        if(MaterialTags.SPONGES.isTagged(type)) return true;

        return false;
    };

   //protected final Vector crimsonCenter;
   // protected final double crimsonRange = (125D*125D);
    //protected final double crimsonRandomRange = (135D*135D);
    protected final CharredBiome charredBiome = new CharredBiome(this);
    protected final ToxicMireBiome toxicMireBiome = new ToxicMireBiome(this);
    protected final FungalGroveBiome fungalGroveBiome = new FungalGroveBiome(this);
    protected final EldritchWastesBiome eldritchWastesBiome = new EldritchWastesBiome(this);

    protected final CruxNoise temperature = CruxNoise.fast()
        .frequency(0.005f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(5)
        ;
    protected final CruxNoise humidity = CruxNoise.fast()
        .frequency(0.002f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(7)
        ;
    protected final CruxNoise continental = CruxNoise.fast()
        .frequency(0.01f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(3)
        ;

    public AbyssPopulator() {
        //crimsonCenter = new Vector(CruxMath.random(-300D, 300D), 0D, CruxMath.random(-300D, 300D));
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
        /*if(b.getType() == Material.WATER || (b.getBlockData() instanceof Waterlogged w && w.isWaterlogged()) ||
                b.getType() == Material.KELP_PLANT || b.getType() == Material.KELP){
            //limitedRegion.setType(x,y,z, Material.LAVA);
        }else if(b.isReplaceable() && b.getType() != Material.SNOW){
            limitedRegion.setType(x,y,z, Material.AIR);
        }*/

        Material type = b.getType();
        if(type == Material.ICE) {
            limitedRegion.setType(x, y, z, Material.PACKED_ICE);
        }

        float t = temperature.noise(x,y,z);
        float h = humidity.noise(x,y,z);
        float c = continental.noise(x,y,z);

        //case DEEPSLATE_DIAMOND_ORE ->{
        //                AbyssBlocks.DEEPSLATE_FUNGIRE_ORE.setBlock(limitedRegion, x,y,z);
        //                //limitedRegion.setType(x,y,z,Material.DEEPSLATE);
        //            }
        //            case DIAMOND_ORE ->{
        //                AbyssBlocks.FUNGIRE_ORE.setBlock(limitedRegion, x,y,z);
        //                //limitedRegion.setType(x,y,z,Material.STONE);
        //            }
        //charred
        if(t > .3f && h < 0f){
            diamondOre(limitedRegion, x, y, z);
            charredBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
            return;
        }else if(t < 0f && (h < -.02f || h > .02f) && c > .3f){ //corrupt
            diamondOre(limitedRegion, x, y, z);
            fungalGroveBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
            return;
        }else if(t < .5f && h < 0f){
            diamondOre(limitedRegion, x, y, z);
            eldritchWastesBiome.accept(worldInfo, random, chunkX, chunkZ, limitedRegion, x, y, z);
            return;
        }
        toxicMireBiome.accept(worldInfo,random,chunkX,chunkZ,limitedRegion,x,y,z);
    }

    public CruxBlockGroup diamondOre(LimitedRegion limitedRegion, int x, int y, int z){
        Material type = limitedRegion.getType(x,y,z);
        if(type == Material.DIAMOND_ORE){
            AbyssBlocks.FUNGIRE_ORE.setBlock(limitedRegion, x,y,z);
            return AbyssBlocks.FUNGIRE_ORE;
        }
        if(type == Material.DEEPSLATE_DIAMOND_ORE){
            AbyssBlocks.DEEPSLATE_FUNGIRE_ORE.setBlock(limitedRegion, x,y,z);
            return AbyssBlocks.DEEPSLATE_FUNGIRE_ORE;
        }
        return null;
    }

    private boolean isLava(int x, int y, int z, @NotNull LimitedRegion region){
        return region.isInRegion(x,y,z) && region.getType(x,y,z) == Material.LAVA;
    }
}

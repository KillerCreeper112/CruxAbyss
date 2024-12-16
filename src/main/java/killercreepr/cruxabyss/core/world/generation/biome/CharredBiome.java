package killercreepr.cruxabyss.core.world.generation.biome;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.world.biome.BiomeManager;
import killercreepr.cruxabyss.core.world.generation.populator.GrimPopulator;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import killercreepr.cruxgeneration.util.FastNoiseLite;
import killercreepr.usurvive.block.USurviveBlocks;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Orientable;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class CharredBiome extends GrimBiome {
    private final FastNoiseLite magma = new FastNoiseLite();
    private final FastNoiseLite noise = new FastNoiseLite();
    private final CruxBlock CHARRED_LOG_X = USurviveBlocks.CHARRED_LOG.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.X);
    private final CruxBlock CHARRED_LOG_Y = USurviveBlocks.CHARRED_LOG.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.Y);
    private final CruxBlock CHARRED_LOG_Z = USurviveBlocks.CHARRED_LOG.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.Z);

    public CharredBiome(@NotNull GrimPopulator master) {
        super(master);
        noise.SetFrequency(0.005f);
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetFractalOctaves(5);

        magma.SetFrequency(0.03f);
        magma.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        magma.SetFractalType(FastNoiseLite.FractalType.FBm);
        magma.SetFractalOctaves(5);
    }

    public void acceptBiomeSet(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setBiome(BiomeManager.CHARRED_WASTES, limitedRegion, x,y,z);
    }

    protected final List<Material> smallBurntDecorations = List.of(
        Material.DEAD_FIRE_CORAL,
        Material.DEAD_HORN_CORAL,
        Material.DEAD_BRAIN_CORAL,
        Material.DEAD_BUSH
    );
    public Material randomSmallBurntDecoration(){
        return CruxCollection.getRandom(smallBurntDecorations);
    }

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        acceptBiomeSet(worldInfo, random, limitedRegion, x, y, z);
        if(!b.isSolid()) return;
        Material m = limitedRegion.getType(x,y,z);
        if(m == Material.BEDROCK) return;
        if(MaterialSetTag.LOGS.isTagged(m) && limitedRegion.getBlockData(x,y,z) instanceof Orientable orientable){
            CruxBlock custom;
            switch (orientable.getAxis()){
                case X -> custom = CHARRED_LOG_X;
                case Y -> custom = CHARRED_LOG_Y;
                case Z -> custom = CHARRED_LOG_Z;
                default -> custom = null;
            }
            if(custom != null) custom.setBlock(limitedRegion, x,y,z);
            return;
        }else if(MaterialSetTag.LEAVES.isTagged(m)){
            limitedRegion.setType(x,y,z, Material.AIR);
            if(limitedRegion.isInRegion(x,y+1,z) && limitedRegion.getType(x,y+1,z) == Material.SNOW){
                limitedRegion.setType(x,y+1,z, Material.AIR);
            }
            return;
        }else if(MaterialSetTag.PLANKS.isTagged(m)){
            CruxBlock custom = AbyssBlocks.CHARRED_PLANKS.getBaseBlock();
            custom.setBlock(limitedRegion, x, y, z);
            return;
        }else if(MaterialSetTag.SMALL_FLOWERS.isTagged(m) || MaterialSetTag.TALL_FLOWERS.isTagged(m)){
            if(b.getBlockData() instanceof Bisected bisected){
                if(bisected.getHalf() == Bisected.Half.TOP){
                    limitedRegion.setType(x, y, z, Material.AIR);
                    return;
                }
            }
            if(CruxMath.random().nextBoolean()){
                limitedRegion.setType(x, y, z, Material.AIR);
                return;
            }
            limitedRegion.setType(x, y, z, randomSmallBurntDecoration());
            return;
        }
        if(isOre(m)) return;
        /*for(int adjX = 1; adjX < 2; adjX++){
            for(int adjZ = 1; adjZ < 2; adjZ++){
                if(isLava(x+adjX,y, z+adjZ,limitedRegion) ||
                        isLava(x+adjX,y, z-adjZ,limitedRegion) ||
                        isLava(x-adjX,y, z+adjZ,limitedRegion) ||
                        isLava(x-adjX,y, z-adjZ,limitedRegion)){
                    isNearLava = true;
                    nearLava.addBlock(b);
                    break;
                }
            }
        }*/

        float n = noise.GetNoise(x,y,z);
        Material type;
        if(n > .95f) type = Material.GRAVEL;
        else if(n > .85f) type = Material.STONE;
        else{
            if(magma.GetNoise(x,y,z) > (/*isNearLava ? .2f :*/ .4f)){
                type = Material.MAGMA_BLOCK;
            }else{
                if(n > .7f) type = Material.COBBLESTONE;
                else if(n > .52f) type = Material.CYAN_TERRACOTTA;
                else if(n > .35f) type = Material.GRAY_TERRACOTTA;
                else if(n > .015f) type = Material.BLACKSTONE;
                else type = Material.BASALT;
            }
        }
        limitedRegion.setType(x,y,z,type);
    }
    private boolean isLava(int x, int y, int z, @NotNull LimitedRegion region){
        return region.isInRegion(x,y,z) && region.getType(x,y,z) == Material.LAVA;
    }
}

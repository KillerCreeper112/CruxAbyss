package killercreepr.cruxabyss.core.world.generation.biome;

import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.world.biome.BiomeManager;
import killercreepr.cruxabyss.core.world.generation.populator.GrimPopulator;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.component.BushGroup;
import killercreepr.cruxblocks.api.block.component.BushType;
import killercreepr.cruxblocks.api.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ToxicGrasslandsBiome extends GrimBiome {

    public ToxicGrasslandsBiome(@NotNull GrimPopulator master) {
        super(master);
    }

    public void acceptBiomeSet(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setBiome(BiomeManager.TOXIC_GRASSLANDS, limitedRegion, x,y,z);
    }

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        acceptBiomeSet(worldInfo, random, limitedRegion, x, y, z);
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(!b.isSolid()){
            decorationLogic(worldInfo, random, limitedRegion, x, y, z);
        }
    }

    private void decorationLogic(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        flowerLogic(limitedRegion, x, y, z);
    }

    private boolean flowerLogic(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.isInRegion(x, y-1, z)) return false;
        if(!limitedRegion.getType(x, y-1, z).isSolid()) return false;
        int size = CruxMath.random(2, 4);
        for(int i = 0; i < size; i++){
            if(!limitedRegion.isInRegion(x, y + i, z)) return false;
            Material m = limitedRegion.getType(x, y + i, z);
            if(!m.isEmpty()) return false;
        }
        setFlower(limitedRegion, x, y, z, size, AbyssBlocks.PLAGUE_ROOTS);
        return true;
    }

    private void setDefaultFlower(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setFlower(limitedRegion, x, y, z, CruxMath.random(2, 4), AbyssBlocks.PLAGUE_ROOTS);
    }

    private boolean setFlower(@NotNull LimitedRegion region, int x, int y, int z, int length, @NotNull CruxBlockGroup bushBlock){
        //make sure it's all in region first
        for(int i = 0; i < length; i++){
            if(!region.isInRegion(x, y + i, z)) return false;
        }
        BushGroup bush = bushBlock.getComponents().get(CruxBlockComponents.BUSH_GROUP);
        for(int i = 0; i < length; i++){
            CruxBlock block;
            if(i == 0) block = bush.getBlock(BushType.BOTTOM);
            else if((i+1) == length) block = bush.getBlock(BushType.TOP);
            else block = bush.getBlock(BushType.MIDDLE);
            block.setBlock(region, x, y + i, z);
        }
        return true;
    }
}

package killercreepr.cruxabyss.core.world.generation.biome;

import com.destroystokyo.paper.MaterialTags;
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
        if(!b.isSolid()){
            decorationLogic(worldInfo, random, chunkX, chunkZ, limitedRegion, x, y, z);
            return;
        }

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

    private void decorationLogic(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        fungiLogic(limitedRegion, x, y, z);
    }

    private boolean fungiLogic(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.isInRegion(x, y-1, z)) return false;
        var ground = limitedRegion.getType(x, y-1, z);
        if(!ground.isSolid() || isLiquid(ground)) return false;
        if(!isReplaceable(limitedRegion, x, y, z)) return false;
        if(!CruxMath.testChance(1)) return false;
        if(true){
            if(CruxMath.testChance(2)){
                return attemptFlowerPlace(limitedRegion, x, y, z, 2, AbyssBlocks.EYEWITHER);
            }else if(CruxMath.testChance(5)){
                AbyssBlocks.VEILSTARE.setBlock(limitedRegion, x, y, z);
                return true;
            }else {
                AbyssBlocks.WISPTHISTLE.setBlock(limitedRegion, x, y, z);
                return true;
            }
        }

        CruxBlockGroup block;
        int size;
        if(CruxMath.testChance(25)){
            size = 2;
            block = AbyssBlocks.TALL_PLAGUE_SHROOM;
        }else if(CruxMath.testChance(10)){
            AbyssBlocks.MIREHORN.setBlock(limitedRegion, x, y, z);
            return true;
        }else if(CruxMath.testChance(3)){
            AbyssBlocks.TOXSPORE.setBlock(limitedRegion, x, y, z);
            return true;
        }else return false;
        return false; //attemptFlowerPlace(limitedRegion, x, y, z, size, block);
    }

    private boolean flowerLogic(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.isInRegion(x, y-1, z)) return false;
        var ground = limitedRegion.getType(x, y-1, z);
        if(!ground.isSolid() || isLiquid(ground)) return false;
        if(!isReplaceable(limitedRegion, x, y, z)) return false;

        CruxBlockGroup block;
        int size;

        size = CruxMath.random(2,4);
        block = AbyssBlocks.PLAGUE_ROOTS;
        return attemptFlowerPlace(limitedRegion, x, y, z, size, block);
    }

    public boolean isLiquid(Material m){
        return m == Material.WATER || m == Material.LAVA;
    }

    private boolean attemptFlowerPlace(@NotNull LimitedRegion limitedRegion, int x, int y, int z,
                                       int size, @NotNull CruxBlockGroup block){
        for(int i = 0; i < size; i++){
            if(!isReplaceable(limitedRegion,x, y + i, z)) return false;
        }

        return setFlower(limitedRegion, x, y, z, size, block);
    }

    public boolean isReplaceable(LimitedRegion region, int x, int y, int z){
        if(!region.isInRegion(x, y, z)) return false;
        Block b = region.getBlockState(x, y, z).getBlock();
        if(b.isLiquid()) return false;
        return b.isEmpty() || b.isReplaceable();
    }

    private boolean setFlower(@NotNull LimitedRegion region, int x, int y, int z, int length, @NotNull CruxBlockGroup bushBlock){
        //make sure it's all in region first
        /*for(int i = 0; i < length; i++){
            if(!region.isInRegion(x, y + i, z)) return false;
        }*/
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

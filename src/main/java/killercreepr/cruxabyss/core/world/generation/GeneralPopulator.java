package killercreepr.cruxabyss.core.world.generation;

import killercreepr.cruxabyss.core.world.generation.populator.GrimPopulator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GeneralPopulator extends GrimPopulator {
    @Override
    public void populateXYZ(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(b.getType() == Material.WATER || (b.getBlockData() instanceof Waterlogged w && w.isWaterlogged()) ||
                b.getType() == Material.KELP_PLANT || b.getType() == Material.KELP){
            limitedRegion.setType(x,y,z, Material.LAVA);
        }else if(b.isReplaceable() && b.getType() != Material.SNOW){
            limitedRegion.setType(x,y,z, Material.AIR);
        }else if(b.getType() == Material.ICE) limitedRegion.setType(x,y,z, Material.PACKED_ICE);
    }
}

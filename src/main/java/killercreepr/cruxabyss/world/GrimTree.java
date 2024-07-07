package killercreepr.cruxabyss.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GrimTree {
    public enum Type{
        TEST;
        public @NotNull GrimTree tree(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion){
            return new GrimTree(worldInfo, random, limitedRegion);
        }
    }

    private final Random rand = new Random();
    private final WorldInfo worldInfo;
    private final Random random;
    private final LimitedRegion limitedRegion;

    public GrimTree(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
        this.worldInfo = worldInfo;
        this.random = random;
        this.limitedRegion = limitedRegion;
    }

    public boolean canGrow(int x, int y, int z){
        return limitedRegion.getType(x, y - 1, z).isSolid();
    }

    public void grow(int x, int y, int z){
        Material log = Material.CHERRY_LOG;
        Material leaf = Material.SLIME_BLOCK;
        int size = rand.nextInt(4, 12);
        for(int i = 0; i <= size; i++){
            Location v = new Location(limitedRegion.getWorld(), x, y+i, z);
            if(canPlace(v)) limitedRegion.setType(v, log);
        }
        for(int xx = -3; xx < 3; xx++){
            for(int zz = -3; zz < 3; zz++){
                for(int yy = -3; yy < 3; yy++){
                    Location v = new Location(limitedRegion.getWorld(), x+xx, y+size-yy, z+zz);
                    if(canPlace(v)) limitedRegion.setType(v, leaf);
                }
            }
        }
    }

    private boolean canPlace(@NotNull Location v){
        BlockState s = limitedRegion.getBlockState(v);
        return s.getType().isEmpty() || s.getBlock().isReplaceable();
    }
}

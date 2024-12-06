package killercreepr.cruxabyss.core.world.generation.biome;

import com.destroystokyo.paper.MaterialTags;
import killercreepr.cruxabyss.core.world.generation.populator.GrimPopulator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class GrimBiome{
    public static boolean isOre(@NotNull Material m){
        return MaterialTags.ORES.isTagged(m);
    }

    protected final GrimPopulator master;
    public GrimBiome(@NotNull GrimPopulator master) {
        this.master = master;
    }

    public abstract void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion,
                                int x, int y, int z);

    protected void setBiome(@NotNull Biome biome, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        limitedRegion.setBiome(x, y, z, biome);
        /*CraftLimitedRegion craftLimitedRegion = (CraftLimitedRegion) limitedRegion;
        craftLimitedRegion.setBiome(x,y,z, biome);*/
    }

    protected void setBiome(@NotNull Biome biome, @NotNull LimitedRegion limitedRegion, int x, int y, int z,
                            int xHeight, int yHeight, int zHeight){
        setBiome(biome, limitedRegion, x,y,z, -xHeight, -yHeight, -zHeight, xHeight, yHeight, zHeight);
    }

    protected void setBiome(@NotNull Biome biome, @NotNull LimitedRegion limitedRegion, int x, int y, int z,
                            int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
        CraftLimitedRegion craftLimitedRegion = (CraftLimitedRegion) limitedRegion;
        craftLimitedRegion.setBiome(x,y,z,biome);
        for(int xx = minX; xx < maxX; xx++){
            for(int zz = minZ; zz < maxZ; zz++){
                for(int yy = minY; yy < maxY; yy++){
                    if(craftLimitedRegion.isInRegion(x+xx, y+yy, z+zz)){
                        craftLimitedRegion.setBiome(x+xx,y+yy,z+zz, biome);
                    }
                }
            }
        }
    }

    public void setBiome(@NotNull Biome biome, @NotNull LimitedRegion limitedRegion, int x, int y, int z,
                            int minWidth, int minHeight, int maxWidth, int maxHeight){
        setBiome(biome, limitedRegion, x,y,z, minWidth, minHeight, minWidth, maxWidth, maxHeight, maxWidth);
    }

    protected void setBiome(@NotNull Biome biome, @NotNull LimitedRegion limitedRegion, int x, int y, int z,
                            int width, int height){
        CraftLimitedRegion craftLimitedRegion = (CraftLimitedRegion) limitedRegion;
        for(int xx = -width; xx < width; xx++){
            for(int yy = -height; yy < height; yy++){
                if(craftLimitedRegion.isInRegion(x+xx, y+yy, z+xx)){
                    org.bukkit.block.Biome bio = craftLimitedRegion.getBiome(x+xx, y+yy, z+xx);
                    //todo IDK why this is for now if(bio == org.bukkit.block.Biome.CUSTOM) continue;
                    craftLimitedRegion.setBiome(x+xx,y+yy,z+xx, biome);
                }
            }
        }
    }

    protected void setBiome(@NotNull Biome biome, @NotNull LimitedRegion limitedRegion, int x, int y, int z,
                            int radius){
        setBiome(biome, limitedRegion,x,y,z, radius, radius, radius);
    }
}

package killercreepr.cruxabyss.world.generation.populator;

import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GrimPopulator extends BlockPopulator {
    private final ConcurrentHashMap<Vector, ChunkFunction> DATA = new ConcurrentHashMap<>();
    //private final ConcurrentHashMap.KeySetView<LimitedRegion, Boolean> PREVIOUS = ConcurrentHashMap.newKeySet();
    private final int maxCache;

    public GrimPopulator() {
        maxCache = 1000000;
    }

    public GrimPopulator(int maxCache) {
        this.maxCache = maxCache;
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion){
        populateBeforeFunctions(worldInfo, random, chunkX, chunkZ, limitedRegion);
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int xx = x + (chunkX * 16);
                int zz = z + (chunkZ * 16);
                for(int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight() ; y++){
                    populateXYZ(worldInfo, random, chunkX, chunkZ, limitedRegion, xx, y, zz);
                    boolean noFunction = true;
                    Vector pos = new Vector(xx, y, zz);
                    ChunkFunction function = DATA.get(pos);
                    if(function != null){
                        function.accept(worldInfo, random, limitedRegion);
                        //cache clean up
                        DATA.remove(pos);
                        //Grimline.log(Level.INFO, "Removed from cache. (" + DATA.size() + ")");
                        noFunction = false;
                    }
                    if(noFunction) populateIfNoFunction(worldInfo, random, chunkX, chunkZ, limitedRegion, xx, y, zz);
                }
            }
        }
    }

    /*public void place(@NotNull Structure structure, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, @NotNull Location corner){
        for(Palette p : structure.getPalettes()){
            for(BlockState s : p.getBlocks()){
                int x = corner.getBlockX() + s.getX();
                int y = corner.getBlockY() + s.getY();
                int z = corner.getBlockZ() + s.getZ();
                function(worldInfo, random, limitedRegion, new Vector(x, y, z), new ChunkFunction() {
                    @Override
                    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                        limitedRegion.setType(x, y, z, s.getType());
                        if(limitedRegion.getBlockState(x, y, z) instanceof TileState){
                            limitedRegion.setBlockData(x, y, z, s.getBlockData());
                            limitedRegion.setBlockState(x, y, z, s);
                        }else limitedRegion.setBlockData(x, y, z, s.getBlockData());
                    }
                });
            }
        }
    }*/

    public void populateBeforeFunctions(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion){}

    public void populateIfNoFunction(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion,
                                     int x, int y, int z){}

    public void populateXYZ(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion,
                         int x, int y, int z){}

    /**
     * Uses the function, if it is in the limited region. Otherwise, stores the function in cache.
     */
    public void function(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, @NotNull Vector position, @NotNull ChunkFunction function){
        if(limitedRegion.isInRegion(position.getBlockX(), position.getBlockY(), position.getBlockZ())) {
            function.accept(worldInfo, random, limitedRegion);
            return;
        }
        /*int chunkX = floor(position.getBlockX()) >> 4;
        int chunkZ = floor(position.getBlockZ()) >> 4;*/
        addFunction(position, function);
    }


    // NumberConversions#floor(double), used by Location#getBlockX(), Location#getBlockZ()
    /*public static int floor(double num) {
        int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }*/

    public GrimPopulator addFunction(@NotNull Vector position, @NotNull ChunkFunction function){
        /*if(DATA.size() >= maxCache){
            //Grimline.log(Level.INFO, "Cannot add to cache. Limit reached! (" + DATA.size() + ")");
            return this;
        }*/
        DATA.put(position, function);
        //Grimline.log(Level.INFO, "Added to cache. (" + DATA.size() + ")");
        return this;
    }

    public @Nullable ChunkFunction getFunction(int x, int y, int z){
        return DATA.getOrDefault(new Vector(x, y, z), null);
    }

    public static abstract class ChunkFunction{

        public abstract void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion);
    }
}

package killercreepr.cruxabyss.world.generation;

import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockContainer {
    private final ConcurrentHashMap<Block, Long> stored = new ConcurrentHashMap<>();

    public boolean isEmpty(){
        return stored.isEmpty();
    }

    public void clear(){
        stored.clear();
    }

    public @NotNull ConcurrentHashMap<Block, Long> getStored(){ return stored; }

    public void addBlock(@NotNull Block b, int time){
        stored.put(b, time == -1 ? -1 : System.currentTimeMillis() + (50L * time));
    }

    public void addBlock(@NotNull Block b){
        stored.put(b, System.currentTimeMillis() + (50L * 300));
    }

    public void removeBlock(@NotNull Block b){
        stored.remove(b);
    }

    public boolean hasBlock(@NotNull Block block){
        return stored.containsKey(block);
    }

    public boolean isNear(int x, int y, int z, double distance){
        for(Map.Entry<Block, Long> entry : stored.entrySet()){
            if(distanceSquared(x,y,z,
                    entry.getKey().getX(),entry.getKey().getY(),entry.getKey().getZ()) < (distance * distance)) return true;
        }
        return false;
    }

    public double distanceSquared(int x, int y, int z){
        for(Map.Entry<Block, Long> entry : stored.entrySet()){
            return distanceSquared(x,y,z,
                    entry.getKey().getX(),entry.getKey().getY(),entry.getKey().getZ());
        }
        return -1D;
    }

    public static double distanceSquared(int x, int y, int z, int xx, int yy, int zz){
        return NumberConversions.square(x - xx) + NumberConversions.square(y - yy) + NumberConversions.square(z - zz);
    }

    public static double distanceSquared(double x, double y, double z, double xx, double yy, double zz){
        return NumberConversions.square(x - xx) + NumberConversions.square(y - yy) + NumberConversions.square(z - zz);
    }

    public boolean hasBlock(@NotNull Block block, double distance){
        for(Map.Entry<Block, Long> entry : stored.entrySet()){
            if(entry.getKey().equals(block) ||
                     entry.getKey().getLocation().distanceSquared(block.getLocation()) < (distance * distance)) return true;
        }
        return false;
    }

    public void cleanUp(){
        for(Map.Entry<Block, Long> entry : new HashSet<>(stored.entrySet())){
            if(entry.getValue() != -1 && System.currentTimeMillis() > entry.getValue()) stored.remove(entry.getKey());
        }
    }
}

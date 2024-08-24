package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.loot.WeightedObject;
import killercreepr.crux.util.CruxMath;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface NaturalEntitySpawn extends WeightedObject {
    @Nullable Entity spawn(@NotNull SpawnContext ctx);
    int getGroupSize(@NotNull SpawnContext ctx);
    int getGroupRadius(@NotNull SpawnContext ctx);
    boolean canSpawn(@NotNull SpawnContext ctx);

    static @NotNull List<Entity> spawn(@NotNull Collection<NaturalEntitySpawn> poll, @NotNull SpawnContext ctx){
        List<Entity> list = new ArrayList<>();
        for(NaturalEntitySpawn s : poll){
            int spawned = 0;
            int maxGroup = s.getGroupSize(ctx);
            int groupRadius = s.getGroupRadius(ctx);
            for(int i = 0; i < maxGroup; i++){
                Entity e;
                if(spawned < 1){
                    if(s.canSpawn(ctx)) e = s.spawn(ctx);
                    else break;
                }else{
                    e = spawnGroup(groupRadius, ctx, s);
                }
                if(e == null && spawned == 0) break;
                if(e != null){
                    spawned++;
                    list.add(e);
                }
            }
        }
        return list;
    }

    static Entity spawnGroup(int groupRadius, @NotNull SpawnContext ctx, @NotNull NaturalEntitySpawn s){
        Block center = ctx.getBlock();
        for(int x = groupRadius; x >= -groupRadius; --x) {
            for(int y = groupRadius; y >= -groupRadius; --y) {
                for(int z = groupRadius; z >= -groupRadius; --z) {
                    Block b = center.getRelative(x,y,z);
                    if(b.equals(center)) continue;
                    SpawnContext groupInfo = SpawnContext.simple(b, ctx.getRandom());
                    if(s.canSpawn(groupInfo)){
                        return s.spawn(groupInfo);
                    }
                }
            }
        }
        return null;
    }

    static @NotNull List<NaturalEntitySpawnGroup> randomContainer(@NotNull Collection<NaturalEntitySpawnGroup> poll, int rolls, @NotNull SpawnContext ctx){
        List<NaturalEntitySpawnGroup> list = new ArrayList<>();
        Map<NaturalEntitySpawnGroup, Integer> data = new HashMap<>();
        for(NaturalEntitySpawnGroup p : poll){
            data.put(p, p.getWeight());
        }
        for(int i = 0; i < rolls; i++){
            int totalWeight = 0;
            int weight;
            for(NaturalEntitySpawnGroup item : new HashSet<>(data.keySet())){
                if(!item.canSpawn(ctx)){
                    data.remove(item);
                    continue;
                }
                weight = item.getWeight();
                if(weight < 1){
                    data.remove(item);
                    continue;
                }
                totalWeight += weight;
                data.put(item, weight);
            }
            int chance = CruxMath.random(0, totalWeight);
            for(Map.Entry<NaturalEntitySpawnGroup, Integer> entry : new HashSet<>(data.entrySet())){
                if(chance <= entry.getValue()){
                    data.remove(entry.getKey());
                    list.add(entry.getKey());
                    break;
                }
                chance -= entry.getValue();
            }
        }
        return list;
    }
}

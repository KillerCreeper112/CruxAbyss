package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.loot.WeightedObject;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface NaturalEntitySpawnGroup extends WeightedObject {
    boolean canSpawn(@NotNull SpawnContext ctx);
    @NotNull Collection<NaturalEntitySpawn> selectRandom(int rolls, @NotNull SpawnContext ctx);
    @NotNull Collection<NaturalEntitySpawn> getAllAvailableSpawns();

    static @NotNull List<Entity> spawn(@NotNull Collection<? extends NaturalEntitySpawn> poll, @NotNull SpawnContext ctx){
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

    static @Nullable Entity spawnGroup(int groupRadius, @NotNull SpawnContext ctx, @NotNull NaturalEntitySpawn s){
        for(int x = groupRadius; x >= -groupRadius; --x) {
            for(int y = groupRadius; y >= -groupRadius; --y) {
                for(int z = groupRadius; z >= -groupRadius; --z) {
                    Block b = ctx.getBlock().getRelative(x,y,z);
                    if(b.equals(ctx.getBlock())) continue;
                    SpawnContext groupCtx = SpawnContext.simple(b, ctx.getRandom());
                    if(s.canSpawn(groupCtx)){
                        return s.spawn(groupCtx);
                    }
                }
            }
        }
        return null;
    }
}

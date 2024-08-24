package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.util.CruxEntity;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxworlds.world.entity.entity.NaturalEntitySpawn;
import killercreepr.cruxworlds.world.entity.entity.impl.SimpleNaturalEntitySpawnGroup;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class AbyssNaturalEntitySpawnGroup extends SimpleNaturalEntitySpawnGroup {
    public AbyssNaturalEntitySpawnGroup(int weight, float quality, @NotNull Collection<NaturalEntitySpawn> spawns) {
        super(weight, quality, spawns);
    }

    public AbyssNaturalEntitySpawnGroup(int weight, float quality, @NotNull NaturalEntitySpawn... spawns) {
        super(weight, quality, spawns);
    }

    protected int getEntityAmountNearChunk(@NotNull Chunk chunk, int radius){
        return getEntityAmountNearChunk(chunk, radius, null);
    }

    protected int getEntityAmountNearChunk(@NotNull Chunk chunk, int radius, @Nullable Predicate<Entity> predicate){
        return CruxEntity.getEntityAmountNearChunk(chunk, radius, e ->{
            if(predicate != null && !predicate.test(e)) return false;
            for(NaturalEntitySpawn s : spawns){
                if(!(s instanceof AbyssNaturalEntitySpawn mob)) continue;
                if(CruxMob.is(e, mob.getMob())) return true;
            }
            return false;
        });
    }
}

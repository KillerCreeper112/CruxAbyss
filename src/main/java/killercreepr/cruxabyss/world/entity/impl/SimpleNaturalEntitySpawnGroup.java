package killercreepr.cruxabyss.world.entity.impl;

import killercreepr.crux.loot.impl.SimpleWeighted;
import killercreepr.crux.util.CruxEntity;
import killercreepr.crux.util.CruxWeightedSupplier;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawn;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxabyss.world.entity.SpawnContext;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public abstract class SimpleNaturalEntitySpawnGroup extends SimpleWeighted implements NaturalEntitySpawnGroup {
    protected final @NotNull Collection<NaturalEntitySpawn> spawns;
    public SimpleNaturalEntitySpawnGroup(int weight, float quality, @NotNull Collection<NaturalEntitySpawn> spawns) {
        super(weight, quality);
        this.spawns = spawns;
    }

    public SimpleNaturalEntitySpawnGroup(int weight, float quality, @NotNull NaturalEntitySpawn... spawns) {
        this(weight, quality, Set.of(spawns));
    }

    @NotNull
    @Override
    public Collection<NaturalEntitySpawn> selectRandom(int rolls, @NotNull SpawnContext ctx) {
        return CruxWeightedSupplier.builder(spawns)
            .rolls(rolls)
            .filter(check -> check.canSpawn(ctx))
            .build().rollList();
    }

    @NotNull
    @Override
    public Collection<NaturalEntitySpawn> getAllAvailableSpawns() {
        return spawns;
    }

    protected int getEntityAmountNearChunk(@NotNull Chunk chunk, int radius){
        return getEntityAmountNearChunk(chunk, radius, null);
    }

    /**
     * Convenience method for AbyssNaturalEntitySpawns
     */
    protected int getEntityAmountNearChunk(@NotNull Chunk chunk, int radius, @Nullable Predicate<Entity> predicate){
        return CruxEntity.getEntityAmountNearChunk(chunk, radius, e ->{
            if(predicate != null && !predicate.test(e)) return false;
            for(NaturalEntitySpawn s : spawns){
                if(!(s instanceof AbyssNaturalEntitySpawn abyss)) continue;
                if(CruxMob.is(e, abyss.getMob())) return true;
            }
            return false;
        });
    }
}

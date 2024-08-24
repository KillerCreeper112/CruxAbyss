package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.loot.api.WeightedObject;
import org.jetbrains.annotations.NotNull;

public interface NaturalEntitySpawnGroup extends WeightedObject {
    boolean canSpawn(@NotNull SpawnContext ctx);
}

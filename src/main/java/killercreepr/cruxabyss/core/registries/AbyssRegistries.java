package killercreepr.cruxabyss.core.registries;

import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.registry.Registry;
import killercreepr.crux.core.registry.SimpleRegistry;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;

public class AbyssRegistries {
    public static final Registry<NaturalEntitySpawnGroup> ABYSS_NATURAL_ENTITY_SPAWN_GROUP = SimpleRegistry.fromSet();
    public static final KeyedRegistry<OutpostUpgrade> OUTPOST_UPGRADE = KeyedRegistry.keyedRegistry();
}

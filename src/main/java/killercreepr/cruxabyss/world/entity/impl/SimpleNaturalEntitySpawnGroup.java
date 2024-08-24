package killercreepr.cruxabyss.world.entity.impl;

import killercreepr.crux.loot.SimpleWeighted;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawnGroup;

public abstract class SimpleNaturalEntitySpawnGroup extends SimpleWeighted implements NaturalEntitySpawnGroup {
    public SimpleNaturalEntitySpawnGroup(int weight, float quality) {
        super(weight, quality);
    }
}

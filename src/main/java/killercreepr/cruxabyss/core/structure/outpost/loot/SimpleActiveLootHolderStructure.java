package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.cruxabyss.api.structure.ActiveLootHolderStructure;
import killercreepr.cruxabyss.api.structure.StoredLootHolderStructure;
import killercreepr.cruxstructures.core.structure.active.SimpleActiveStructure;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class SimpleActiveLootHolderStructure extends SimpleActiveStructure implements ActiveLootHolderStructure {
    public SimpleActiveLootHolderStructure(@NotNull StoredLootHolderStructure stored, @NotNull Chunk chunk) {
        super(stored, chunk);
    }

    @NotNull
    @Override
    public StoredLootHolderStructure getData() {
        return (StoredLootHolderStructure) super.getData();
    }

    @Override
    public void tick() {
        super.tick();
    }
}

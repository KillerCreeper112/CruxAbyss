package killercreepr.cruxabyss.api.structure;

import killercreepr.cruxstructures.api.structure.Structure;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LootHolderStructure extends Structure {
    @Override
    @Nullable StoredLootHolderStructure buildStored(@NotNull Location center, double rotation);
}

package killercreepr.cruxabyss.api.structure;

import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StoredLootHolderStructure extends StoredStructure {
    @Override
    @Nullable ActiveLootHolderStructure buildActive(@NotNull Chunk chunk);
    long getLastLootGenerateTime();
    void setLastLootGenerateTime(long lastLootGenerateTime);
}

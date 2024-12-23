package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxabyss.api.structure.ActiveLootHolderStructure;
import killercreepr.cruxabyss.api.structure.StoredLootHolderStructure;
import killercreepr.cruxstructures.api.structure.Structure;
import killercreepr.cruxstructures.core.structure.stored.CfgStoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleStoredLootHolderStructure extends CfgStoredStructure implements StoredLootHolderStructure {
    protected long lastLootGenerateTime;
    public SimpleStoredLootHolderStructure(@NotNull Structure structure, @NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation, @Nullable BoundingBox innerBox) {
        super(structure, chunk, center, rotation, innerBox);
    }

    public SimpleStoredLootHolderStructure(@NotNull Key structureKey, @NotNull StoredChunk chunk, @NotNull CruxPosition center, @NotNull BoundingBox boundingBox, double rotation, @Nullable BoundingBox innerBox) {
        super(structureKey, chunk, center, boundingBox, rotation, innerBox);
    }

    public long getLastLootGenerateTime() {
        return lastLootGenerateTime;
    }

    public void setLastLootGenerateTime(long lastLootGenerateTime) {
        this.lastLootGenerateTime = lastLootGenerateTime;
    }

    @Override
    public @Nullable ActiveLootHolderStructure buildActive(@NotNull Chunk chunk) {
        return new SimpleActiveLootHolderStructure(this, chunk);
    }
}

package killercreepr.cruxabyss.structure;

import killercreepr.crux.data.StoredChunk;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.cruxstructures.structure.Structure;
import killercreepr.cruxstructures.structure.active.ActiveStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoredAbyssOutpost extends SimpleStoredStructure {
    protected boolean persist = true;
    public StoredAbyssOutpost(@NotNull Structure structure, @NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation) {
        super(structure, chunk, center, rotation);
    }

    public StoredAbyssOutpost(@NotNull Key structureKey, @NotNull StoredChunk chunk, @NotNull CruxPosition center, @NotNull BoundingBox boundingBox, double rotation) {
        super(structureKey, chunk, center, boundingBox, rotation);
    }

    @Override
    public @Nullable ActiveStructure buildActive(@NotNull Chunk chunk) {
        return new ActiveAbyssOutpost(this, chunk);
    }


    @Override
    public boolean shouldPersist() {
        return persist;
    }
}

package killercreepr.cruxabyss.structure;

import killercreepr.crux.data.BlockPos;
import killercreepr.crux.data.StoredChunk;
import killercreepr.cruxstructures.structure.Structure;
import killercreepr.cruxstructures.structure.active.ActiveStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssOutpost extends SimpleStoredStructure {
    protected int lifeSpan = 200;
    public AbyssOutpost(@NotNull Structure structure, @NotNull StoredChunk chunk, @NotNull BlockPos center, double rotation) {
        super(structure, chunk, center, rotation);
    }

    public AbyssOutpost(@NotNull Key structureKey, @NotNull StoredChunk chunk, @NotNull BlockPos center, @NotNull BoundingBox boundingBox, double rotation) {
        super(structureKey, chunk, center, boundingBox, rotation);
    }

    @Override
    public @Nullable ActiveStructure buildActive(@NotNull Chunk chunk) {
        return new ActiveAbyssOutpost(this, chunk);
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }
}

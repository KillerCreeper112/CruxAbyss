package killercreepr.cruxabyss.core.structure;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.crux.core.util.CruxedBoundingBox;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.InnerBoxedStructure;
import killercreepr.cruxstructures.api.structure.Structure;
import killercreepr.cruxstructures.core.structure.stored.SimpleStoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoredAbyssSafezone extends SimpleStoredStructure implements InnerBoxedStructure {
    public static StoredAbyssSafezone createNew(@NotNull Structure structure, @NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation,
                                                Vector expand){
        BoundingBox inner = calculateInnerBoundingBox(center, structure, rotation);
        BoundingBox outer = calculateOuterBox(inner, expand);

        return new StoredAbyssSafezone(
            structure.key(), chunk, center, outer, rotation, inner
        );
    }

    protected final BoundingBox innerBox;

    public StoredAbyssSafezone(@NotNull Structure structure, @NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation, BoundingBox innerBox) {
        super(structure, chunk, center, rotation);
        this.innerBox = innerBox;
    }

    public StoredAbyssSafezone(@NotNull Key structureKey, @NotNull StoredChunk chunk, @NotNull CruxPosition center, @NotNull BoundingBox boundingBox, double rotation, BoundingBox innerBox) {
        super(structureKey, chunk, center, boundingBox, rotation);
        this.innerBox = innerBox;
    }

    public static @NotNull BoundingBox calculateInnerBoundingBox(@NotNull CruxPosition center, @NotNull Structure structure, double rotation) {
        CruxPosition origin = structure.originPos();
        return CruxedBoundingBox.wrap(structure.boundingBox()).moveTo(origin, center)
            .rotateY(rotation, center.x() + 0.5, center.y() + 0.5, center.z() + 0.5)
            .box();
    }

    public static @NotNull BoundingBox calculateOuterBox(@NotNull BoundingBox box, Vector expand) {
        return box.clone().expand(expand);
    }

    @Override
    public @Nullable ActiveStructure buildActive(@NotNull Chunk chunk) {
        return new ActiveAbyssSafezone(this, chunk);
    }

    @Override
    public @NotNull BoundingBox getInnerBox() {
        return innerBox;
    }
}

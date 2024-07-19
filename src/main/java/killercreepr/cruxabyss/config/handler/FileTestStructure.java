package killercreepr.cruxabyss.config.handler;

import killercreepr.cruxabyss.structure.StoredTestStructure;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxstructures.config.FileSimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileTestStructure extends FileSimpleStoredStructure<StoredTestStructure> {
    @Override
    public @Nullable StoredTestStructure deserializeFromFile(@NotNull FileContext<?> context, @NotNull FileElement e) {
        SimpleStoredStructure simple = super.deserializeFromFile(context, e);
        if(simple == null) return null;
        return new StoredTestStructure(simple.getStructureKey(), simple.getChunk(), simple.getPosition(), simple.getBoundingBox(), simple.getRotation());
    }

    @Override
    public @NotNull String jsonSerializerID() {
        return "test_structure";
    }
}

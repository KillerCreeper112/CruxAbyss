package killercreepr.cruxabyss.core.config.handler;

import killercreepr.cruxabyss.core.structure.StoredAbyssSafezone;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.config.FileSimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileAbyssSafezone extends FileSimpleStoredStructure<StoredAbyssSafezone> {
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> context, @NotNull StoredAbyssSafezone object) {
        FileObject o = (FileObject) super.serializeToFile(context, object);
        FileRegistry registry = context.getRegistry();

        FileObject data = new FileObject();
        data.add("bounding_box", registry.serializeToFile(object.getBoundingBox()));
        data.add("inner_box", registry.serializeToFile(object.getInnerBox()));
        o.add("data", data);
        return o;
    }

    public @Nullable StoredAbyssSafezone deserializeFromFile(@NotNull FileContext<?> context, @NotNull FileElement e){
        FileRegistry registry = context.getRegistry();
        SimpleStoredStructure simple = super.deserializeFromFile(context, e);
        if(simple == null) return null;
        FileObject o = e.getAsFileObject().get("data").getAsFileObject();
        BoundingBox boundingBox = registry.deserializeFromFile(BoundingBox.class, o.get("bounding_box"));
        BoundingBox innerBox = registry.deserializeFromFile(BoundingBox.class, o.get("inner_box"));

        return new StoredAbyssSafezone(
            simple.getStructureKey(), simple.getChunk(),
            simple.getPosition(),
            boundingBox, simple.getRotation(), innerBox
        );
    }

    @Override
    public @NotNull String jsonSerializerID() {
        return "abyss_safezone";
    }
}

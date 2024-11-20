package killercreepr.cruxabyss.config.handler;

import killercreepr.cruxabyss.structure.StoredAbyssOutpost;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.config.FileSimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class FileAbyssOutpost extends FileSimpleStoredStructure<StoredAbyssOutpost> {
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> context, @NotNull StoredAbyssOutpost object) {
        FileObject o = (FileObject) super.serializeToFile(context, object);
        FileRegistry registry = context.getRegistry();

        FileObject data = new FileObject();
        if(object.owner != null){
            data.add("owner", registry.serializeToFile(object.owner));
        }
        o.add("data", data);
        //o.addProperty("lifeSpan", object.getLifeSpan());
        return o;
    }

    public @Nullable StoredAbyssOutpost deserializeFromFile(@NotNull FileContext<?> context, @NotNull FileElement e){
        FileRegistry registry = context.getRegistry();
        SimpleStoredStructure simple = super.deserializeFromFile(context, e);
        if(simple == null) return null;
        FileObject o = (FileObject) e;
        StoredAbyssOutpost outpost = new StoredAbyssOutpost(simple.getStructureKey(), simple.getChunk(),
            simple.getPosition(), simple.getBoundingBox(), simple.getRotation());
        if(!(o.get("data") instanceof FileObject data)) return outpost;
        outpost.owner = registry.deserializeFromFile(UUID.class, data.get("owner"));
        return outpost;
    }

    @Override
    public @NotNull String jsonSerializerID() {
        return "abyss_outpost";
    }
}

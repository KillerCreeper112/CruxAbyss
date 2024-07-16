package killercreepr.cruxabyss.config.handler;

import killercreepr.cruxabyss.structure.StoredAbyssOutpost;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.config.FileSimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileAbyssOutpost extends FileSimpleStoredStructure<StoredAbyssOutpost> {
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> context, @NotNull StoredAbyssOutpost object) {
        FileObject o = (FileObject) super.serializeToFile(context, object);
        //o.addProperty("lifeSpan", object.getLifeSpan());
        return o;
    }

    public @Nullable StoredAbyssOutpost deserializeFromFile(@NotNull FileContext<?> context, @NotNull FileElement e){
        SimpleStoredStructure simple = super.deserializeFromFile(context, e);
        if(simple == null) return null;
        FileObject o = (FileObject) e;
        StoredAbyssOutpost outpost = new StoredAbyssOutpost(simple.getStructureKey(), simple.getChunk(), simple.getPosition(), simple.getBoundingBox(), simple.getRotation());
        //outpost.setLifeSpan(o.get("lifeSpan").getAsInt());
        return outpost;
    }

    @Override
    public @NotNull String jsonSerializerID() {
        return "abyss_outpost";
    }
}

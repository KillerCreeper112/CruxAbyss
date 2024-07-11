package killercreepr.cruxabyss.config.handler;

import killercreepr.cruxabyss.structure.AbyssOutpost;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.config.FileSimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileAbyssOutpost extends FileSimpleStoredStructure<AbyssOutpost> {
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> context, @NotNull AbyssOutpost object) {
        FileObject o = (FileObject) super.serializeToFile(context, object);
        o.addProperty("lifeSpan", object.getLifeSpan());
        return o;
    }

    public @Nullable AbyssOutpost deserializeFromFile(@NotNull FileContext<?> context, @NotNull FileElement e){
        SimpleStoredStructure simple = super.deserializeFromFile(context, e);
        if(simple == null) return null;
        FileObject o = (FileObject) e;
        AbyssOutpost outpost = new AbyssOutpost(simple.getStructureKey(), simple.getChunk(), simple.getBlockPos(), simple.getBoundingBox());
        outpost.setLifeSpan(o.get("lifeSpan").getAsInt());
        return outpost;
    }

    @Override
    public @NotNull String jsonSerializerID() {
        return "abyss_outpost";
    }
}

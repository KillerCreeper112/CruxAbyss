package killercreepr.cruxabyss.core.config.handler;

import killercreepr.cruxabyss.api.structure.StoredLootHolderStructure;
import killercreepr.cruxabyss.core.structure.outpost.StoredAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.loot.SimpleStoredLootHolderStructure;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.core.config.FileCfgStoredStructure;
import killercreepr.cruxstructures.core.config.FileSimpleStoredStructure;
import killercreepr.cruxstructures.core.structure.stored.CfgStoredStructure;
import killercreepr.cruxstructures.core.structure.stored.SimpleStoredStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class FileLootHolderStructure extends FileCfgStoredStructure<StoredLootHolderStructure> {
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> context, @NotNull StoredLootHolderStructure object) {
        FileObject o = (FileObject) super.serializeToFile(context, object);
        FileRegistry registry = context.getRegistry();
        FileObject data = new FileObject();
        data.addProperty("last_loot_generate_time", object.getLastLootGenerateTime());
        o.add("data", data);
        return o;
    }

    public @Nullable StoredLootHolderStructure deserializeFromFile(@NotNull FileContext<?> context, @NotNull FileElement e){
        FileRegistry registry = context.getRegistry();
        CfgStoredStructure simple = super.deserializeSimple(context, e);
        if(simple == null) return null;
        FileObject o = (FileObject) e;
        StoredLootHolderStructure outpost = new SimpleStoredLootHolderStructure(simple.getStructureKey(), simple.getChunk(),
            simple.getPosition(), simple.getBoundingBox(), simple.getRotation(), simple.getInnerBox());
        if(!(o.get("data") instanceof FileObject data)) return outpost;
        Number lastGenerate = registry.deserializeFromFile(Number.class, data.get("last_loot_generate_time"));
        if(lastGenerate != null) outpost.setLastLootGenerateTime(lastGenerate.longValue());
        return outpost;
    }

    @Override
    public @NotNull String jsonSerializerID() {
        return "abyss_outpost";
    }
}

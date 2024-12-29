package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.api.component.StructureComponent;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.core.structure.component.StructureTickedStoredComponent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AbyssOutpostLootHolder extends StructureTickedStoredComponent implements StructureComponent {
    @Override
    public void onCreated(@NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation, @NotNull StoredStructure stored) {
        stored.set(AbyssComponents.ABYSS_OUTPOST_LOOT_HOLDER_DATA, new AbyssOutpostLootHolderData());
    }

    @Override
    public void onFileLoad(@NotNull FileContext<?> context, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = context.getRegistry();
        AbyssOutpostLootHolderData data = new AbyssOutpostLootHolderData();
        data.hologramUUID = reg.deserializeFromFile(UUID.class, o.get("hologramUUID"));
        Long x = reg.deserializeFromFile(Long.class, o.get("nextGeneration"));
        if(x != null) data.nextGeneration = x;
        x = reg.deserializeFromFile(Long.class, o.get("lastGenerated"));
        if(x != null) data.lastGenerated = x;
        structure.set(AbyssComponents.ABYSS_OUTPOST_LOOT_HOLDER_DATA, data);
    }
}

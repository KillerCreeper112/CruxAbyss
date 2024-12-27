package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
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
        AbyssOutpostData outpostData = new AbyssOutpostData();
        outpostData.owner = reg.deserializeFromFile(UUID.class, o.get("owner"));
        structure.set(AbyssComponents.ABYSS_OUTPOST_DATA, outpostData);
    }
}

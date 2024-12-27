package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostLootHolderData implements StoredStructureComponent {
    public long lastGenerated;
    public long nextGeneration;
    @Override
    public void onFileSave(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = ctx.getRegistry();
        o.addProperty("last_generated", lastGenerated);
    }

    @Override
    public void onActiveCreated(@NotNull ActiveStructure structure) {
        structure.set(AbyssComponents.ACTIVE_ABYSS_OUTPOST_LOOT_HOLDER, new ActiveAbyssOutpostLootHolder(structure));
    }
}

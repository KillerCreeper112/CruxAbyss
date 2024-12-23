package killercreepr.cruxabyss.core.structure.outpost.loot;

import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.cruxabyss.api.structure.LootHolderStructure;
import killercreepr.cruxabyss.api.structure.StoredLootHolderStructure;
import killercreepr.cruxstructures.api.structure.module.StructureModule;
import killercreepr.cruxstructures.core.structure.CfgFAWEStructure;
import killercreepr.cruxstructures.core.structure.stored.CfgStoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class SimpleLootHolderStructure extends CfgFAWEStructure implements LootHolderStructure {
    public SimpleLootHolderStructure(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, holder, persistent, beforePlacementModules, modules);
    }

    public SimpleLootHolderStructure(@NotNull Key key, @NotNull String filename, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, filename, persistent, beforePlacementModules, modules);
    }

    public SimpleLootHolderStructure(@NotNull Key key, @NotNull File schematicFile, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, schematicFile, persistent, beforePlacementModules, modules);
    }

    @Override
    public @Nullable StoredLootHolderStructure buildStored(@NotNull Location center, double rotation) {
        CfgStoredStructure built = (CfgStoredStructure) super.buildStored(center, rotation);
        return new SimpleStoredLootHolderStructure(
            built.getStructureKey(), built.getChunk(), built.getPosition(), built.getBoundingBox(), built.getRotation(), built.getInnerBox()
        );
    }
}

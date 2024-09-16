package killercreepr.cruxabyss.structure;

import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.crux.data.StoredChunk;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.cruxstructures.structure.impl.CfgStoredBlocksStructure;
import killercreepr.cruxstructures.structure.module.StructureModule;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class AbyssOutpost extends CfgStoredBlocksStructure {
    public AbyssOutpost(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, holder, persistent, beforePlacementModules, modules);
    }

    public AbyssOutpost(@NotNull Key key, @NotNull String filename, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, filename, persistent, beforePlacementModules, modules);
    }

    public AbyssOutpost(@NotNull Key key, @NotNull File schematicFile, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, schematicFile, persistent, beforePlacementModules, modules);
    }

    @Override
    public @Nullable StoredStructure buildStored(@NotNull Location center, double rotation) {
        return new StoredAbyssOutpost(this, StoredChunk.from(center), CruxPosition.block(center), rotation);
    }
}

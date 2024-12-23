package killercreepr.cruxabyss.core.structure;

import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.structure.module.StructureModule;
import killercreepr.cruxstructures.core.structure.CfgStoredBlocksStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class TestStructure extends CfgStoredBlocksStructure {
    public TestStructure(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, holder, persistent, beforePlacementModules, modules);
    }

    public TestStructure(@NotNull Key key, @NotNull String filename, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, filename, persistent, beforePlacementModules, modules);
    }

    public TestStructure(@NotNull Key key, @NotNull File schematicFile, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules) {
        super(key, schematicFile, persistent, beforePlacementModules, modules);
    }

    @Override
    public @Nullable StoredStructure buildStored(@NotNull Location center, double rotation) {
        return new StoredTestStructure(this, StoredChunk.from(center), CruxPosition.block(center), rotation);
    }
}

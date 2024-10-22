package killercreepr.cruxabyss.structure;

import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.crux.data.StoredChunk;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.cruxstructures.structure.impl.CfgFAWEStructure;
import killercreepr.cruxstructures.structure.module.StructureModule;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class AbyssSafezone extends CfgFAWEStructure {
    protected final Vector expandBox;
    public AbyssSafezone(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules, Vector expandBox) {
        super(key, holder, persistent, beforePlacementModules, modules);
        this.expandBox = expandBox;
    }

    public AbyssSafezone(@NotNull Key key, @NotNull String filename, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules, Vector expandBox) {
        super(key, filename, persistent, beforePlacementModules, modules);
        this.expandBox = expandBox;
    }

    public AbyssSafezone(@NotNull Key key, @NotNull File schematicFile, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules, Vector expandBox) {
        super(key, schematicFile, persistent, beforePlacementModules, modules);
        this.expandBox = expandBox;
    }

    @Override
    public @Nullable StoredStructure buildStored(@NotNull Location center, double rotation) {
        return StoredAbyssSafezone.createNew(
            this, StoredChunk.from(center), CruxPosition.block(center), rotation, expandBox
        );
    }
}

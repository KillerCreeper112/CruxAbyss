package killercreepr.cruxabyss.structure;

import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.crux.data.BlockPos;
import killercreepr.crux.data.StoredChunk;
import killercreepr.cruxstructures.structure.active.ActiveStructure;
import killercreepr.cruxstructures.structure.impl.CfgFAWEStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class TestStructure extends CfgFAWEStructure {

    public TestStructure(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent) {
        super(key, holder, persistent);
    }

    public TestStructure(@NotNull Key key, @NotNull String filename, boolean persistent) {
        super(key, filename, persistent);
    }

    public TestStructure(@NotNull Key key, @NotNull File schematicFile, boolean persistent) {
        super(key, schematicFile, persistent);
    }

    @Override
    public @Nullable StoredStructure buildStored(@NotNull Location center, double rotation) {
        return new SimpleStoredStructure(this, StoredChunk.from(center), BlockPos.from(center), rotation){
            @Override
            public @Nullable ActiveStructure buildActive(@NotNull Chunk chunk) {
                return new ActiveTestStructure(this, chunk);
            }
        };
    }
}

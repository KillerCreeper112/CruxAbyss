package killercreepr.cruxabyss.structure;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import killercreepr.crux.data.BlockPos;
import killercreepr.crux.data.StoredChunk;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.cruxabyss.component.AbyssComponents;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.component.CruxBlockComponents;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.structure.impl.CfgStoredBlocksStructure;
import killercreepr.cruxstructures.structure.module.StructureModule;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AbyssOutpost extends CfgStoredBlocksStructure {
    protected final Collection<BlockPos> conquestNodes = new HashSet<>();
    protected final Collection<BlockPos> spawners = new HashSet<>();
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

    public Collection<BlockPos> getConquestNodes() {
        return conquestNodes;
    }

    public Collection<BlockPos> getSpawners() {
        return spawners;
    }

    @Override
    public void onForEachBlock(Collection<BlockPos> list, BlockVector3 block, BlockState state) {
        super.onForEachBlock(list, block, state);
        if(state.isAir()) return;
        BlockData data = BukkitAdapter.adapt(state);
        CruxBlock crux = CruxCore.inst().cruxBlocks().getBlockRegistry().getByBlockData(data);
        if(crux == null) return;
        if(crux.getComponents().has(AbyssComponents.ABYSS_CONQUEST_NODE)){
            conquestNodes.add(BlockPos.at(block.x(), block.y(), block.z()));
        }
        if(crux.getComponents().has(CruxBlockComponents.ENTITY_SPAWNER)){
            spawners.add(BlockPos.at(block.x(), block.y(), block.z()));
        }
    }
}

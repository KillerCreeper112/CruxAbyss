package killercreepr.cruxabyss.core.structure.outpost;

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

public class AbyssOutpost extends StructureTickedStoredComponent implements StructureComponent {
    @Override
    public void onCreated(@NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation, @NotNull StoredStructure stored) {
        stored.set(AbyssComponents.ABYSS_OUTPOST_DATA, new AbyssOutpostData());
    }

    @Override
    public void onFileLoad(@NotNull FileContext<?> context, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = context.getRegistry();
        AbyssOutpostData outpostData = new AbyssOutpostData();
        outpostData.owner = reg.deserializeFromFile(UUID.class, o.get("owner"));
        structure.set(AbyssComponents.ABYSS_OUTPOST_DATA, outpostData);
    }
    /*protected Collection<BlockPos> conquestNodes = new HashSet<>();
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
        CfgStoredStructure built = (CfgStoredStructure) super.buildStored(center, rotation);
        return new StoredAbyssOutpost(
            built.getStructureKey(), built.getChunk(), built.getPosition(), built.getBoundingBox(), built.getRotation(), built.getInnerBox()
        );
    }

    public Collection<BlockPos> getConquestNodes() {
        return conquestNodes;
    }

    @Override
    public void onForEachBlock(Collection<BlockPos> list, BlockVector3 block, BlockState state) {
        super.onForEachBlock(list, block, state);
        if(state.isAir()) return;
        if(conquestNodes == null) conquestNodes = new HashSet<>();
        BlockData data = BukkitAdapter.adapt(state);
        CruxBlock crux = CruxCore.inst().cruxBlocks().getBlockRegistry().getByBlockData(data);
        if(crux == null) return;
        if(crux.getComponents().has(AbyssComponents.ABYSS_CONQUEST_NODE)){
            conquestNodes.add(BlockPos.at(block.x(), block.y(), block.z()));
        }
    }*/
}

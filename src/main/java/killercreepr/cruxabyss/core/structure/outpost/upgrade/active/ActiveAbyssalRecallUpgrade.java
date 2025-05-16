package killercreepr.cruxabyss.core.structure.outpost.upgrade.active;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.component.parser.DataComponentDecoder;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.BlockPlaceInsideModule;
import killercreepr.cruxabyss.api.structure.outpost.OutpostSnapshotData;
import killercreepr.cruxabyss.api.values.AbyssOutpostUpgradesCfg;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssRecallAnchor;
import killercreepr.cruxabyss.core.teleport.module.TeleportAbyssalRecallModule;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.cruxteleport.api.teleport.TeleportBuildContext;
import killercreepr.cruxteleport.api.teleport.module.TeleportModule;
import killercreepr.usurvive.core.util.RespawnUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ActiveAbyssalRecallUpgrade extends SimpleActiveOutpostUpgrade implements BlockPlaceInsideModule {
    public static class SnapshotBlock{
        public final CruxPosition relativePosition;
        public final BlockState blockState;

        public SnapshotBlock(CruxPosition relativePosition, BlockState blockState) {
            this.relativePosition = relativePosition;
            this.blockState = blockState;
        }
    }

    public static class SnapshotData implements OutpostSnapshotData{
        public final List<SnapshotBlock> anchors;

        public SnapshotData(List<SnapshotBlock> anchors) {
            this.anchors = anchors;
        }
    }

    protected final AbyssOutpostData data;
    public ActiveAbyssalRecallUpgrade(int level, AbyssOutpostData data) {
        super(level);
        this.data = data;
    }

    @Nullable
    @Override
    public OutpostSnapshotData createSnapshotData() {
        List<SnapshotBlock> anchors = new ArrayList<>();
        getRespawnAnchors().forEach(anchor ->{
            Block block = anchor.block();
            if(block == null){
                Crux.logError("AbyssRecallAnchor is null! " + anchor.getPosition());
                return;
            }
            if(anchor.isDestroyed()){
                return;
            }

            CruxPosition blockPos = anchor.getPosition();
            CruxPosition centerStructurePos = data.getStored().getPosition();

            CruxPosition relativePos = blockPos.subtract(centerStructurePos);

            SnapshotBlock snapBlock = new SnapshotBlock(relativePos, block.getState().copy());
            anchors.add(snapBlock);
        });
        if(anchors.isEmpty()) return null;
        return new SnapshotData(anchors);
    }

    @Override
    public void acceptSnapshot(@NotNull OutpostSnapshotData data) {
        if(!(data instanceof SnapshotData merge)) return;
        World world = this.data.getStored().getChunk().toBukkitWorld();
        if(world == null){
            Crux.logError("Cannot place abyss recall anchors! World is null: " + this.data.getStored().getChunk().worldKey());
            return;
        }
        merge.anchors.forEach(block ->{
            CruxPosition pos = this.data.getStored().getPosition()
                .add(block.relativePosition);

            Block b = pos.getBlock(world);
            var state = block.blockState;
            Crux.handlers().block().setType(b, state.getType());
            b.setBlockData(state.getBlockData());
            state.copy(b.getLocation()).update();


            AbyssRecallAnchor recallAnchor = AbyssRecallAnchor.abyssRecallAnchor(pos, this.data);
            addRespawnAnchor(recallAnchor);
        });
    }

    public int getMaxRespawnAnchors(){
        return level;
    }

    public AbyssRecallAnchor wrapAnchor(CruxPosition pos){
        return AbyssRecallAnchor.abyssRecallAnchor(pos, data);
    }

    protected final Map<CruxPosition, AbyssRecallAnchor> respawnAnchors = new HashMap<>();
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.isCancelled()) return;
        Block b = event.getBlock();
        if(b.getType() != Material.RESPAWN_ANCHOR) return;
        if(!isWithinRadius(b)) return;
        Player p = event.getPlayer();
        if(!data.isMemberOrOwner(p.getUniqueId())) return;

        Lang.ABYSS_OUTPOST_UPGRADE_RECALL_CAN_BE_RECALL_ANCHOR.use(p);
    }

    public boolean isWithinRadius(Block b){
        return getBoxRadius().contains(b.getLocation().toVector());
    }

    public BoundingBox getBoxRadius(){
        if(level < 2) return data.getStored().getBoundingBox();
        return data.getStored().getBoundingBox().clone()
            .expand((level-1) * 8);
    }

    public boolean canTeleportToAnchor(Block b){
        if(!(b.getBlockData() instanceof RespawnAnchor anchor)) return false;
        return anchor.getCharges() > 0;
    }

    public boolean onTeleportToAnchor(Block b){
        if(!(b.getBlockData() instanceof RespawnAnchor anchor)) return false;
        if(anchor.getCharges() < 1) return false;
        anchor.setCharges(anchor.getCharges()-1);
        b.setBlockData(anchor);
        CreateSound.sound(Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE).playAt(b.getLocation().toCenterLocation().add(0, .5, 0));
        return true;
    }

    public boolean attemptTeleportToRespawnAnchor(Entity p, Block b){
        if(!canTeleportToAnchor(b)) return false;
        Location loc = RespawnUtil.findRespawnLocationOrTop(b);
        if(loc == null) return false;
        CruxTeleport.Builder builder= CruxTeleport.builder()
            .teleportTo(() ->{
                if(!canTeleportToAnchor(b)) return null;
                return RespawnUtil.findRespawnLocationOrTop(b);
            });
        String components = ((AbyssOutpostUpgradesCfg) CruxAbyss.inst().values()).ABYSS_OUTPOST_UPGRADE_RECALL_TELEPORT_COMPONENTS().value();
        if(components != null){
            DataComponentDecoder.componentDecoder().parseComponents(components).forEach(builder::set);
        }
        builder.set(AbyssComponents.TELEPORT_OUTPOST_ABYSSAL_RECALL, new TeleporterComponent() {
            @NotNull
            @Override
            public TeleportModule buildTeleportModule(@NotNull TeleportBuildContext ctx) {
                return new TeleportAbyssalRecallModule(ActiveAbyssalRecallUpgrade.this, b);
            }
        });

        return CruxTeleporter.teleporter().scheduleTeleport(p, builder.build()) != null;
    }

    public Collection<Block> getRespawnAnchors(World world){
        Collection<Block> list = new HashSet<>();
        respawnAnchors.values().removeIf(pos ->{
            Block b = pos.getPosition().getBlock(world);
            if(b.getType() != Material.RESPAWN_ANCHOR){
                return true;
            }
            list.add(b);
            return false;
        });
        return list;
    }

    @Override
    public void tick(int tick, int rate) {}

    public boolean hasRespawnAnchor(CruxPosition pos){
        return respawnAnchors.containsKey(pos);
    }

    public AbyssRecallAnchor removeRespawnAnchor(CruxPosition pos){
        return respawnAnchors.remove(pos);
    }

    public AbyssRecallAnchor addRespawnAnchor(AbyssRecallAnchor pos){
        return respawnAnchors.put(pos.getPosition(), pos);
    }

    public Collection<AbyssRecallAnchor> getRespawnAnchors() {
        return respawnAnchors.values();
    }

    public void setRespawnAnchors(Collection<CruxPosition> list){
        respawnAnchors.clear();
        if(list==null) return;
        for(CruxPosition pos : list){
            addRespawnAnchor(wrapAnchor(pos));
        }
    }

    @Nullable
    @Override
    public FileElement serialize(@NotNull FileContext<?> ctx) {
        FileObject o = new FileObject();
        o.add("respawn_anchors", ctx.getRegistry().serializeToFile(respawnAnchors.keySet()));
        return o;
    }
}

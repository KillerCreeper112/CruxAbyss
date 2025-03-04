package killercreepr.cruxabyss.core.structure.outpost.upgrade.active;

import killercreepr.crux.api.component.parser.DataComponentDecoder;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.api.structure.BlockPlaceInsideModule;
import killercreepr.cruxabyss.api.values.AbyssOutpostUpgradesCfg;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ActiveAbyssalRecallUpgrade extends SimpleActiveOutpostUpgrade implements BlockPlaceInsideModule {
    protected final AbyssOutpostData data;
    public ActiveAbyssalRecallUpgrade(int level, AbyssOutpostData data) {
        super(level);
        this.data = data;
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
        Player p = event.getPlayer();
        if(!data.isMemberOrOwner(p.getUniqueId())) return;
        if(getRespawnAnchors(b.getWorld()).size() >= getMaxRespawnAnchors()) return;
        addRespawnAnchor(wrapAnchor(CruxPosition.block(b)));
        p.sendMessage("Respawn anchor has been linked to Abyss Outpost Recall upgrade.");
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
        return true;
    }

    public boolean attemptTeleportToRespawnAnchor(Player p, Block b){
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
        o.add("respawn_anchors", ctx.getRegistry().serializeToFile(respawnAnchors));
        return o;
    }
}

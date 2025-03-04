package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssOutpostUpgrades;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveAbyssalRecallUpgrade;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class AbyssOutpostListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b==null) return;
        if(!(b.getBlockData() instanceof RespawnAnchor anchor)) return;
        if(anchor.getCharges() < 1) return;

        ItemStack item = event.getItem();
        if(item != null && item.getType() == Material.GLOWSTONE){
            return;
        }

        CruxWorld crux = CruxCore.core().worldManager().getWorld(b.getWorld().key());
        if(crux==null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module==null) return;
        Vector vec = b.getLocation().toVector();
        StoredStructure stored = module.getFirstStoredAt(
            StoredStructure.class, b, check ->{
                if(!check.has(AbyssComponents.ABYSS_OUTPOST_DATA)) return false;
                BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
                return box.contains(vec);
            }
        );
        if(stored==null) return;
        AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(!(data.getTickedOutpostUpgrade(AbyssOutpostUpgrades.ABYSSAL_RECALL) instanceof ActiveAbyssalRecallUpgrade upgrade)) return;
        if(!data.isMemberOrOwner(p.getUniqueId())) return;

        event.setCancelled(true);
        CruxPosition pos = CruxPosition.block(b);
        if(upgrade.hasRespawnAnchor(pos)){
            upgrade.removeRespawnAnchor(pos);
            Lang.ABYSS_OUTPOST_UPGRADE_RECALL_REMOVED.use(p);
            return;
        }
        if(upgrade.getRespawnAnchors(b.getWorld()).size() >= upgrade.getMaxRespawnAnchors()){
            Lang.ABYSS_OUTPOST_UPGRADE_RECALL_REACHED_MAX.use(p);
            return;
        }
        upgrade.addRespawnAnchor(upgrade.wrapAnchor(pos));
        Lang.ABYSS_OUTPOST_UPGRADE_RECALL_ADDED.use(p);
    }

}

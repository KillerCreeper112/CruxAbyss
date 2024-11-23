package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.component.AbyssAltarItemComponent;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.api.event.CruxBlockPlaceEvent;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.structure.InnerBoxedStructure;
import killercreepr.cruxstructures.structure.impl.CfgStoredBlocksStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class AbyssAltarPortalListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b==null) return;
        if(b.getType() != Material.ENCHANTING_TABLE) return;

        ItemStack item = event.getItem();
        if(item==null) return;
        CruxItem cruxItem = CruxItem.wrap(item);
        Collection<AbyssAltarItemComponent> list = cruxItem.getAllOfType(AbyssAltarItemComponent.class);
        if(list == null || list.isEmpty()) return;
        AbyssAltar altar = AbyssAltar.getFromCenter(b);
        if(altar == null) return;

        event.setCancelled(true);
        for(AbyssAltarItemComponent altarItem : list){
            if(!altarItem.canPlaceOn(altar)) return;

            altarItem.place(altar, p, cruxItem);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        StoredStructure stored = CruxCore.inst().structureManager().getFirstStoredAt(StoredStructure.class, b);
        if (stored == null) return;

        if(stored instanceof InnerBoxedStructure){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxBlockPlace(CruxBlockPlaceEvent event) {
        Block b = event.getContext().getBlock();
        StoredStructure stored = CruxCore.inst().structureManager().getFirstStoredAt(StoredStructure.class, b);
        if (stored == null) return;

        if(stored instanceof InnerBoxedStructure){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxBlockBreak(CruxBlockBreakEvent event) {
        Block b = event.getContext().getBlock();
        StoredStructure stored = CruxCore.inst().structureManager().getFirstStoredAt(StoredStructure.class, b);
        if (stored == null) return;

        if(stored instanceof InnerBoxedStructure){
            event.setCancelled(true);
        }
    }


    //todo proper structure thing for this
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        StoredStructure stored = CruxCore.inst().structureManager().getFirstStoredAt(StoredStructure.class, b);
        if (stored == null) return;

        if(stored instanceof InnerBoxedStructure){
            event.setCancelled(true);
            return;
        }
        CruxPosition blockPos = CruxPosition.block(b);

        if (!(stored.getParent() instanceof CfgStoredBlocksStructure s)) return;
        CruxPosition structurePos = stored.fromWorldToStructurePos(blockPos);
        if(s.getBlocks(stored.getRotation()).contains(structurePos)){
            event.setCancelled(true);
        }
    }
}

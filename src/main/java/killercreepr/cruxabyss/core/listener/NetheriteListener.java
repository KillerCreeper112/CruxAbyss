package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.lang.Lang;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class NetheriteListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        BlockState state = event.getBlockState();
        if(state.getType() != Material.ANCIENT_DEBRIS) return;
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;
        if(Crux.handlers().item().getType(item).equals(Crux.key("minecraft:diamond_pickaxe"))){
            event.getItems().clear();
            Lang.CANNOT_MINE_ANCIENT_DEBRIS.use(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        Block state = event.getBlock();
        if(state.getType() != Material.ANCIENT_DEBRIS) return;
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(CruxItem.isEmpty(item)) return;
        if(Crux.handlers().item().getType(item).equals(Crux.key("minecraft:diamond_pickaxe"))){
            Lang.CANNOT_MINE_ANCIENT_DEBRIS.use(p);
        }
    }

}

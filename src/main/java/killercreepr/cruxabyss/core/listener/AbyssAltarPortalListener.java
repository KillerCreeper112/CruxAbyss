package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.component.AbyssAltarItemComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
        Collection<AbyssAltarItemComponent> list = cruxItem.getAllOfTypeOrDefaultData(AbyssAltarItemComponent.class);
        if(list == null || list.isEmpty()) return;
        AbyssAltar altar = AbyssAltar.getFromCenter(b);
        if(altar == null) return;

        event.setCancelled(true);
        for(AbyssAltarItemComponent altarItem : list){
            if(!altarItem.canPlaceOn(altar)) return;

            altarItem.place(altar, p, cruxItem);
        }
    }
}

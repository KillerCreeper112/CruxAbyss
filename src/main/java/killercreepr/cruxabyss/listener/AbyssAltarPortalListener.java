package killercreepr.cruxabyss.listener;

import killercreepr.crux.util.CruxLoc;
import killercreepr.cruxabyss.altar.AbyssAltar;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.entity.type.AbyssAltarPortal;
import killercreepr.cruxabyss.item.AbyssItemTags;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbyssAltarPortalListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b==null) return;
        if(b.getType() != Material.ENCHANTING_TABLE) return;

        ItemStack item = event.getItem();
        if(item==null) return;
        if(!AbyssItemTags.ABYSS_GEMS.isTagged(item)) return;

        AbyssAltar altar = new AbyssAltar(b);
        if(!altar.isValid()) return;
        ItemStack clonedItem = item.clone();
        if(p.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);

        Location spawn = b.getLocation().toCenterLocation().add(0, 1, 0);
        spawn.getWorld().spawn(spawn, ItemDisplay.class, e ->{
            e.setItemStack(clonedItem);
        });

        BlockFace direction = altar.getDirection();
        Location portalSpawn = b.getLocation();
        portalSpawn.setDirection(direction.getDirection());
        CruxLoc.relative(portalSpawn, 0D, 0D, 2D);

        AbyssMob.ALTAR_PORTAL.spawn(portalSpawn);
    }
}

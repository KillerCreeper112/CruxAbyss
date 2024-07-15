package killercreepr.cruxabyss.listener;

import killercreepr.crux.Crux;
import killercreepr.crux.data.BlockPos;
import killercreepr.crux.util.CruxColor;
import killercreepr.crux.util.CruxLoc;
import killercreepr.cruxabyss.altar.AbyssAltar;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.entity.type.AbyssAltarPortal;
import killercreepr.cruxabyss.item.AbyssItemTags;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.structure.Structure;
import killercreepr.cruxstructures.structure.impl.CfgStoredBlocksStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        BlockPos blockPos = BlockPos.from(b);
        StoredStructure stored = CruxCore.inst().structureManager().getFirstStoredAt(StoredStructure.class, b);
        if (stored == null) return;

        if (!(stored.getParent() instanceof CfgStoredBlocksStructure s)) return;

        BlockPos structurePos = stored.fromWorldToStructurePos(blockPos);
        if(s.getBlocks(stored.getRotation()).contains(structurePos)){
            event.setCancelled(true);
        }
    }
}

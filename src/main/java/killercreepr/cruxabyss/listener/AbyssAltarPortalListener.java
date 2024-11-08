package killercreepr.cruxabyss.listener;

import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.util.CruxGoalUtil;
import killercreepr.cruxabyss.altar.AbyssAltar;
import killercreepr.cruxabyss.entity.goal.AbyssCrystalGoal;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.item.AbyssItemTags;
import killercreepr.cruxblocks.event.CruxBlockBreakEvent;
import killercreepr.cruxblocks.event.CruxBlockPlaceEvent;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.structure.InnerBoxedStructure;
import killercreepr.cruxstructures.structure.impl.CfgStoredBlocksStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import killercreepr.usurvive.world.WorldUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
        if(!AbyssItemTags.ABYSS_GEMS.isTagged(item)) return;

        AbyssAltar altar = new AbyssAltar(b);
        if(!altar.isValid()) return;
        event.setCancelled(true);
        if(altar.getSelectedEntity() != null) return;

        ItemStack clonedItem = item.clone();
        if(p.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);

        if(WorldUtil.getDimensionID(b.getWorld()).equalsIgnoreCase("abyss")){
            b.getWorld().createExplosion(b.getLocation().toCenterLocation(), 4f, true, true);
            return;
        }

        Location spawn = altar.getCenter().getLocation().toCenterLocation().add(0, .5, 0);
        Mob crystalMob = (Mob) AbyssMob.ABYSS_CRYSTAL.spawn(spawn);
        AbyssCrystalGoal goal = CruxGoalUtil.getGoal(crystalMob, AbyssCrystalGoal.class);
        goal.setItem(clonedItem);
        goal.setAltar(altar);
        CreateSound.sound(Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1.5f).playAt(crystalMob);
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

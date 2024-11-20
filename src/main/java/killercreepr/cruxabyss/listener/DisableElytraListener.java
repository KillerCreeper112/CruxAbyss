package killercreepr.cruxabyss.listener;

import killercreepr.crux.core.util.CruxItem;
import killercreepr.usurvive.world.WorldUtil;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class DisableElytraListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if(!event.isGliding()) return;
        if(!(event.getEntity() instanceof Player p)) return;
        if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        if(!WorldUtil.getDimensionID(p.getWorld()).equalsIgnoreCase("abyss")) return;
        ItemStack elytra = p.getInventory().getChestplate();
        if(CruxItem.isEmpty(elytra) || elytra.getType() != Material.ELYTRA) return;
        if(!(elytra.getItemMeta() instanceof Damageable d) || d.getDamage() >= CruxItem.getMaxDurability(elytra)) return;

        elytra.editMeta(Damageable.class, meta -> meta.setDamage(CruxItem.getMaxDurability(elytra)));
        p.playEffect(EntityEffect.BREAK_EQUIPMENT_CHESTPLATE);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Block b = event.getClickedBlock();
        if(b == null) return;
        if(!WorldUtil.getDimensionID(b.getWorld()).equalsIgnoreCase("abyss")) return;
        if(!(b.getBlockData() instanceof Bed)) return;
        event.setCancelled(true);
        b.getWorld().createExplosion(b.getLocation(), 4f);
    }

}

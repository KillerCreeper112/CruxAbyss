package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.world.WorldUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Map;

public class AbyssSpecificsListener implements Listener {
    protected final ValuesProvider cfg;

    public AbyssSpecificsListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if(!event.isGliding()) return;
        if(!(event.getEntity() instanceof Player p)) return;
        if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        CruxWorld world = CruxCore.inst().worldManager().getWorld(p.getWorld().getUID());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;

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
        CruxWorld world = CruxCore.inst().worldManager().getWorld(b.getWorld().getUID());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(!(b.getBlockData() instanceof Bed)) return;
        event.setCancelled(true);
        b.getWorld().createExplosion(b.getLocation(), 4f);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(p.getWorld().getUID());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        Block b = p.getLocation().getBlock();
        if(!isWater(b)) return;
        Key biome = b.getBiome().key();
        Collection<PotionEffect> effects = cfg.ABYSS_WATER_EFFECTS().valueOr(Map.of()).get(biome);
        if(effects != null) effects.forEach(p::addPotionEffect);
    }

    public boolean isWater(Block b){
        Material m = b.getType();
        switch (m){
            case WATER, KELP_PLANT, SEAGRASS, TALL_SEAGRASS ->{
                return true;
            }
        }
        if(b.getBlockData() instanceof Waterlogged l && l.isWaterlogged()) return true;
        return false;
    }

}

package killercreepr.cruxabyss.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AbyssSpecificsListener implements Listener {
    protected final ValuesProvider cfg;

    public AbyssSpecificsListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity e = event.getEntity();
        if(!(e instanceof Mob)) return;
        if(e instanceof Animals){
            animalDeath(e);
        }
    }

    public void animalDeath(Entity e){
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;

        Location loc = e.getLocation().add(0, e.getHeight()/2, 0);
        ValuesProvider cfg = CruxAbyss.inst().values();

        double range = cfg.ANIMAL_DEATH_RANGE().value().doubleValue();//CruxMath.random(.8, 1.2);
        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .location(loc)
            .count(CruxMath.random(10, 20))
            .offset(range, range, range)
            .data(new Particle.DustTransition(Color.fromRGB(0xCCCC0B), Color.fromRGB(0x947E3F), 2f))
            .spawn();

        new GetEntityNear<>(DynamicLocation.createStatic(loc), LivingEntity.class)
            .range(range)
            .find().forEach(victim ->{
                cfg.ANIMAL_DEATH_EFFECTS_NEARBY().valueOr(Set.of()).forEach(victim::addPotionEffect);
            });
        CreateSound.sound(Sound.ITEM_INK_SAC_USE, 1.5f).playAt(e);
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if(!event.isGliding()) return;
        if(!(event.getEntity() instanceof Player p)) return;
        if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        CruxWorld world = CruxCore.inst().worldManager().getWorld(p.getWorld().key());
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
        CruxWorld world = CruxCore.inst().worldManager().getWorld(b.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(!(b.getBlockData() instanceof Bed)) return;
        event.setCancelled(true);
        b.getWorld().createExplosion(b.getLocation(), 4f);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(p.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        Block b = p.getLocation().getBlock();
        if(!isWater(b)) return;
        Key biome = b.getBiome().key();
        Collection<PotionEffect> effects = cfg.ABYSS_WATER_EFFECTS().valueOr(Map.of()).get(biome);
        if(effects != null) effects.forEach(pot ->{
            if(pot.getType() == PotionEffectType.POISON && p.hasPotionEffect(PotionEffectType.POISON)) return;
            if(pot.getType() == PotionEffectType.WITHER && p.hasPotionEffect(PotionEffectType.WITHER)) return;
            p.addPotionEffect(pot);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED){
            double multiplier = cfg.ABYSS_NATURAL_HEALING_MULTIPLIER().value().doubleValue();
            if(multiplier==1D) return;
            event.setAmount(event.getAmount()*multiplier);
        }
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

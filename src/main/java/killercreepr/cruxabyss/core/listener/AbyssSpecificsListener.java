package killercreepr.cruxabyss.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.core.entity.memory.SleeperHolder;
import killercreepr.usurvive.core.util.RespawnUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
        if(!(b.getBlockData() instanceof Bed bed)) return;
        event.setCancelled(true);

        var pair = RespawnUtil.getFullBed(b, bed);
        if(pair == null) return;
        Location middle = getMiddleLocation(pair);
        Crux.handlers().block().setType(pair.getFirst(), Material.AIR);
        Crux.handlers().block().setType(pair.getSecond(), Material.AIR);
        AbyssMob.SLEEPLESS_HORROR.spawn(middle);
        CreateSound.sound(Sound.ENTITY_VEX_CHARGE, 0.6f).playAt(middle);

        //b.getWorld().createExplosion(b.getLocation(), 4f);
    }

    public Location getMiddleLocation(Pair<Block,Block> pair){
        Location loc = pair.getFirst().getLocation().toCenterLocation();
        loc = CruxLoc.shiftToward(loc, pair.getSecond().getLocation().toCenterLocation(), 0.5D);

        if(pair.getFirst().getBlockData() instanceof Bed bed){
            loc.setDirection(bed.getFacing().getDirection().multiply(-1));
        }

        return loc;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDismount(EntityDismountEvent event) {
        var e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        e.setFallDistance(event.getDismounted().getFallDistance());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityMount(EntityMountEvent event) {
        var e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(!(e instanceof LivingEntity living)) return;
        float lastFallDistance = e.getFallDistance();
        if(lastFallDistance <= 0f) return;
        var attribute = living.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if(attribute == null) return;
        double safe = attribute.getValue();
        if(lastFallDistance > safe){
            float dmg = lastFallDistance - (float) safe;
            if(dmg <= 0f) return;
            living.damage(dmg, DamageSource.builder(DamageType.FALL).build());
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        Location to = event.getTo();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(to.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        Location from = event.getFrom();
        if(to.getY() >= from.getY()) return;
        var vehicle = event.getVehicle();
        if(vehicle.getPassengers().isEmpty()) return;
        float fallDistance = vehicle.getFallDistance();
        if(fallDistance <= 0f){
            float lastFallDistance = CruxTag.get(vehicle, "last_fall_distance", PersistentDataType.FLOAT, 0f);
            CruxTag.remove(vehicle, "last_fall_distance");
            if(lastFallDistance <= 0f) return;
            vehicle.getPassengers().forEach(e ->{
                if(!(e instanceof LivingEntity living)) return;
                var attribute = living.getAttribute(Attribute.SAFE_FALL_DISTANCE);
                if(attribute == null) return;
                double safe = attribute.getValue();
                if(lastFallDistance > safe){
                    float dmg = lastFallDistance - (float) safe;
                    if(dmg <= 0f) return;
                    living.damage(dmg, DamageSource.builder(DamageType.FALL).build());
                }
            });
            return;
        }
        CruxTag.set(vehicle, "last_fall_distance", PersistentDataType.FLOAT, fallDistance);
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(p.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        Block b = p.getLocation().getBlock();
        if(!isWater(b)) return;
        if(p.getVehicle() != null){
            if(!isWater(p.getEyeLocation().getBlock())) return;
        }

        Key biome = b.getBiome().key();
        Collection<PotionEffect> effects = cfg.ABYSS_WATER_EFFECTS().valueOr(Map.of()).get(biome);
        if(effects != null){
            effects.forEach(pot ->{
                if(pot.getType().equals(PotionEffectType.POISON) || pot.getType().equals(PotionEffectType.WITHER)){
                    if(p.hasPotionEffect(pot.getType())) return;
                }
                p.addPotionEffect(pot);
            });
        }
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        Entity e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        if(CruxMob.is(e)) return;
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        Location spawn = event.getLocation();
        if(e instanceof AbstractVillager){
            event.setCancelled(true);
            AbyssMob.VILDER.spawn(spawn);
            return;
        }
        if(e instanceof Pillager){
            event.setCancelled(true);
            AbyssMob.SCOURGER.spawn(spawn);
            return;
        }
        if(e instanceof Vindicator){
            event.setCancelled(true);
            AbyssMob.TOXICATOR.spawn(spawn);
            return;
        }
    }

}

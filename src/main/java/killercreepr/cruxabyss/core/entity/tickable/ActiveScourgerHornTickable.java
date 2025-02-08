package killercreepr.cruxabyss.core.entity.tickable;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.bukkit.AttributeBukkitAdaptor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.SimpleActiveEntityTickable;
import killercreepr.usurvive.api.entity.player.UPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

//After sprinting for 3 seconds, release a trail of small spore clouds.
public class ActiveScourgerHornTickable extends SimpleActiveEntityTickable implements Listener {
    protected final CruxAttributeContainer sporeCloudAttributes;
    protected final Collection<PotionEffect> sporeCloudEffects;
    protected final int runUpTime;
    protected final int cooldown;
    protected final int maxSpores;
    protected final int itemDmg;
    protected final int sporeSpawnRate;
    protected final int sporeCloudLifeSpan;
    protected final List<SporeCloud> sporeClouds = new ArrayList<>();
    protected final ParticleBuilderSupplier supplier;
    protected final ParticleBuilder particle;
    protected final UPlayer uPlayer;
    protected final CreateSound sporeSpawnSound;
    public ActiveScourgerHornTickable(Entity entity, EntityTickable tickable,
                                      CruxSlot slot, CruxAttributeContainer sporeCloudAttributes,
                                      Collection<PotionEffect> sporeCloudEffects, int runUpTime, int cooldown, int maxSpores, int itemDmg, int sporeSpawnRate,
                                      int sporeCloudLifeSpan, ParticleBuilderSupplier supplier, CreateSound sporeSpawnSound) {
        super(entity, tickable, slot);
        uPlayer = UPlayer.getPlayer(entity);
        this.sporeCloudAttributes = sporeCloudAttributes;
        this.sporeCloudEffects = sporeCloudEffects;
        this.runUpTime = runUpTime;
        this.cooldown = cooldown;
        this.maxSpores = maxSpores;
        this.itemDmg = itemDmg;
        this.sporeSpawnRate = sporeSpawnRate;
        this.sporeCloudLifeSpan = sporeCloudLifeSpan;
        this.supplier = supplier;
        this.particle = supplier == null ? null : supplier.build();
        this.sporeSpawnSound = sporeSpawnSound;

        this.tickSporeClouds = () ->{
            sporeClouds.removeIf(spore ->{
                if(!CruxMath.hasOccurredWithin(spore.time(), sporeCloudLifeSpan)) return true;
                tickSporeCloud(spore);
                return false;
            });
            tickingSpores = false;
        };
    }

    public boolean setCooldown(int time){
        if(!(entity instanceof Player p)) return false;
        EquipmentSlot equip = AttributeBukkitAdaptor.adapt(slot);
        if(equip==null) return false;
        ItemStack item = p.getInventory().getItem(equip);
        if(CruxItem.isEmpty(item)) return false;
        Crux.scheduler().runTask(() -> p.setCooldown(item, time));
        return true;
    }

    public boolean isOnCooldown(){
        if(!(entity instanceof Player p)) return false;
        EquipmentSlot equip = AttributeBukkitAdaptor.adapt(slot);
        if(equip==null) return false;
        ItemStack item = p.getInventory().getItem(equip);
        if(CruxItem.isEmpty(item)) return false;
        return p.hasCooldown(item);
    }

    public void hit(Entity e){
        hit.put(e.getUniqueId(), System.currentTimeMillis());
    }

    public boolean hitWithin(Entity e, int ticks){
        Long time = hit.get(e.getUniqueId());
        return time != null && CruxMath.hasOccurredWithin(time, ticks);
    }

    protected final Map<UUID, Long> hit = new HashMap<>();
    public void tickSporeCloud(SporeCloud spore){
        Location loc = spore.location();
        int cooldown = (int) sporeCloudAttributes.getValue(CruxAttribute.ATTACK_SPEED);
        particle.location(loc).spawn();
        new GetEntityNear<>(LivingEntity.class)
            .center(loc)
            .range(sporeCloudAttributes.getValue(CruxAttribute.ATTACK_RANGE))
            .filter(e ->{
                if(e.equals(entity)) return false;
                if(hitWithin(e, cooldown)) return false;
                if(uPlayer == null) return true;
                return !uPlayer.hasFriend(e.getUniqueId()) && !uPlayer.isApartOfParty(e.getUniqueId());
            })
            .find().forEach(hit ->{
                hit(hit);
                EntityDamager.entityDamager(hit, entity)
                    .attack(
                        sporeCloudAttributes.getValue(CruxAttribute.ATTACK_DAMAGE),
                        sporeCloudAttributes.getValue(CruxAttribute.ATTACK_KNOCKBACK),
                        sporeCloudAttributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP),
                        loc
                    );
                if(sporeCloudEffects != null){
                    hit.addPotionEffects(sporeCloudEffects);
                }
            });
    }

    public final Runnable tickSporeClouds;

    protected boolean tickingSpores = false;
    public void tickSporeClouds(){
        if(sporeClouds.isEmpty()) return;
        if(tickingSpores) return;
        tickingSpores = true;
        Crux.scheduler().runTask(tickSporeClouds);
    }

    @Override
    public void tick() {
        if(!(entity instanceof Player e)) return;

        tickSporeClouds();

        if(e.isSprinting()) sprintingTick(e);
        else notSprintingTick(e);
    }

    public boolean wasSprintingLastTick(){
        return sprintingTick > 0;
    }

    public void spawnSpore(Location loc){
        SporeCloud cloud = new SporeCloud(loc, System.currentTimeMillis());
        sporeClouds.add(cloud);
        if(sporeSpawnSound != null){
            Crux.scheduler().runTask(() -> sporeSpawnSound.playAt(loc));
        }
    }

    public boolean canSpawnSpore(){
        return sporeClouds.size() < maxSpores;
    }

    public boolean damageItem(int amount){
        if(amount < 1 || !(entity instanceof Player p)) return false;
        EquipmentSlot equip = AttributeBukkitAdaptor.adapt(slot);
        if(equip==null) return false;
        ItemStack item = p.getInventory().getItem(equip);
        if(CruxItem.isEmpty(item)) return false;
        Crux.scheduler().runTask(() -> p.damageItemStack(equip, amount));
        return true;
    }

    protected boolean spawnedSpore;
    protected boolean reachedMax;
    public void tickTrail(){
        if(sporeSpawnRate != 0 && sprintingTick % sporeSpawnRate != 0) return;
        if(!canSpawnSpore()){
            setCooldown(cooldown);
            damageItem(itemDmg);
            reachedMax = true;
            return;
        }
        if(reachedMax && isOnCooldown()) return;
        reachedMax = false;
        spawnedSpore = true;
        spawnSpore(entity.getLocation());
    }

    protected int sprintingTick = 0;
    protected int runUp = 0;
    public void sprintingTick(Player e){
        if(wasSprintingLastTick()){
            if(runUp < runUpTime){
                runUp++;
                return;
            }
            tickTrail();
        }else{
            if(isOnCooldown()) reachedMax = true;
        }
        sprintingTick++;
    }

    public void notSprintingTick(Player e){
        sprintingTick = 0;
        runUp = 0;
        if(spawnedSpore){
            spawnedSpore = false;
            setCooldown(cooldown);
            damageItem(itemDmg);
            reachedMax = false;
        }
    }

    public record SporeCloud(Location location, long time){ }
}

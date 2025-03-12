package killercreepr.cruxabyss.core.entity.tickable;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.usurvive.api.entity.player.UPlayer;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class ActivePlagueStalkerTickable extends ListenerActiveEntityTickable implements Listener {
    protected final double friendsRange;
    protected final int minTime;
    protected final int maxTime;
    protected final Collection<PotionEffect> timedEffects;
    protected final Collection<PotionEffect> friendsCombatEffects;
    protected final int minFriendAmount;
    protected final int combatCooldown;
    public ActivePlagueStalkerTickable(Entity entity, EntityTickable tickable, CruxSlot slot, double friendsRange, int minTime, int maxTime, Collection<PotionEffect> timedEffects, Collection<PotionEffect> friendsCombatEffects, int minFriendAmount, int combatCooldown) {
        super(entity, tickable, slot);
        this.friendsRange = friendsRange;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.timedEffects = timedEffects;
        this.friendsCombatEffects = friendsCombatEffects;
        this.minFriendAmount = minFriendAmount;
        this.combatCooldown = combatCooldown;
    }

    @Override
    public void stopped() {
        super.stopped();
        if(!(entity instanceof LivingEntity e)) return;
        Crux.scheduler().runTask(() ->{
            if(!CruxEntityUtil.isValid(e)) return;
            for (PotionEffectType type : added) {
                e.removePotionEffect(type);
            }
        });
    }

    protected final Collection<PotionEffectType> added = new HashSet<>();
    /*protected static final Collection<PotionEffect> EFFECTS = Set.of(
        new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0, false, false, true),
        new PotionEffect(PotionEffectType.SPEED, 200, 0, false, false, true)
    );*/

    protected long lastCombatEffects;
    protected int tick = 0;
    @Override
    public void tick() {
        tick++;
        if(tick % 20 == 0){
            tick=0;
            if(isNight()){
                if(entity instanceof LivingEntity l){
                    Crux.scheduler().runTask(() ->{
                        if(!CruxEntityUtil.isValid(l)) return;
                        for (PotionEffect effect : timedEffects) {
                            if(l.addPotionEffect(effect)){
                                added.add(effect.getType());
                            }
                        }
                    });
                }
            }
        }
    }

    public boolean isNight(){
        World world = entity.getWorld();
        return world.getTime() >= minTime && world.getTime() <= maxTime;
    }

    public boolean isFriendOrPet(Entity e){
        if(entity.equals(e)) return false;
        if(e instanceof Player){
            var uPlay = UPlayer.getPlayer(entity);
            return uPlay != null && (uPlay.hasFriend(e.getUniqueId()) || uPlay.isApartOfParty(e.getUniqueId()));
        }
        if(!(e instanceof Wolf t)) return false;
        AnimalTamer tamer = t.getOwner();
        if(tamer == null) return false;
        UUID tamerUUID = tamer.getUniqueId();
        if(tamerUUID.equals(entity.getUniqueId())) return true;
        var uPlay = UPlayer.getPlayer(entity);
        return uPlay != null && (uPlay.hasFriend(tamerUUID) || uPlay.isApartOfParty(tamerUUID));
    }

    public boolean hasFriendsNearby(){
        int amount = 0;
        for (Entity near : entity.getWorld().getNearbyEntities(entity.getLocation(), friendsRange, friendsRange, friendsRange)) {
            if(!isFriendOrPet(near)) continue;
            amount++;
            if(amount >= minFriendAmount) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity owner = EntityDamager.getOwner(event.getDamager());
        if(!entity.equals(owner) || !(entity instanceof LivingEntity l)) return;

        if(CruxMath.hasOccurredWithin(lastCombatEffects, combatCooldown)) return;
        if(isFriendOrPet(event.getEntity())) return;

        if(!hasFriendsNearby()) return;
        lastCombatEffects = System.currentTimeMillis();
        for (PotionEffect effect : friendsCombatEffects) {
            if(l.addPotionEffect(effect)){
                added.add(effect.getType());
            }
        }
    }

}

package killercreepr.cruxabyss.listener;

import killercreepr.crux.Crux;
import killercreepr.crux.data.entity.EntityMemory;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.cruxabyss.data.entity.AbyssHolder;
import killercreepr.cruxabyss.data.entity.AbyssSafezoneGuideHolder;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.event.EntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.event.SuccessfulEntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.menu.AbyssWarningMenu;
import killercreepr.cruxteleport.teleport.world.RandomWorldTP;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.logging.Level;

public class AbyssTravelTrackingListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTravelThroughRift(EntityTravelThroughRiftEvent event) {
        if(!(event.getEntity() instanceof Player p) || event.isCancelTeleport()) return;

        AbyssHolder holder = EntityMemory.getDataHolder(p, AbyssHolder.class);
        if(holder == null) return;
        if(holder.getAbyssTravelAmount() > 0) return;
        Mob mob = event.getRift().getMob();
        event.setCancelTeleport(true);
        Runnable task = () ->{
            RandomWorldTP to = event.getTo();
            to.randomlyTeleportAsync(p).whenComplete((spawn, throwable) ->{
                if(throwable != null) Crux.log(Level.WARNING, throwable.getMessage());
                if(spawn==null) return;
                Crux.scheduler().runTask(() ->{
                    Entity returnPortal = AbyssMob.RETURN_PORTAL.spawn(spawn, mob.getLocation());
                    SuccessfulEntityTravelThroughRiftEvent successEvent = new SuccessfulEntityTravelThroughRiftEvent(p, to, returnPortal);
                    successEvent.callEvent();
                });
            });
        };
        AbyssWarningMenu menu = new AbyssWarningMenu(task);
        menu.load();
        menu.open(p);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSuccessfulEntityTravelThroughRift(SuccessfulEntityTravelThroughRiftEvent event) {
        Entity e = event.getEntity();
        AbyssHolder holder = EntityMemory.getDataHolder(e, AbyssHolder.class);
        if(holder == null) return;
        int amount = holder.getAbyssTravelAmount();
        holder.setAbyssTravelAmount(amount+1);

        if(!(e instanceof Player p)) return;
        PlayerMemory mem = PlayerMemory.getOrCreate(p);
        AbyssSafezoneGuideHolder guide = mem.getDataHolder(AbyssSafezoneGuideHolder.class);
        if(guide == null){
            AbyssSafezoneGuideHolder data = new AbyssSafezoneGuideHolder(mem);
            data.update(p.getWorld(), p);
            mem.getDataHolders().register(data);
            return;
        }
        guide.update(p.getWorld(), p);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        AbyssSafezoneGuideHolder holder = EntityMemory.getDataHolder(p, AbyssSafezoneGuideHolder.class);
        if(holder == null) return;
        holder.onWorldChanged(p, event.getFrom());
    }


}

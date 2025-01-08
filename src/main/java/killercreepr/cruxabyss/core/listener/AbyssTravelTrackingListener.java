package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.cruxabyss.api.event.EntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.api.event.SuccessfulEntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.component.impl.TeleportAbyssWorldModule;
import killercreepr.cruxabyss.core.data.entity.AbyssHolder;
import killercreepr.cruxabyss.core.data.entity.AbyssSafezoneGuideHolder;
import killercreepr.cruxabyss.core.menu.AbyssWarningMenu;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class AbyssTravelTrackingListener implements Listener {
    protected final ValuesProvider cfg;

    public AbyssTravelTrackingListener(ValuesProvider cfg) {
        this.cfg = cfg;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTravelThroughRift(EntityTravelThroughRiftEvent event) {
        if(!(event.getEntity() instanceof Player p) || event.isCancelTeleport()) return;

        AbyssHolder holder = EntityMemory.getDataHolder(p, AbyssHolder.class);
        if(holder == null) return;
        if(holder.getAbyssTravelAmount() >= cfg.ABYSS_RIFT_SHOW_WARNING_IF_BELOW().value().intValue()) return;
        Mob mob = event.getRift().getMob();
        event.setCancelTeleport(true);
        Runnable task = () ->{
            CruxTeleport.Builder to = event.getTo();
            to.set(AbyssComponents.TELEPORT_ABYSS_WORLD, new TeleportAbyssWorldModule(p.getUniqueId(), mob));
            CruxTeleporter.teleporter().scheduleTeleport(p, to.build());
            /*to.randomlyTeleportAsync(p).whenComplete((spawn, throwable) ->{
                if(throwable != null) Crux.log(Level.WARNING, throwable.getMessage());
                if(spawn==null) return;
                Crux.scheduler().runTask(() ->{
                    Entity returnPortal = AbyssMob.RETURN_PORTAL.spawn(spawn, mob.getLocation());
                    SuccessfulEntityTravelThroughRiftEvent successEvent = new SuccessfulEntityTravelThroughRiftEvent(p, to, returnPortal);
                    successEvent.callEvent();
                });
            });*/
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
        if(amount >= cfg.ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW().value().intValue()) return;

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

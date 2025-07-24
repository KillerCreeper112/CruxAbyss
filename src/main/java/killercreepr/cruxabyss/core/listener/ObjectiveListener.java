package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.api.event.AbyssOutpostDeactivateEvent;
import killercreepr.cruxabyss.core.advancement.objective.AbyssOutpostCaptureObjective;
import killercreepr.cruxabyss.core.advancement.objective.AbyssOutpostDeactivateObjective;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ObjectiveListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssOutpostCapture(AbyssOutpostCaptureEvent event) {
        if(!(event.getEntity() instanceof Player p)) return;
        AdvancementHolder holder = AdvancementHolder.holderIfLoaded(p);
        if(holder==null) return;
        Crux.scheduler().runTask(() ->{
            if(!p.isOnline()) return;
            holder.getAdvancementTracker().apply(AbyssOutpostCaptureObjective.class, (manager,
                                                                                      advancement,
                                                                                      objective) -> {
                objective.trigger(p.getUniqueId(), manager, advancement, event);
            });
        });
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssOutpostDeactivate(AbyssOutpostDeactivateEvent event) {
        Player p = event.getPlayer();
        AdvancementHolder holder = AdvancementHolder.holderIfLoaded(p);
        if(holder==null) return;
        Crux.scheduler().runTask(() ->{
            if(!p.isOnline()) return;
            holder.getAdvancementTracker().apply(AbyssOutpostDeactivateObjective.class, (manager,
                                                                                      advancement,
                                                                                      objective) -> {
                objective.trigger(p.getUniqueId(), manager, advancement, event);
            });
        });
    }
}

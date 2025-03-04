package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.core.advancement.objective.AbyssOutpostCaptureObjective;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ObjectiveListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssOutpostCapture(AbyssOutpostCaptureEvent event) {
        Player p = event.getPlayer();
        AdvancementHolder holder = EntityMemory.getOrCreateDataHolder(p, AdvancementHolder.class);
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
}

package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.entity.memory.DepthsOfMadnessHolder;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CustomObjectivesListener implements Listener {
    protected final Holder<CruxAdvancementManager> MANAGER = () -> AdvancementRegistries.ADVANCEMENT_MANAGERS
        .get(USurvivePlugin.inst().key("abyss"));
    protected final Holder<CruxAdvancement> DEPTHS_OF_MADNESS = () -> MANAGER.value().getAdvancement(Crux.key("depths_of_madness"));

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedBlock()) return;
        Player p = event.getPlayer();
        if(DEPTHS_OF_MADNESS.value() == null) return;
        if(EntityMemory.getDataHolder(p, DepthsOfMadnessHolder.class) != null || DEPTHS_OF_MADNESS.value().isGranted(p)) return;
        EntityMemory.getOrCreateDataHolder(p, DepthsOfMadnessHolder.class, mem -> new DepthsOfMadnessHolder(mem, e ->{
            //todo fun little thing when complete
        }));
    }
}

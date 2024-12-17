package killercreepr.cruxabyss.core.listener;

import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class AbyssRegenerationListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().getUID());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED){
            event.setAmount(event.getAmount()*.5);
        }
    }

}

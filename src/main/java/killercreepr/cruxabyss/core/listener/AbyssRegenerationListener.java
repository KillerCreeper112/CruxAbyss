package killercreepr.cruxabyss.core.listener;

import killercreepr.usurvive.world.WorldUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class AbyssRegenerationListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity e = event.getEntity();
        if(!WorldUtil.getDimensionID(e.getWorld()).equalsIgnoreCase("abyss")) return;
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED){
            event.setAmount(event.getAmount()*.5);
        }
    }

}

package killercreepr.cruxabyss.core.listener;

import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AbyssalMobsListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if(!CruxMob.isInCategory(e, AbyssMobCategory.ABYSSAL)) return;
        switch (event.getCause()){
            case POISON  -> {
                event.setCancelled(true);
            }
            case FALL, CONTACT, FIRE, FIRE_TICK, CAMPFIRE, LAVA, HOT_FLOOR, FREEZE -> {
                event.setDamage(event.getDamage()/2D);
            }
            case SUFFOCATION, DROWNING, THORNS -> {
                event.setDamage(event.getDamage()*.7);
            }
        }
    }

}

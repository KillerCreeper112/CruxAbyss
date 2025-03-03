package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.cruxabyss.core.entity.memory.PlagueWingGliderHolder;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

//todo suffocation damage is weird in MEG https://git.lumine.io/mythiccraft/model-engine-4/-/issues/181
public class AbyssalListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();


        PlagueWingGliderHolder holder = EntityMemory.getDataHolder(e, PlagueWingGliderHolder.class);
        if(holder!=null){
            switch (event.getCause()){
                case POISON, HOT_FLOOR, FALL, DROWNING ->{
                    event.setCancelled(true);
                }
            }
            return;
        }

        if(!CruxMob.isInCategory(e, AbyssMobCategory.ABYSSAL)) return;
        switch (event.getCause()){
            case POISON, HOT_FLOOR, FIRE_TICK  -> {
                event.setCancelled(true);
            }
            case FALL, CONTACT, FIRE, CAMPFIRE, LAVA, FREEZE -> {
                event.setDamage(event.getDamage()/2);
            }
            case /*SUFFOCATION, */DROWNING, THORNS -> {
                event.setDamage(event.getDamage()*.7);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageHighest(EntityDamageEvent event) {
        Entity e = event.getEntity();
        PlagueWingGliderHolder holder = EntityMemory.getDataHolder(e, PlagueWingGliderHolder.class);
        if(holder==null) return;
        holder.damageItem((int)Math.round(event.getFinalDamage()), true);
    }

}

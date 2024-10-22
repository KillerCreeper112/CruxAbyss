package killercreepr.cruxabyss.listener;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import killercreepr.cruxabyss.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.structure.ActiveAbyssSafezone;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxstructures.manager.StructureManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;

public class AbyssSafezoneListener implements Listener {
    protected final Plugin plugin;
    protected final StructureManager manager;

    public AbyssSafezoneListener(Plugin plugin, StructureManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if(target == null) return;
        Entity e = event.getEntity();
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;

        ActiveAbyssSafezone targetStructure = manager.getFirstActiveAt(
            ActiveAbyssSafezone.class, target.getLocation().getBlock()
        );
        if(targetStructure != null) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityPathfind(EntityPathfindEvent event) {
        Entity e = event.getEntity();
        Location to = event.getLoc();
        Location current = e.getLocation();
        if(CruxMob.isInCategory(e, AbyssMobCategory.ABYSS_SAFEZONE)){
            ActiveAbyssSafezone currentStructure = manager.getFirstActiveAt(
                ActiveAbyssSafezone.class, current.getBlock()
            );
            if(currentStructure == null) return;
            ActiveAbyssSafezone toStructure = manager.getFirstActiveAt(
                ActiveAbyssSafezone.class, to.getBlock()
            );
            if(toStructure == null) event.setCancelled(true);
            return;
        }
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;
        ActiveAbyssSafezone currentStructure = manager.getFirstActiveAt(
            ActiveAbyssSafezone.class, current.getBlock()
        );
        if(currentStructure != null) return;
        ActiveAbyssSafezone toStructure = manager.getFirstActiveAt(
            ActiveAbyssSafezone.class, to.getBlock()
        );
        if(toStructure != null){
            event.setCancelled(true);
            if(e instanceof Mob m){
                Entity target = m.getTarget();
                if(target == null) return;
                ActiveAbyssSafezone targetStructure = manager.getFirstActiveAt(
                    ActiveAbyssSafezone.class, target.getLocation().getBlock()
                );
                if(targetStructure != null) m.setTarget(null);
            }
        }
    }

}

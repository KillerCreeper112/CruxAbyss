package killercreepr.cruxabyss.core.listener;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.structure.ActiveAbyssSafezone;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.world.WorldUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;

public class AbyssSafezoneListener implements Listener {
    protected final Plugin plugin;

    public AbyssSafezoneListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().getUID());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;
        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);

        ActiveAbyssSafezone targetStructure = module.getFirstActiveAt(
            ActiveAbyssSafezone.class, e.getLocation().getBlock()
        );
        if(targetStructure == null) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if(target == null) return;
        Entity e = event.getEntity();
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;

        CruxWorld crux = CruxCore.core().worldManager().getWorld(target.getWorld().getUID());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);

        ActiveAbyssSafezone targetStructure = module.getFirstActiveAt(
            ActiveAbyssSafezone.class, target.getLocation().getBlock()
        );
        if(targetStructure != null) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityPathfind(EntityPathfindEvent event) {
        Entity e = event.getEntity();
        Location to = event.getLoc();
        Location current = e.getLocation();

        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);

        if(CruxMob.isInCategory(e, AbyssMobCategory.ABYSS_SAFEZONE)){
            ActiveAbyssSafezone currentStructure = module.getFirstActiveAt(
                ActiveAbyssSafezone.class, current.getBlock()
            );
            if(currentStructure == null) return;
            ActiveAbyssSafezone toStructure = module.getFirstActiveAt(
                ActiveAbyssSafezone.class, to.getBlock()
            );
            if(toStructure == null) event.setCancelled(true);
            return;
        }
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;
        ActiveAbyssSafezone currentStructure = module.getFirstActiveAt(
            ActiveAbyssSafezone.class, current.getBlock()
        );
        if(currentStructure != null) return;
        ActiveAbyssSafezone toStructure = module.getFirstActiveAt(
            ActiveAbyssSafezone.class, to.getBlock()
        );
        if(toStructure != null){
            event.setCancelled(true);
            if(e instanceof Mob m){
                Entity target = m.getTarget();
                if(target == null) return;
                ActiveAbyssSafezone targetStructure = module.getFirstActiveAt(
                    ActiveAbyssSafezone.class, target.getLocation().getBlock()
                );
                if(targetStructure != null) m.setTarget(null);
            }
        }
    }

}

package killercreepr.cruxabyss.core.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Deprecated
public class AbyssSafezoneListener implements Listener {
    protected final Plugin plugin;

    public AbyssSafezoneListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity e = event.getEntity();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(e.getWorld().getUID());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;
        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);

        ActiveAbyssSafeZone targetStructure = module.getFirstActiveAt(
            ActiveAbyssSafeZone.class, e.getLocation().getBlock()
        );
        if(targetStructure == null) return;
        event.setCancelled(true);
    }*/


    /*@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if(target == null) return;
        Entity e = event.getEntity();
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;

        CruxWorld crux = CruxCore.core().worldManager().getWorld(target.getWorld().getUID());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);

        ActiveAbyssSafeZone targetStructure = module.getFirstActiveAt(
            ActiveAbyssSafeZone.class, target.getLocation().getBlock()
        );
        if(targetStructure != null) event.setCancelled(true);
    }*/

    /*@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityPathfind(EntityPathfindEvent event) {
        Entity e = event.getEntity();
        Location to = event.getLoc();
        Location current = e.getLocation();

        CruxWorld crux = CruxCore.core().worldManager().getWorld(e.getWorld().getUID());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);

        if(CruxMob.isInCategory(e, AbyssMobCategory.ABYSS_SAFEZONE)){
            ActiveAbyssSafeZone currentStructure = module.getFirstActiveAt(
                ActiveAbyssSafeZone.class, current.getBlock()
            );
            if(currentStructure == null) return;
            ActiveAbyssSafeZone toStructure = module.getFirstActiveAt(
                ActiveAbyssSafeZone.class, to.getBlock()
            );
            if(toStructure == null) event.setCancelled(true);
            return;
        }
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;
        ActiveAbyssSafeZone currentStructure = module.getFirstActiveAt(
            ActiveAbyssSafeZone.class, current.getBlock()
        );
        if(currentStructure != null) return;
        ActiveAbyssSafeZone toStructure = module.getFirstActiveAt(
            ActiveAbyssSafeZone.class, to.getBlock()
        );
        if(toStructure != null){
            event.setCancelled(true);
            if(e instanceof Mob m){
                Entity target = m.getTarget();
                if(target == null) return;
                ActiveAbyssSafeZone targetStructure = module.getFirstActiveAt(
                    ActiveAbyssSafeZone.class, target.getLocation().getBlock()
                );
                if(targetStructure != null) m.setTarget(null);
            }
        }
    }*/

}

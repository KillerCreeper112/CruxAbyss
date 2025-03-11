package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxabyss.core.entity.mob.goal.HostileTargetGoal;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

public class HostileMobListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        event.getEntities().forEach(this::load);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
    }

    public void load(Entity e){
        if(!(e instanceof Mob mob)) return;
        if(!(e instanceof Enemy)) return;
        CruxGoalUtil.addIfNotPresent(
            mob, HostileTargetGoal.class, 3, () -> new HostileTargetGoal(mob)
        );
    }
}

package killercreepr.cruxabyss.core.entity.goal;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarPlacedItemGoal extends CruxMobModeledGoal implements Listener {
    public AbyssAltarPlacedItemGoal(@NotNull Mob mob) {
        super(mob);
    }

    public AbyssAltarPlacedItemGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    public void onLeftClick(Player p){

    }

    public void onRightClick(Player p){

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!event.getRightClicked().equals(mob)) return;
        onRightClick(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(!(event.getEntity() instanceof Player p)) return;
        onLeftClick(p);
    }

}

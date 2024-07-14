package killercreepr.cruxabyss.entity.goal;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.persistence.CruxPersistence;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AbyssReturnPortalGoal extends CruxMobModeledGoal {
    public AbyssReturnPortalGoal(@NotNull Mob mob, ActiveModel model) {
        this(CruxMobGoal.defaultKey(),mob, model);
    }
    public AbyssReturnPortalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, ActiveModel model) {
        super(key, mob, model);
        returnTo = CruxTag.get(mob, "return_to", CruxPersistence.LOCATION, null);
        Objects.requireNonNull(returnTo, "Return portal does not have a return location!");
    }

    protected @NotNull Location returnTo;

    @NotNull
    public Location getReturnTo() {
        return returnTo;
    }

    public void setReturnTo(@NotNull Location returnTo) {
        this.returnTo = returnTo;
    }

    protected int lifeSpan = 200;
    protected int cooldown = 60/2;

    @Override
    public void tick() {
        if(cooldown > 0){
            cooldown--;
            return;
        }
        lifeSpan--;
        if(lifeSpan < 0){
            mob.remove();
            Bukkit.broadcastMessage("UR PROBLEM NOW. BYE");
            return;
        }
        mob.getWorld().getNearbyEntities(mob.getBoundingBox(), e -> e instanceof Player).forEach(e ->{
            mob.remove();
            Player p = (Player) e;
            p.sendMessage("get scared kid");
            p.teleport(returnTo);
        });
    }
}

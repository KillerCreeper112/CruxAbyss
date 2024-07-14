package killercreepr.cruxabyss.entity.goal;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.location.EntityLocation;
import killercreepr.crux.util.GetEntityNear;
import killercreepr.crux.util.GetNear;
import killercreepr.cruxabyss.CruxAbyss;
import killercreepr.cruxabyss.teleport.RandomTP;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarPortalGoal extends CruxMobModeledGoal {
    public AbyssAltarPortalGoal(@NotNull Mob mob, ActiveModel model) {
        this(CruxMobGoal.defaultKey(),mob, model);
    }

    public AbyssAltarPortalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, ActiveModel model) {
        super(key, mob, model);
        getNear = new GetEntityNear<>(EntityLocation.from(mob), Player.class)
            .amount(1)
            .range(2D);
    }

    protected final GetNear<Player> getNear;
    @Override
    public void tick() {
        mob.getWorld().getNearbyEntities(mob.getBoundingBox(), e -> e instanceof Player).forEach(e ->{
            Player p = (Player) e;
            p.sendMessage("seeya loser");
            new RandomTP(CruxAbyss.inst().game.getWorld()).randomTeleport(p);
        });
    }
}

package killercreepr.cruxabyss.entity.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.location.DynamicLocation;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.GetEntityNear;
import killercreepr.crux.util.GetNear;
import killercreepr.cruxabyss.CruxAbyss;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.teleport.RandomTP;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarPortalGoal extends CruxMobModeledGoal {
    public AbyssAltarPortalGoal(@NotNull Mob mob, ActiveModel model) {
        this(CruxMobGoal.defaultKey(),mob, model);
    }

    public AbyssAltarPortalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, ActiveModel model) {
        super(key, mob, model);
    }

    protected int particle;
    public void particleTick(){
        if(particle > 0){
            particle--;
            return;
        }
        particle = CruxMath.random(5, 20);
        new ParticleBuilder(Particle.REVERSE_PORTAL)
            .count(10)
            .offset(1, 1, 1)
            .location(mob.getLocation())
            .extra(.3)
            .spawn()
        ;
    }

    protected final GetNear<Player> getNearestPlayer = new GetEntityNear<>(DynamicLocation.from(mob), Player.class)
        .range(4D)
        .amount(1)
        .operation(GetNear.Operation.NEAREST)
        ;
    public void rotate(){
        Player at = getNearestPlayer.findFirst();
        if(at==null) return;

        Location loc = CruxLoc.lookAt(mob.getEyeLocation(), at.getEyeLocation());
        mob.lookAt(at);
        mob.setBodyYaw(loc.getYaw());
    }

    @Override
    public void tick() {
        rotate();
        particleTick();
        mob.getWorld().getNearbyEntities(mob.getBoundingBox(), e -> e instanceof Player).forEach(e ->{
            Player p = (Player) e;
            p.sendMessage("seeya loser");
            Location spawn = new RandomTP(CruxAbyss.inst().game.toBukkitWorld()).randomTeleport(p);
            if(spawn==null) return;
            AbyssMob.RETURN_PORTAL.spawn(spawn, mob.getLocation());
            mob.damage(99999999D);
        });
    }
}

package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class HostileTargetGoal extends CruxMobGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("hostile_target"));
    public HostileTargetGoal(@NotNull Mob mob) {
        this(KEY, mob);
    }

    public HostileTargetGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    @Override
    public void entityDamageMob(EntityDamageByEntityEvent event) {

    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return CruxMob.isInCategory(target, AbyssMobCategory.ABYSS_SAFEZONE) && super.isValidNaturalTarget(target);
    }

    @Override
    public void tick() {
        if(mob.getTarget() != null) return;
        super.tick();
    }

    @Override
    public double getForgetTargetDistance() {
        return getFollowDistance();
    }
}

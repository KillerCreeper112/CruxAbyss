package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class PlagueStalkerGoal extends CruxMobModeledGoal {
    public PlagueStalkerGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_WOLF_AMBIENT, .6f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_WOLF_GROWL, .6f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_WOLF_HURT, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_WOLF_DEATH, .6f);
            }
        });
    }
}

package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class VilderGoal extends CruxMobModeledGoal implements Listener {
    public VilderGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_VILLAGER_AMBIENT, .6f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_VILLAGER_HURT, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_VILLAGER_DEATH, .6f);
            }
        });
    }
}

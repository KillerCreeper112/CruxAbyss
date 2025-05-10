package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class CharredBonesGoal extends CruxMobModeledGoal {
    public CharredBonesGoal(@NotNull Mob mob) {
        super(mob);
        sounds(
            new CruxGoalSounds(mob) {
                @Override
                public @NotNull CreateSound ambient() {
                    return CreateSound.sound(Sound.ENTITY_SKELETON_AMBIENT, .9f);
                }

                /*@Override
                public @NotNull CreateSound attack() {
                    return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
                }*/
                @Override
                public @NotNull CreateSound hurt() {
                    return CreateSound.sound(Sound.ENTITY_SKELETON_HURT, .9f);
                }

                @Override
                public @NotNull CreateSound death() {
                    return CreateSound.sound(Sound.ENTITY_SKELETON_DEATH, .9f);
                }
            }
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void entityDamage(CruxEntityDamageEvent event){
        if(mob.equals(event.getDamager())){
            playAnimation("attack", true);
            return;
        }
        if(mob.equals(event.getEntity()) && event.getDamager() != null){
            playAnimation("hit_by", true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        switch (event.getCause()){
            case FIRE, FIRE_TICK, LAVA, HOT_FLOOR, CAMPFIRE -> event.setCancelled(true);
        }
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}

package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CapgrasGoal extends CruxMobModeledGoal {
    public CapgrasGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            private final List<Sound> AMBIENT = List.of(
                    Sound.ENTITY_GOAT_AMBIENT,
                    Sound.ENTITY_CHICKEN_AMBIENT,
                    Sound.ENTITY_COW_AMBIENT,
                    Sound.ENTITY_STRAY_AMBIENT,
                    Sound.ENTITY_EVOKER_AMBIENT,
                    Sound.ENTITY_VILLAGER_AMBIENT,
                    Sound.ENTITY_HUSK_AMBIENT,
                    Sound.ENTITY_SHEEP_AMBIENT,
                    Sound.ENTITY_AXOLOTL_IDLE_AIR
            );
            private final List<Sound> ATTACK = List.of(
                    Sound.ENTITY_GOAT_RAM_IMPACT,
                    Sound.ENTITY_SLIME_ATTACK,
                    Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,
                    Sound.ENTITY_RABBIT_ATTACK,
                    Sound.ENTITY_PLAYER_ATTACK_STRONG,
                    Sound.ENTITY_WARDEN_ATTACK_IMPACT,
                    Sound.ENTITY_DOLPHIN_ATTACK,
                    Sound.ENTITY_AXOLOTL_ATTACK
            );
            private final List<Sound> HURT = List.of(
                    Sound.ENTITY_GOAT_HURT,
                    Sound.ENTITY_CHICKEN_HURT,
                    Sound.ENTITY_COW_HURT,
                    Sound.ENTITY_STRAY_HURT,
                    Sound.ENTITY_EVOKER_HURT,
                    Sound.ENTITY_VILLAGER_HURT,
                    Sound.ENTITY_HUSK_HURT,
                    Sound.ENTITY_SHEEP_HURT,
                    Sound.ENTITY_AXOLOTL_HURT
            );
            private final List<Sound> DEATH = List.of(
                    Sound.ENTITY_GOAT_DEATH,
                    Sound.ENTITY_CHICKEN_DEATH,
                    Sound.ENTITY_COW_DEATH,
                    Sound.ENTITY_STRAY_DEATH,
                    Sound.ENTITY_EVOKER_DEATH,
                    Sound.ENTITY_VILLAGER_DEATH,
                    Sound.ENTITY_HUSK_DEATH,
                    Sound.ENTITY_SHEEP_DEATH,
                    Sound.ENTITY_AXOLOTL_DEATH
            );
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(CruxCollection.getRandom(AMBIENT),  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.15f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(CruxCollection.getRandom(ATTACK),  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.15f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(CruxCollection.getRandom(HURT),  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.15f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(CruxCollection.getRandom(DEATH),  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.15f);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void entityDamage(CruxEntityDamageEvent event){
        if(mob.equals(event.getDamager())){
            playAnimation("attack_" + CruxMath.random(1,6), true);
            return;
        }
        if(mob.equals(event.getEntity()) && event.getDamager() != null){
            playAnimation("hit_by", true);
        }
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}

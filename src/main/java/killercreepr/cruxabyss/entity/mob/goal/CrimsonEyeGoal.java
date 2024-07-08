package killercreepr.cruxabyss.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.event.CruxEntityDamageEvent;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxTag;
import killercreepr.crux.util.GetEntityNear;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class CrimsonEyeGoal extends CruxMobModeledGoal {
    private int strongAttack;
    private int strongAttackCooldown;
    public CrimsonEyeGoal(@NotNull Mob mob, @NotNull ActiveModel model) {
        super(mob, model);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return new CreateSound(Sound.ENTITY_SLIME_SQUISH, 1.5f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return new CreateSound(Sound.ENTITY_SLIME_ATTACK, 1.5f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return new CreateSound(Sound.ENTITY_SLIME_ATTACK, 1.5f);
            }

            @Override
            public @NotNull CreateSound death() {
                return new CreateSound(Sound.ENTITY_SLIME_DEATH, 1.5f);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void entityDamage(CruxEntityDamageEvent event){
        if(mob.equals(event.getDamager()) && strongAttack < 1){
            playAnimation("attack", true);
            return;
        }
        if(mob.equals(event.getEntity()) && event.getDamager() != null){
            playAnimation("hit_by", true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(mob.equals(event.getDamager()) && strongAttack < 1){
            playAnimation("attack", true);
            return;
        }
        if(mob.equals(event.getEntity())) {
            playAnimation("hit_by", true);
        }
    }

    @Override
    public void tick() {
        if(CruxTag.has(mob, "hide")){
            if(!isPlayingAnimation("hide")) playAnimation("hide", true);
            if(!new GetEntityNear<>(LivingEntity.class)
                .center(mob)
                .range(3D)
                .find().isEmpty()){
                CruxTag.remove(mob, "hide");
                playAnimation("expose", true);
                stopAnimation("hide");
            }
            return;
        }
        if(isPlayingAnimation("expose")){
            strongAttackCooldown = 15;
            attackCooldown = 10;
            mob.getWorld().playSound(mob.getLocation(), Sound.BLOCK_HONEY_BLOCK_PLACE, 1f, 1.8f);
            Block g = mob.getLocation().subtract(0, .3, 0).getBlock();
            if(!g.isEmpty()){
                new ParticleBuilder(Particle.BLOCK)
                    .offset(.5, .5, .5)
                    .extra(.1)
                    .color(10)
                    .location(mob.getLocation())
                    .spawn()
                ;
            }
        }
        super.tick();
        if(target == null) return;
        if(strongAttack > 0){
            strongAttack--;
            //animation hit time at 7 ticks
            if(strongAttack == 7){
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        new CruxAttributeModifier(Crux.key("strong_damage"), 1D, CruxAttribute.Operation.MULTIPLY),
                        Crux.key("strong_attack"));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        new CruxAttributeModifier(Crux.key("strong_knockback"), 1D, CruxAttribute.Operation.MULTIPLY),
                    Crux.key("strong_attack"));
                CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        new CruxAttributeModifier(Crux.key("strong_range"), 1D, CruxAttribute.Operation.MULTIPLY),
                    Crux.key("strong_attack"));
                hit(true, target);
                CruxAttribute.removeModifiers(mob, Crux.key("strong_attack"));
            }
            return;
        }
        if(strongAttackCooldown > 0){
            strongAttackCooldown--;
            return;
        }
        double range = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE);
        if(range <= 0D) return;
        double distance = getDistance();
        if(distance <= (range*1.5)){//7
            strongAttack = 11; //animation time
            playAnimation("strong_attack", true);
            strongAttackCooldown = CruxMath.random(95, 130);
        }
    }
}

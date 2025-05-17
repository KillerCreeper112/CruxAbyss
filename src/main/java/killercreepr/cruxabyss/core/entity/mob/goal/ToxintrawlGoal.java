package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ToxintrawlGoal extends CruxMobModeledGoal implements Listener {
    public ToxintrawlGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_STRIDER_AMBIENT, 0.4f, .75f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_STRIDER_STEP,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_STRIDER_HURT,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, .75f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_STRIDER_DEATH,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, .6f);
            }
        });
    }

    @Override
    public void moveTo() {
        this.moveTo(10F);
    }

    @Override
    public boolean shouldConstantlyLookAtTarget() {
        return true;
    }

    protected int tonguePullCooldown;
    @Override
    public void tick() {
        if(isElongatingTongue()){
            if(target == null){
                setTonguePull(false);
                return;
            }
            mob.lookAt(target);
            elongateTongueTick();
            return;
        }else{
            stopAnimation("open_mouth");
        }

        super.tick();
        if(target == null) return;

        if(tonguePullCooldown > 0){
            tonguePullCooldown--;
            return;
        }
        double distance = getSquaredDistanceFromTarget();
        if(!isDistanceForTonguePull(distance)) return;
        tonguePullCooldown = CruxMath.random(100, 200);
        setTonguePull(true);
        CreateSound.sound(Sound.ENTITY_GENERIC_SPLASH, 1.8f).playAt(mob);
        CreateSound.sound(Sound.ENTITY_FOX_BITE, 2).playAt(mob);
    }

    public boolean isDistanceForTonguePull(double squaredDistance){
        return squaredDistance >= 2.5D*2.5D && squaredDistance <= 10D*10D;
    }

    public void setTonguePull(boolean on){
        if(on){
            playAnimation("tongue_pull", true);
            playAnimation("open_mouth", true);
            return;
        }
        stopAnimation("tongue_pull");
        stopAnimation("open_mouth");
    }

    public void elongateTongueTick(){
        Location end = getTongueEndPosition();

        if(end.getBlock().isSolid()){
            setTonguePull(false);
            return;
        }

        end.getWorld().getNearbyEntitiesByType(LivingEntity.class, end, 1.2, this::isValidNaturalTarget).forEach(victim ->{
            Vector v = mob.getLocation().toVector().subtract(victim.getLocation().toVector())
                .normalize().multiply(.1);
            victim.setVelocity(victim.getVelocity().add(v));
        });
    }

    public boolean isElongatingTongue(){
        return isPlayingAnimation("tongue_pull");
    }

    public Location getTongueEndPosition(){
        return getModel().getBone("tongue_end").get().getLocation();
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(mob.equals(event.getEntity())){
            playAnimation("hurt", false);
        }
        if(!event.getDamager().equals(mob)) return;
        String attackID = "attack_" + CruxMath.random(1, 2);
        playAnimation(attackID, true);
    }
}

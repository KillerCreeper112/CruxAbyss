package killercreepr.cruxabyss.entity.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.location.DynamicLocation;
import killercreepr.crux.persistence.CruxPersistence;
import killercreepr.crux.util.*;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AbyssReturnPortalGoal extends CruxMobModeledGoal {
    public AbyssReturnPortalGoal(@NotNull Mob mob) {
        this(CruxMobGoal.defaultKey(),mob);
    }
    public AbyssReturnPortalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
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

    protected boolean hasLeftPortal = false;
    protected int completeMaxTime = 600;

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
        if(!hasLeftPortal){
            if(mob.getWorld().getNearbyEntities(mob.getBoundingBox(), e -> e instanceof Player).isEmpty()){
                hasLeftPortal = true;
            }else{
                if(completeMaxTime >= 0){
                    completeMaxTime--;
                    if(completeMaxTime < 1){
                        mob.damage(999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
                    }
                }
                return;
            }
        }

        particleTick();
        if(cooldown > 0){
            cooldown--;
            return;
        }
        if(lifeSpan >= 0){
            lifeSpan--;
            if(lifeSpan < 0){
                mob.damage(999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
                return;
            }
        }
        int index = 0;
        for (Entity e : mob.getWorld().getNearbyEntities(mob.getBoundingBox(), e -> e instanceof Player)) {
            index++;
            if(index == 1){
                mob.damage(999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
                CreateSound.sound(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.2f).playAt(mob.getLocation());
            }

            Player p = (Player) e;
            p.teleport(returnTo);
        }
    }

    public int getCompleteMaxTime() {
        return completeMaxTime;
    }

    public void setCompleteMaxTime(int completeMaxTime) {
        this.completeMaxTime = completeMaxTime;
    }

    public boolean isHasLeftPortal() {
        return hasLeftPortal;
    }

    public void setHasLeftPortal(boolean hasLeftPortal) {
        this.hasLeftPortal = hasLeftPortal;
    }

    public int getParticle() {
        return particle;
    }

    public void setParticle(int particle) {
        this.particle = particle;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }
}

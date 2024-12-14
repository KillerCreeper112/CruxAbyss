package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaguewingGoal extends CruxMobModeledGoal implements Listener {
    public PlaguewingGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_SPIDER_AMBIENT, .6f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_SPIDER_AMBIENT, .9f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_SPIDER_HURT, .7f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_SPIDER_DEATH, .6f);
            }
        });
    }

    protected Location locationTarget;
    protected int spitAttackTime = 0;
    protected int spitAttackCooldown = 0;
    protected int hitsTillBackup = CruxMath.random(1, 4);

    public void spit(Location loc){
        Vector v = CruxMath.parabolicMotion(
            mob.getEyeLocation().toVector(), loc.toVector(), CruxMath.random(2, 5), CruxMath.random(.15, .25)
        );
        mob.getWorld().spawn(mob.getEyeLocation(), Snowball.class, e ->{
            CruxTag.set(e, "plaguewing_spit", PersistentDataType.INTEGER, 1);
            CruxTag.set(e, "ignore_abyssal_mobs", PersistentDataType.INTEGER, 1);
            e.setItem(new ItemStack(Material.SLIME_BLOCK));
            e.setVelocity(v);
        });
        Vector dir = loc.toVector().subtract(mob.getEyeLocation().toVector()).normalize();
        int amount = CruxMath.random(8, 15);
        while(amount > 0){
            amount--;
            new ParticleBuilder(Particle.ITEM)
                .location(mob.getEyeLocation())
                .offset(dir.getX(), dir.getY(), dir.getZ())
                .count(0)
                .extra(.1)
                .data(new ItemStack(Material.SLIME_BALL))
                .spawn()
            ;
        }
        CreateSound.sound(Sound.ENTITY_LLAMA_SPIT, 1.5f).playAt(mob);
    }

    @Override
    public void tick() {
        if(spitAttackCooldown > 0){
            spitAttackCooldown--;
        }
        if(locationTarget != null){
            checkTargetLogic();
            if(target == null){
                locationTarget = null;
                return;
            }
            moveTo(locationTarget, speed);
            if(target != null){
                mob.lookAt(target);
                if(spitAttackTime > 0){
                    spitAttackTime--;
                    if(spitAttackTime < 1){
                        spit(target.getLocation());
                        spitAttackCooldown = CruxMath.random(100, 200);
                    }
                    return;
                }
            }

            if(!CruxMath.hasOccurredWithin(lastHitTarget, 20)){
                locationTarget = null;
            }
            return;
        }
        super.tick();
    }

    protected long lastHitTarget;
    @Override
    protected void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(event.isCancelled()) return;
        if((event.getEntity() instanceof LivingEntity victim)){
            if(CruxMath.testChance(50)){
                victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
            }
        }
        lastHitTarget = System.currentTimeMillis();

        hitsTillBackup--;
        if(hitsTillBackup > 0) return;

        Location l = CruxLoc.shift(mob.getEyeLocation(), CruxMath.random(-7, -4), 0D, 0D);
        if(l.getBlock().isSolid()){
            return;
        }
        locationTarget = l;
        if(spitAttackCooldown < 1){
            spitAttackTime = CruxMath.random(20, 40);
        }
        hitsTillBackup = CruxMath.random(1, 3);
    }

    protected final double speed = CruxMath.random(1.5D, 2D);
    @Override
    public void moveTo() {
        this.moveTo(speed);
    }

    @Override
    public void moveTo(@Nullable LivingEntity target, double speed) {
        if(target == null) super.moveTo(target, speed);
        else{
            moveTo(target.getEyeLocation(), speed);
        }
    }

    @Override
    public boolean shouldConstantlyLookAtTarget() {
        return true;
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return super.isValidNaturalTarget(target) && !CruxMob.isInCategory(target, MobCategory.ENEMY);
    }
}

package killercreepr.cruxabyss.core.component.impl;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxitems.api.item.component.ConsumableComponent;
import killercreepr.cruxitems.api.item.consume.ItemConsumeContext;
import killercreepr.cruxitems.api.item.consume.ItemConsumeResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaglashComponent implements ConsumableComponent {
    protected final int cooldown;
    protected final CruxAttributeContainer attributes;
    protected final int time;
    protected final int pushTime;
    protected final double speedDropOff;

    public CreateSound getUseSound() {
        return useSound;
    }

    protected final CreateSound useSound;
    protected final ParticleBuilderSupplier particleSupplier;

    public StaglashComponent(int cooldown, CruxAttributeContainer attributes, int time, int pushTime, double speedDropOff, CreateSound useSound, ParticleBuilderSupplier particleSupplier) {
        this.cooldown = cooldown;
        this.attributes = attributes;
        this.time = time;
        this.pushTime = pushTime;
        this.speedDropOff = speedDropOff;
        this.useSound = useSound;
        this.particleSupplier = particleSupplier;
    }

    public int getPushTime() {
        return pushTime;
    }

    public int getTime() {
        return time;
    }

    public ParticleBuilderSupplier getParticleSupplier() {
        return particleSupplier;
    }

    public CruxAttributeContainer getAttributes() {
        return attributes;
    }

    public int getCooldown() {
        return cooldown;
    }

    @Override
    public @NotNull ItemConsumeResult onConsume(@NotNull ItemConsumeContext ctx) {
        Player e = ctx.getPlayer();
        CruxItem item = ctx.getItem();
        if(e.hasCooldown(item.item())) return ItemConsumeResult.cancelled();

        if(e.getGameMode() != GameMode.CREATIVE){
            Crux.handlers().item().damageItem(item.item(), 1, e);
            e.getInventory().setItem(ctx.getSlot(), item.item());
        }

        e.setCooldown(item.item(), cooldown);

        new Ability(
            e, attributes.getValue(CruxAttribute.ATTACK_DAMAGE),
            attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK),
            attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP),
            attributes.getValue(CruxAttribute.ATTACK_RANGE),
            attributes.getValue(CruxAttribute.MOVEMENT_SPEED),
            speedDropOff,
            time, pushTime,
            particleSupplier == null ? null : particleSupplier.build()
        ).start();

        if(useSound != null){
            useSound.playAt(e);
        }

        return ItemConsumeResult.cancelled();
    }

    @Override
    public boolean isConsumable(@NotNull ItemConsumeContext ctx) {
        return true;
    }

    public static class Ability{
        protected final Entity user;
        protected final double damage;
        protected final double kb;
        protected final double upKb;
        protected final double range;
        protected final double speed;
        protected final double speedDropOff;
        protected final int time;
        protected final int pushTime;
        protected final ParticleBuilder particle;

        protected int currentTime;
        protected double currentSpeed;

        public Ability(Entity user, double damage, double kb, double upKb, double range, double speed, double speedDropOff, int time, int pushTime, ParticleBuilder particle) {
            this.user = user;
            this.damage = damage;
            this.kb = kb;
            this.upKb = upKb;
            this.range = range;
            this.speed = speed;
            this.speedDropOff = speedDropOff;
            this.time = time;
            this.pushTime = pushTime;
            this.particle = particle;
            this.currentSpeed = speed;
        }

        protected final Map<UUID, Long> hit = new HashMap<>();

        public boolean wasHitWithin(Entity e, int ticks){
            Long time = hit.get(e.getUniqueId());
            if(time == null) return false;
            return CruxMath.hasOccurredWithin(time, ticks);
        }

        public void hit(Entity e){
            hit.put(e.getUniqueId(), System.currentTimeMillis());
        }

        public void start(){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(!CruxEntityUtil.isValid(user)){
                        cancel();
                        return;
                    }
                    currentTime++;
                    if(currentTime >= time){
                        cancel();
                        return;
                    }
                    tick();
                }
            }.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
        }

        public void tick(){
            Location mid = user.getLocation().add(0, user.getHeight()/2, 0);

            if(currentTime <= pushTime){
                Location l = user.getLocation();
                l.setPitch(CruxMath.clamp(l.getPitch(), -10, 10));
                Vector dir = l.getDirection().multiply(currentSpeed);
                currentSpeed *= speedDropOff;
                user.setVelocity(user.getVelocity().add(dir));
            }

            user.getWorld().getNearbyEntities(
                user.getBoundingBox().expand(range), e -> !e.equals(user)
            ).forEach(hit ->{
                if(wasHitWithin(hit, 5)) return;
                hit(hit);
                EntityDamager.entityDamager(hit, user)
                    .setSource(
                        DamageSource.builder(DamageType.MAGIC)
                            .withDirectEntity(user)
                            .build()
                    )
                    .attack(damage, kb, upKb);
                new ParticleBuilder(Particle.WITCH)
                    .location(hit.getLocation().add(0, hit.getHeight()/2, 0))
                    .count(CruxMath.random(6, 9))
                    .extra(.2)
                    .offset(.5, .5, .5)
                    .spawn()
                ;
            });

            if(particle == null) return;
            particle.location(mid).spawn();
        }

        public Entity getUser() {
            return user;
        }

        public double getDamage() {
            return damage;
        }

        public double getKb() {
            return kb;
        }

        public double getUpKb() {
            return upKb;
        }

        public double getRange() {
            return range;
        }

        public double getSpeed() {
            return speed;
        }

        public int getTime() {
            return time;
        }
    }
}

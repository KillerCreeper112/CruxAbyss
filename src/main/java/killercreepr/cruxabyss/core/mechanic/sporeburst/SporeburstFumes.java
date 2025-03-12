package killercreepr.cruxabyss.core.mechanic.sporeburst;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeAccessor;
import killercreepr.cruxentities.api.combat.EntityDamager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SporeburstFumes {
    protected final Location loc;
    protected final CruxAttributeAccessor attributes;
    protected final int lifeSpan;
    protected final ParticleBuilder particles;
    protected final GetNear<LivingEntity> getNear;
    protected final Entity owner;
    protected final Collection<PotionEffect> effects;

    public SporeburstFumes(Location loc, CruxAttributeAccessor attributes, int lifeSpan, ParticleBuilder particles, GetNear<LivingEntity> getNear, Entity owner, Collection<PotionEffect> effects) {
        this.loc = loc;
        this.getNear = getNear;
        this.attributes = attributes;
        this.lifeSpan = lifeSpan;
        this.particles = particles;
        this.owner = owner;
        this.effects = effects;
    }

    protected int tick = 0;
    public void start(){
        CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, .8f).playAt(getNear.center().value());
        new ParticleBuilder(Particle.ITEM)
            .count(CruxMath.random(15, 20))
            .extra(.3)
            .offset(.6, .6, .6)
            .data(new ItemStack(Material.SLIME_BALL))
            .location(getNear.center().value())
            .spawn()
        ;
        new BukkitRunnable(){
            @Override
            public void run() {
                tick++;

                tick();

                if(tick >= lifeSpan){
                    cancel();
                }
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
    }

    protected final Map<UUID, Long> hit = new HashMap<>();
    public void hit(Entity e){
        hit.put(e.getUniqueId(), System.currentTimeMillis());
    }

    public boolean wasHitWithin(Entity e){
        Long time = hit.get(e.getUniqueId());
        if(time == null) return false;
        int attackSpeed = (int) attributes.getValue(CruxAttribute.ATTACK_SPEED);
        return CruxMath.hasOccurredWithin(time, attackSpeed);
    }
    public void tick(){
        if(tick % 5 != 0) return;
        particles.spawn();
        for (LivingEntity hit : getNear.find()) {
            if(wasHitWithin(hit)) return;
            hit(hit);

            EntityDamager.entityDamager(hit, owner)
                .setHitPosition(getNear.center().value())
                .attack(
                    attributes.getValue(CruxAttribute.ATTACK_DAMAGE),
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK),
                    attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP)
                );
            hit.addPotionEffects(effects);
        }
    }
}

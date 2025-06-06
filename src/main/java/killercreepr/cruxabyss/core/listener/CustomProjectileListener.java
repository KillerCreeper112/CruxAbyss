package killercreepr.cruxabyss.core.listener;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.component.impl.SporeburstChargeComponent;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.mechanic.sporeburst.SporeburstFumes;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeAccessor;
import killercreepr.cruxattributes.api.attribute.CruxAttributeHandler;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.usurvive.api.event.ProjectileRicochetEvent;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class CustomProjectileListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        sporeBurst(event);
        if(CruxTag.has(e, "rotfiend_ooze")) performRotfiendOoze(event);
        if(event.getHitEntity() != null){
            if(CruxTag.has(e, "ignore_abyssal_mobs")){
                if(CruxMob.isInCategory(event.getHitEntity(), AbyssMobCategory.ABYSSAL)){
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if(!CruxTag.has(e, "plaguewing_spit")) return;
        e.getWorld().spawn(e.getLocation(), AreaEffectCloud.class, x ->{
            x.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 80, 1), false);
            x.setDuration(80);
        });
        new ParticleBuilder(Particle.ITEM)
            .count(CruxMath.random(15, 30))
            .extra(.3)
            .offset(1, 1, 1)
            .data(new ItemStack(Material.SLIME_BALL))
            .location(e.getLocation())
            .spawn()
        ;
        CreateSound.sound(Sound.ENTITY_PLAYER_SPLASH, 1.5f).playAt(e);
    }

    public void performRotfiendOoze(ProjectileHitEvent event){
        Entity proj = event.getEntity();
        CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.3f)
            .playAt(proj);
        proj.getWorld().spawn(proj.getLocation(), AreaEffectCloud.class, e ->{
            e.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 1), true);
            e.setDuration((int)(e.getDuration() * .5f));
            e.setRadius(e.getRadius() * .65f);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onProjectileRicochet(ProjectileRicochetEvent event) {
        sporeBurstRicochet(event);
    }

    public boolean sporeBurstRicochet(ProjectileRicochetEvent event){
        Entity e = event.getNewProjectile();
        CruxEntity crux = CruxEntity.entity(e);
        SporeburstChargeComponent data = crux.get(AbyssComponents.SPOREBURST_CHARGE);
        if(data==null) return false;

        CruxAttributeAccessor attributes = CruxAttributeHandler.attributeHandler()
            .addModifier(CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(0.5))
            .addModifier(CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(4))
            ;

        Location loc = event.getProjectile().getLocation();
        double range = 4D;

        new SporeburstFumes(
            loc, attributes, 80,
            new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                .colorTransition(
                    Color.GREEN, Color.YELLOW
                )
                .offset(range/2, range/2, range/2)
                .extra(.2)
                .count(CruxMath.random(20, 25))
                .location(loc),
            new GetEntityNear<>(LivingEntity.class)
                .center(loc)
                .range(range),
            EntityDamager.getOwner(e),
            Set.of(
                new PotionEffect(PotionEffectType.POISON, 100, 0),
                new PotionEffect(PotionEffectType.WEAKNESS, 100, 0)
            )
        ).start();

        return true;
    }


    public boolean sporeBurst(ProjectileHitEvent event){
        Entity e = event.getEntity();
        CruxEntity crux = CruxEntity.entity(e);
        SporeburstChargeComponent data = crux.get(AbyssComponents.SPOREBURST_CHARGE);
        if(data==null) return false;

        Location loc = e.getLocation();
        double range = 4D;

        Entity owner = EntityDamager.getOwner(e);
        for (LivingEntity hit : new GetEntityNear<>(LivingEntity.class)
            .center(loc)
            .range(range)
            .find()) {
            EntityDamager.entityDamager(hit, owner)
                .setHitPosition(loc)
                .attack(8D, 11D, 15D);
            hit.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
            hit.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
        }

        new ParticleBuilder(Particle.ITEM)
            .count(CruxMath.random(25, 30))
            .extra(.5)
            .offset(range/2, range/2, range/2)
            .data(new ItemStack(Material.SLIME_BALL))
            .location(loc)
            .spawn();
        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .colorTransition(
                Color.GREEN, Color.YELLOW
            )
            .offset(range/2, range/2, range/2)
            .extra(.2)
            .count(CruxMath.random(15, 20))
            .location(loc)
            .spawn();
        new ParticleBuilder(Particle.EXPLOSION)
            .count(CruxMath.random(2, 3))
            .extra(.5)
            .offset(range/3, range/3, range/3)
            .location(loc)
            .spawn();

        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 1.5f).playAt(e);
        CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, 1.5f).playAt(e);
        return true;
    }
}

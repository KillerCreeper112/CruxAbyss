package killercreepr.cruxabyss.core.entity.tickable;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.core.entity.tickable.ListenerActiveEntityTickable;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class ActiveToxicFumesTickable extends ListenerActiveEntityTickable implements Listener {
    protected final CruxAttributeContainer attributes;
    protected final float chance;
    protected final float range;
    protected final Collection<PotionEffect> effects;
    protected final ParticleBuilderSupplier supplier;
    protected final CreateSound sound;
    protected final ParticleBuilder particle;
    protected final UPlayer uPlayer;
    public ActiveToxicFumesTickable(Entity entity, EntityTickable tickable,
                                    CruxSlot slot, CruxAttributeContainer attributes,
                                    float chance, float range, Collection<PotionEffect> effects, ParticleBuilderSupplier supplier, CreateSound sound) {
        super(entity, tickable, slot);
        this.attributes = attributes;
        this.chance = chance;
        this.range = range;
        this.effects = effects;
        this.uPlayer = UPlayer.getPlayer(entity);
        this.supplier = supplier;
        this.particle = supplier == null ? null : supplier.build();
        this.sound = sound;
    }

    @Override
    public void tick() {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(entity)) return;
        if(!CruxMath.testChance(chance)) return;
        showParticles();
        new GetEntityNear<>(LivingEntity.class)
            .center(entity)
            .range(range)
            .filter(e ->{
                if(e.equals(entity)) return false;
                if(uPlayer == null) return true;
                return !uPlayer.hasFriend(e.getUniqueId()) && !uPlayer.isApartOfParty(e.getUniqueId());
            }).find().forEach(hit ->{
                EntityDamager.entityDamager(hit, entity)
                    .attack(
                        attributes.getValue(CruxAttribute.ATTACK_DAMAGE),
                        attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK),
                        attributes.getValue(CruxAttribute.ATTACK_KNOCKBACK_UP)
                    );
                if(effects != null) hit.addPotionEffects(effects);
            });
    }

    public void showParticles(){
        if(particle != null) particle.location(entity.getLocation().add(0, entity.getHeight()/2, 0)).spawn();
        if(sound != null) sound.playAt(entity);
    }

}

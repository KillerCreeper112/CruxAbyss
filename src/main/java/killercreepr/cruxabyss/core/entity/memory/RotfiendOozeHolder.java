package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.usurvive.core.entity.memory.ProjectileHitHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class RotfiendOozeHolder extends ProjectileHitHolder {
    public RotfiendOozeHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public RotfiendOozeHolder(@NotNull EntityMemory parent) {
        super(parent);
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity proj = event.getEntity();
        CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.3f)
            .playAt(proj);
        proj.getWorld().spawn(proj.getLocation(), AreaEffectCloud.class, e ->{
            e.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 1), true);
            e.setDuration((int)(e.getDuration() * .75f));
        });
    }
}

package killercreepr.cruxabyss.core.component.impl;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ToxsporeComponent implements CruxBlockComponent {
    @Override
    public void onBroken(@NotNull CruxBlockBreakEvent event) {
        Block b = event.getContext().getBlock();
        Location spawn = b.getLocation().toCenterLocation();
        //new java.awt.Color(0xC0FF00);
        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .colorTransition(
                Color.fromRGB(0xC0FF00),
                Color.fromRGB(0xA40AD8),
                1.5f
            )
            .count(CruxMath.random(15, 25))
            .offset(1, .3, 1)
            .extra(.5)
            .location(spawn)
            .spawn()
        ;
        CreateSound.sound(Sound.ENTITY_PUFFER_FISH_BLOW_OUT, 1.5f).playAt(spawn);
        CreateSound.sound(Sound.ENTITY_CAT_HISS, 2f).playAt(spawn);

        new GetEntityNear<>(LivingEntity.class)
            .center(spawn)
            .range(2)
            .find().forEach(hit ->{
                hit.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));
            });
    }
}

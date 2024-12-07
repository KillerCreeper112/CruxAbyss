package killercreepr.cruxabyss.core.entity.memory;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ScourgerBulletLargeData extends ScourgerBulletData{
    public static final Key KEY = Crux.key("scourger_bullet_large");
    public ScourgerBulletLargeData(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public ScourgerBulletLargeData(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    @Override
    public void onHit(Location loc){
        CreateSound.sound(Sound.ENTITY_SHULKER_BULLET_HIT, .3f).playAt(loc);
        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .location(loc)
            .offset(.7, .7, .7)
            .extra(.3)
            .data(new Particle.DustTransition(Color.GREEN,  Color.YELLOW, 1f))
            .count(CruxMath.random(8, 12))
            .spawn()
        ;

        loc.getWorld().spawn(loc, AreaEffectCloud.class, e ->{
            e.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 40, 1), false);
            e.setDuration(30);
            e.setRadius(2.3f);
        });
    }

    @Override
    public void particleTick(@NotNull Entity entity) {
        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .location(entity.getLocation())
            .offset(.5, .5, .5)
            .extra(.3)
            .data(new Particle.DustTransition(Color.GREEN,  Color.YELLOW, .9f))
            .count(CruxMath.random(5, 8))
            .spawn()
        ;
    }
}

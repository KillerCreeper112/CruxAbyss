package killercreepr.cruxabyss.core.entity.memory;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScourgerBulletData extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("scourger_bullet");
    public ScourgerBulletData(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public ScourgerBulletData(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    protected Location lastLocation;
    protected int particleTick = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        lastLocation = entity.getLocation();

        if(entity.isOnGround()){
            Crux.scheduler().runTask(entity::remove);
            return;
        }

        particleTick(entity);
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        if(e != null) lastLocation = e.getLocation();
        if(lastLocation == null) return;
        Crux.scheduler().runTask(() ->{
            onHit(lastLocation);
        });
    }

    public void onHit(Location loc){
        CreateSound.sound(Sound.ENTITY_SHULKER_BULLET_HIT, .6f).playAt(loc);
        new ParticleBuilder(Particle.DUST)
            .location(loc)
            .offset(.7, .7, .7)
            .extra(.3)
            .data(new Particle.DustOptions(Color.GREEN, 1f))
            .count(CruxMath.random(8, 12))
            .spawn()
        ;
    }

    public void particleTick(@NotNull Entity entity){
        particleTick++;
        if(particleTick >= 2){
            particleTick = 0;
            playParticles(entity);
        }
    }

    public void playParticles(@NotNull Entity entity){
        new ParticleBuilder(Particle.DUST)
            .location(entity.getLocation())
            .offset(.5, .5, .5)
            .extra(.3)
            .data(new Particle.DustOptions(Color.GREEN, .7f))
            .count(CruxMath.random(5, 8))
            .spawn()
        ;
    }
}

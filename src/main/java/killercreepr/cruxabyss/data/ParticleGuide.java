package killercreepr.cruxabyss.data;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.game.SimpleStatutable;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.values.ValuesProvider;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ParticleGuide extends SimpleStatutable {
    protected final @NotNull Player p;
    protected final @NotNull Location from;
    protected final @NotNull Location to;
    protected final int particleAmount;
    protected final @NotNull ValuesProvider cfg;
    protected final Location toCloneSameY;
    public ParticleGuide(@NotNull Player p, @NotNull Location from, @NotNull Location to, int particleAmount, @NotNull ValuesProvider cfg) {
        this.p = p;
        this.from = from;
        this.to = to;
        this.particleAmount = particleAmount;
        this.cfg = cfg;

        this.toCloneSameY = to.clone();
        toCloneSameY.setY(from.getY());
    }

    protected int currentParticleAmount;
    protected Location currentLocation;
    protected Vector dir;
    @Override
    public void started() {
        super.started();
        currentParticleAmount = particleAmount;
        currentLocation = CruxLoc.shiftToward(from.clone(), toCloneSameY, cfg.ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START().value().doubleValue());
        dir = currentLocation.getDirection();
        distance = p.getLocation().distance(to);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(shouldStop()){
                    cancel();
                    return;
                }
                tick();
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 3L);
    }

    public double getParticleY(){
        if(p.getY() >= to.getY()){
            return CruxMath.random(-.3D, -.2D);
        }
        return CruxMath.random(.2D, .3D);
    }

    protected double distance;
    @Override
    public void tick() {
        double speed = CruxMath.clamp(
            distance*cfg.ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER().value().doubleValue(),
            .01, .6
        );
        Location spawn = CruxLoc.shift(currentLocation.clone(), 0D,
            cfg.ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL().value().doubleValue(),
            cfg.ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE().value().doubleValue());
        p.spawnParticle(Particle.FLAME, spawn, 0,
            dir.getX(), getParticleY(), dir.getZ(), speed
        );

        currentParticleAmount--;
        if(currentParticleAmount < 1){
            setStopped();
            return;
        }

        currentLocation = CruxLoc.shiftToward(currentLocation, toCloneSameY, cfg.ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT().value().doubleValue());
    }
}

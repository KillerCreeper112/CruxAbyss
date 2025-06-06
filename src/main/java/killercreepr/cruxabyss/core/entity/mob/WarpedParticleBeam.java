package killercreepr.cruxabyss.core.entity.mob;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.core.Crux;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class WarpedParticleBeam {
    private final Location start;
    private final Location end;
    private final ParticleBuilder particle;
    private final World world;

    private final int steps;
    private final double warpStrength;
    private final int lifetimeTicks;

    private int tick = 0;
    private final Vector perpendicular;

    public WarpedParticleBeam(Location start, Location end, ParticleBuilder particle, int steps, double warpStrength, int lifetimeTicks) {
        this.start = start.clone();
        this.end = end.clone();
        this.particle = particle;
        this.world = start.getWorld();
        this.steps = steps;
        this.warpStrength = warpStrength;
        this.lifetimeTicks = lifetimeTicks;

        // Randomize perpendicular vector
        Vector dir = end.toVector().subtract(start.toVector()).normalize();
        Vector alt = new Vector(0, 1, 0);
        if (Math.abs(dir.dot(alt)) > 0.9) alt = new Vector(1, 0, 0); // avoid collinearity
        this.perpendicular = dir.clone().crossProduct(alt).normalize();
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(Crux.getMainPlugin(), this::tick, 0L, 1L);
    }

    private void tick() {
        if (tick > lifetimeTicks) return;

        double progress = (double) tick / lifetimeTicks;
        Vector dir = end.toVector().subtract(start.toVector());
        double length = dir.length();
        dir.normalize();

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;

            // Position along the line
            Vector point = start.toVector().clone().add(dir.clone().multiply(length * t));

            // Warping animation based on time and progress
            double angle = t * Math.PI * 4 + progress * Math.PI * 2;
            double offset = Math.sin(angle) * warpStrength;

            // Apply perpendicular offset
            point.add(perpendicular.clone().multiply(offset));

            // Optional: vertical wobble
            point.setY(point.getY() + Math.cos(angle * 0.8) * warpStrength * 0.5);

            particle.location(point.toLocation(world)).spawn();
        }

        tick++;
    }
}

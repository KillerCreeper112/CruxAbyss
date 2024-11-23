package killercreepr.cruxabyss.core.structure;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.core.Crux;
import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ActiveTestStructure extends SimpleActiveStructure {
    public ActiveTestStructure(@NotNull StoredStructure data, @NotNull Chunk chunk) {
        super(data, chunk);
    }

    @Override
    public void tick() {
        super.tick();
        new CreateRectangle(center.getWorld(), getData().getBoundingBox(), true, true, 1D)
            .getLocations().forEach(l ->{
                new ParticleBuilder(Particle.HAPPY_VILLAGER).location(l).spawn();
            });
    }

    @Override
    public void started() {
        super.started();
        Crux.log(Level.INFO, "TEST STARTED " + getCenter());
    }

    @Override
    public void stopped() {
        super.stopped();
        Crux.log(Level.INFO, "TEST STARTED " + getCenter());
    }
}

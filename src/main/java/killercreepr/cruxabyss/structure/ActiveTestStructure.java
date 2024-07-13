package killercreepr.cruxabyss.structure;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

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
        Bukkit.broadcastMessage("TEST STARTED");
    }

    @Override
    public void stopped() {
        super.stopped();
        Bukkit.broadcastMessage("TEST STOPPED");
    }
}

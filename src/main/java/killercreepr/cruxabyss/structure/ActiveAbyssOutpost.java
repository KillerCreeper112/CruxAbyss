package killercreepr.cruxabyss.structure;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssOutpost extends SimpleActiveStructure {
    protected final @NotNull AbyssOutpost data;
    public ActiveAbyssOutpost(@NotNull AbyssOutpost data, @NotNull Chunk chunk) {
        super(data, chunk);
        this.data = data;
    }

    @Override
    public void tick() {
        super.tick();
        //data.setLifeSpan(data.getLifeSpan() - 1);
        Bukkit.broadcastMessage(data.getLifeSpan() + "");
        new CreateRectangle(center.getWorld(), data.getBoundingBox(), true, true, .5D)
            .getLocations().forEach(l ->{
                new ParticleBuilder(Particle.HAPPY_VILLAGER).location(l).spawn();
            });
    }

    @Override
    public boolean shouldStop() {
        return data.getLifeSpan() < 1;
    }

    @Override
    public void started() {
        super.started();
        Bukkit.broadcastMessage("ABYSS OUTPOST STARTED");
    }

    @Override
    public void stopped() {
        super.stopped();
        Bukkit.broadcastMessage("ABYSS OUTPOST STOPPED");
    }
}

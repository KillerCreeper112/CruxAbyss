package killercreepr.cruxabyss.structure;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssOutpost extends SimpleActiveStructure {
    protected final @NotNull StoredAbyssOutpost data;
    public ActiveAbyssOutpost(@NotNull StoredAbyssOutpost data, @NotNull Chunk chunk) {
        super(data, chunk);
        this.data = data;
    }

    protected int tick = 0;
    @Override
    public void tick() {
        super.tick();
        tick++;
        if(tick % 60 == 0){

        }
        //Bukkit.broadcastMessage(data.getLifeSpan() + "");
        new CreateRectangle(center.getWorld(), data.getBoundingBox(), true, true, .5D)
            .getLocations().forEach(l ->{
                new ParticleBuilder(Particle.HAPPY_VILLAGER).location(l).spawn();
            });
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

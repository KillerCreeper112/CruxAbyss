package killercreepr.cruxabyss.structure;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.util.CruxLoc;
import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssOutpost extends SimpleActiveStructure {
    protected final @NotNull AbyssOutpost data;
    public ActiveAbyssOutpost(@NotNull AbyssOutpost data, @NotNull Chunk chunk) {
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
        data.setLifeSpan(data.getLifeSpan() - 1);
        //Bukkit.broadcastMessage(data.getLifeSpan() + "");
        new CreateRectangle(center.getWorld(), data.getBoundingBox(), true, true, .5D)
            .getLocations().forEach(l ->{
                new ParticleBuilder(Particle.HAPPY_VILLAGER).location(l).spawn();
            });

        if(shouldStop()){
            data.persist = false;
            for(Block b : CruxLoc.getNearbyBlocks(center, 15)){
                b.setType(Material.AIR);
            }
            center.getWorld().playSound(center.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3f, 1f);
        }
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

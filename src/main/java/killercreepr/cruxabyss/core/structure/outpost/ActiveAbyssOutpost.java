package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateRectangle;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveAbyssOutpost implements ManagedTicked {
    private static final int tickRate = AbyssOutpostData.tickRate;
    protected final ActiveStructure active;
    protected final AbyssOutpostData data;
    protected final Map<OutpostUpgrade, TickedOutpostUpgrade> activeUpgrades = new ConcurrentHashMap<>();
    public ActiveAbyssOutpost(@NotNull ActiveStructure active) {
        this.active = active;
        this.data = active.getData().get(AbyssComponents.ABYSS_OUTPOST_DATA);
        Objects.requireNonNull(data, "No!");
    }

    public ActiveStructure getActive() {
        return active;
    }

    public AbyssOutpostData getData() {
        return data;
    }

    public void resetOwner(){
        data.owner = null;
    }

    public void capture(Player p){
        data.owner = p.getUniqueId();
        data.timeCaptured = System.currentTimeMillis();
    }

    @Override
    public void started() {
        ManagedTicked.super.started();
        initiateUpgrades();
    }

    @Override
    public void stopped() {
        ManagedTicked.super.stopped();
        activeUpgrades.values().forEach(t -> t.stopped(tick, tickRate));
    }

    public void initiateUpgrades(){
        activeUpgrades.clear();
        data.upgrades.forEach((upgrade, level) ->{
            TickedOutpostUpgrade stored = upgrade.createActive(this, level);
            if(stored == null) return;
            activeUpgrades.put(upgrade, stored);
        });
        activeUpgrades.values().forEach(t -> t.started(tick, tickRate));
    }

    protected int tick = 0;
    @Override
    public void tick() {
        tick++;
        activeUpgrades.values().forEach(t -> t.tick(tick, tickRate));
        World world = active.getChunk().getWorld();
        ShapeScheduler.builder()
            .shape(CreateRectangle.builder()
                .boundingBox(active.getData().getBoundingBox())
                .spacing(1)
                .build())
            .locationTick(ctx ->{
                CruxPosition pos = ctx.getLocation();
                Location loc = pos.toLocation(world);
                world.getPlayers().forEach(p -> p.spawnParticle(Particle.FLAME, loc, 0));
            })
            .buildCached().schedule(0);

        BoundingBox outerBox = active.getData().get(StoredStructureComponents.OUTER_BOX);

        ShapeScheduler.builder()
            .shape(CreateRectangle.builder()
                .boundingBox(outerBox)
                .spacing(1)
                .build())
            .locationTick(ctx ->{
                CruxPosition pos = ctx.getLocation();
                Location loc = pos.toLocation(world);
                world.getPlayers().forEach(p -> p.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 0));
            })
            .buildCached().schedule(0);
    }
}

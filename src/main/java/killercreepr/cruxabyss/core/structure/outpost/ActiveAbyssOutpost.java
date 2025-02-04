package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.api.values.AbyssOutpostInvasionCfg;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import org.bukkit.entity.Player;
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
    protected final float invasionChance = 10f;
    @Override
    public void tick() {
        tick++;
        activeUpgrades.values().forEach(t -> t.tick(tick, tickRate));
        if(data.owner == null) return;

        AbyssOutpostInvasionCfg cfg = data.cfg();

        if(tick % 100 == 0){
            if(CruxMath.testChance(cfg.ABYSS_OUTPOST_INVASION_ACTIVE_CHANCE().value().doubleValue())){
                if(!data.wasInvadedWithin(cfg.ABYSS_OUTPOST_INVASION_COOLDOWN().value().intValue())) data.attemptInvasion();
            }
        }
        /*World world = active.getChunk().getWorld();
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
            .buildCached().schedule(0);*/
    }
}

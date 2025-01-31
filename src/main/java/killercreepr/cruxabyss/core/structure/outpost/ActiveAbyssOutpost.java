package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.world.abyss.event.OutpostInvasionEvent;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.world.entity.NaturalCruxMobSpawn;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawnGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    public void invasion(){
        resetOwner();
        data.timeInvaded = System.currentTimeMillis();
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
    protected final float invasionChance = 25f;
    @Override
    public void tick() {
        tick++;
        activeUpgrades.values().forEach(t -> t.tick(tick, tickRate));

        if(tick % 100 == 0){
            if(CruxMath.testChance(invasionChance)){
                CruxWorld world = CruxCore.core().worldManager().getWorld(active.getChunk().getWorld().getUID());
                WorldEventsModule events = world.getModule(WorldEventsModule.class);
                if(events.hasWorldEventOfType(OutpostInvasionEvent.class)) return;

                NaturalEntitySpawnGroup spawnGroup = new SimpleNaturalEntitySpawnGroup(
                    0, 0f, Set.of(
                    new NaturalCruxMobSpawn(10, 0f, AbyssMob.TOXICATOR) {
                        @Override
                        public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                            return true;
                        }
                    },
                    new NaturalCruxMobSpawn(6, 0f, AbyssMob.SCOURGER) {
                        @Override
                        public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                            return true;
                        }
                    },
                    new NaturalCruxMobSpawn(3, 0f, AbyssMob.PLAGUEWING_MOUNT_SCOURGER) {
                        @Override
                        public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                            return true;
                        }
                    }
                )
                ) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                        return true;
                    }
                };

                events.addWorldEvent(new OutpostInvasionEvent(
                    world, active.getData(), spawnGroup, 3,
                    CruxMath.random(3000, 4200) //2.5 min - 3.5 min
                ));
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

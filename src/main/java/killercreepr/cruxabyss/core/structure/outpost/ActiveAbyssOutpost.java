package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.api.structure.StoredLootHolderStructure;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import killercreepr.cruxform.api.shape.CreateLine;
import killercreepr.cruxform.api.shape.CreateRectangle;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActiveAbyssOutpost implements ManagedTicked {
    public final Map<CruxPosition, StoredStructure> lootHolders = new HashMap<>();
    protected final ActiveStructure active;
    protected final AbyssOutpostData data;
    public ActiveAbyssOutpost(@NotNull ActiveStructure active) {
        this.active = active;
        this.data = active.getData().get(AbyssComponents.ABYSS_OUTPOST_DATA);
        Objects.requireNonNull(data, "No!");
    }

    public Map<CruxPosition, StoredStructure> getLootHolders() {
        return lootHolders;
    }

    public ActiveStructure getActive() {
        return active;
    }

    public AbyssOutpostData getData() {
        return data;
    }

    /*public ActiveAbyssOutpost(@NotNull StoredAbyssOutpost data, @NotNull Chunk chunk) {
        super(data, chunk);
        this.data = data;
        updateLootHolders();
    }*/

    public void updateLootHolders(){
        lootHolders.clear();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(active.getCenter().getWorld().getUID());
        if(crux == null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;
        BoundingBox box = active.getData().getBoundingBox();
        Bukkit.broadcastMessage("looking for loot holders");
        module.getStored(StoredLootHolderStructure.class, stored ->{
            BoundingBox check = stored.getBoundingBox();
            Bukkit.broadcastMessage("checking=" + stored.getPosition() + " intersect=" + doBoundingBoxesIntersect(box, check) + ", " + "contains=" + box.contains(check));
            return box.contains(check) || doBoundingBoxesIntersect(box, check);
        }).forEach(stored ->{
                lootHolders.put(stored.getPosition(), stored);
                Bukkit.broadcastMessage("found: " + stored.getPosition());
            });
    }

    public static boolean doBoundingBoxesIntersect(BoundingBox box1, BoundingBox box2) {
        // Check for overlap in the X dimension
        boolean overlapX = box1.getMaxX() > box2.getMinX() && box1.getMinX() < box2.getMaxX();

        // Check for overlap in the Y dimension
        boolean overlapY = box1.getMaxY() > box2.getMinY() && box1.getMinY() < box2.getMaxY();

        // Check for overlap in the Z dimension
        boolean overlapZ = box1.getMaxZ() > box2.getMinZ() && box1.getMinZ() < box2.getMaxZ();

        // Return true if there is overlap in all three dimensions
        return overlapX && overlapY && overlapZ;
    }

    /*@Override
    public @NotNull AbyssOutpost getStructure() {
        return (AbyssOutpost) this.getData().getParent();
    }*/

    public void resetOwner(){
        data.owner = null;
    }

    public void capture(Player p){
        data.owner = p.getUniqueId();
    }

    @Override
    public void started() {
    }

    @Override
    public void tick() {
        if(lootHolders.isEmpty()) updateLootHolders();
        lootHolders.values().forEach(structure ->{
            World world = structure.getChunk().toBukkitWorld();
            ShapeScheduler.builder()
                .shape(CreateLine.builder()
                    .start(structure.getPosition())
                    .end(structure.getPosition().add(0, 5, 0))
                    .spacing(.5)
                    .build())
                .locationTick(ctx ->{
                    CruxPosition pos = ctx.getLocation();
                    Location loc = pos.toLocation(world);
                    world.spawnParticle(Particle.FLAME, loc, 0);
                })
                .buildCached().schedule(0);
        });

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

    @Override
    public void stopped() {
    }

    /*@Override
    @NotNull
    public StoredAbyssOutpost getData() {
        return data;
    }*/
}

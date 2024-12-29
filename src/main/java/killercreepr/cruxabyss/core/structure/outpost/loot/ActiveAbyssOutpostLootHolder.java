package killercreepr.cruxabyss.core.structure.outpost.loot;

import killercreepr.crux.api.block.CruxBlockWrapper;
import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.Pair;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.values.AbyssOutpostLootHolderCfg;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class ActiveAbyssOutpostLootHolder implements ManagedTicked {
    protected final ActiveStructure active;
    protected final AbyssOutpostLootHolderData data;
    protected ActiveAbyssOutpost outpost;
    public ActiveAbyssOutpostLootHolder(@NotNull ActiveStructure active) {
        this.active = active;
        this.data = active.getData().get(AbyssComponents.ABYSS_OUTPOST_LOOT_HOLDER_DATA);
        Objects.requireNonNull(data, "No!");
    }

    public AbyssOutpostLootHolderCfg cfg(){
        return (AbyssOutpostLootHolderCfg) CruxAbyss.inst().values();
    }

    public ActiveAbyssOutpost findActiveOutpost(){
        CruxWorld world = CruxCore.core().worldManager().getWorld(active.getChunk().getWorld().getUID());
        if(world == null) return null;
        StructureWorldModule module = world.getModule(StructureWorldModule.class);
        if(module == null) return null;
        ActiveStructure structure = CruxCollection.getFirst(module.getActive(e -> e.has(AbyssComponents.ACTIVE_ABYSS_OUTPOST)));
        return structure == null ? null : structure.get(AbyssComponents.ACTIVE_ABYSS_OUTPOST);
    }

    protected int checkOutpostCooldown = 0;
    public ActiveAbyssOutpost outpost(){
        if(outpost != null) return outpost;
        if(checkOutpostCooldown > 0){
            checkOutpostCooldown--;
            return null;
        }
        outpost = findActiveOutpost();
        checkOutpostCooldown = 300 / cfg().ABYSS_OUTPOST_LOOT_HOLDER_TICK_TIME().value().intValue();
        return outpost;
    }

    protected int tick = 0;
    @Override
    public void tick() {
        tick++;
        if(tick < cfg().ABYSS_OUTPOST_LOOT_HOLDER_TICK_TIME().value().intValue()) return;

        ActiveAbyssOutpost outpost = outpost();
        if(outpost == null || outpost.getData().owner == null){
            removeHologram();
            return;
        }

        tick = 0;
        Crux.scheduler().runTask(() -> updateHologram());
        Pair<Integer, Long> nextGen = getGenerateAmount(System.currentTimeMillis());
        if(nextGen == null) return;
        data.lastGenerated = System.currentTimeMillis();
        data.nextGeneration = nextGen.getSecond();
        int amount = nextGen.getFirst();
        //If amount more or equal to 10, just generate the whole thing.
        double probabilityMultiplier = amount >= 10 ? 10D : 1D;
        generate(probabilityMultiplier, CruxMath.clamp(nextGen.getFirst(), 0, 10));
    }

    public TextDisplay getHologram(){
        UUID uuid = data.hologramUUID;
        if(uuid == null) return null;
        World world = active.getChunk().getWorld();
        if(world.getEntity(uuid) instanceof TextDisplay d) return d;
        Crux.log(Level.SEVERE, "[ABYSS OUTPOST LOOT HOLDER] ENTITY UUID: " + uuid + " not found or is not a TextDisplay! Chunk(" + active.getChunk().getX() + ", " + active.getChunk().getZ() + ")");
        return null;
    }

    public TextDisplay getOrCreateHologram(){
        TextDisplay existing = getHologram();
        if(existing != null) return existing;

        World world = active.getChunk().getWorld();
        Location spawn = getHologramPosition();
        TextDisplay display = world.spawn(spawn, TextDisplay.class, e ->{
            e.setBillboard(Display.Billboard.CENTER);
            e.setViewRange(12f);
            updateHologram(e);
        });
        data.hologramUUID = display.getUniqueId();
        return display;
    }


    public Location getHologramPosition(){
        Location loc = active.getData().getPosition().toLocation(active.getChunk().getWorld()).toCenterLocation();
        Vector offset = active.getData().getParent().get(AbyssComponents.ABYSS_HOLOGRAM_OFFSET);
        if(offset != null) loc.add(offset);
        return loc;
    }

    public boolean updateHologram(){
        if(!active.getData().getParent().has(AbyssComponents.ABYSS_HOLOGRAM_FORMAT)) return false;
        TextDisplay display = getOrCreateHologram();
        updateHologram(display);
        return true;
    }

    public void removeHologram(){
        TextDisplay found = getHologram();
        if(found != null){
            Crux.scheduler().runTask(() -> found.remove());
            data.hologramUUID = null;
        }
    }

    public void updateHologram(TextDisplay display){
        String format = active.getData().getParent().get(AbyssComponents.ABYSS_HOLOGRAM_FORMAT);
        Objects.requireNonNull(format, "Nope nope");
        Component text = Crux.format().deserialize(format, TagContainer.string(Tag.parsed("abyss_outpost_loot_holder_generation_time",
            getTimeLeftBeforeGeneration() + "")));

        display.text(text);
    }

    public long getTimeLeftBeforeGeneration(){
        long difference = data.nextGeneration - System.currentTimeMillis();
        return Math.max(0, difference);
    }

    public long getNextGenerationAddonTicks(){
        return cfg().ABYSS_OUTPOST_LOOT_HOLDER_GENERATE_TIME().value().intValue();
    }

    //amount | nextGenTime
    public @Nullable Pair<Integer, Long> getGenerateAmount(long time){
        long nextGen = data.nextGeneration;
        if(time < nextGen) return null;
        if(nextGen <= 0) return new Pair<>(1, time + (50L * getNextGenerationAddonTicks()));
        int amount = 0;
        while(time >= nextGen){
            amount++;
            nextGen += (50L * getNextGenerationAddonTicks());
        }
        return new Pair<>(amount, nextGen);
    }

    public void generate(double probabilityMultiplier, int amount){
        while(amount > 0){
            amount--;
            generate(probabilityMultiplier);
        }
    }

    public void generate(double probabilityMultiplier){
        BoundingBox box = active.getData().get(StoredStructureComponents.OUTER_BOX);
        Crux.scheduler().runTask(() ->{
            generateOre(active.getChunk().getWorld(), box, CruxMath.random(), probabilityMultiplier);
        });
    }

    public void generateOre(World world, BoundingBox box, Random random, double probabilityMultiplier) {
        int minX = (int) Math.floor(box.getMinX());
        int minY = (int) Math.floor(box.getMinY());
        int minZ = (int) Math.floor(box.getMinZ());
        int maxX = (int) Math.floor(box.getMaxX() - .5);
        int maxY = (int) Math.floor(box.getMaxY() - .5);
        int maxZ = (int) Math.floor(box.getMaxZ() - .5);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    double probability = calculateOreProbability(x, y, z, box) * probabilityMultiplier;
                    if(random.nextDouble() >= probability) continue;

                    Block b = world.getBlockAt(x, y, z);
                    if (canReplaceBlock(b)) {
                        placeOreBlock(b);
                    }
                }
            }
        }
    }

    private double calculateOreProbability(int x, int y, int z, BoundingBox box) {
        double centerY = box.getMinY() + (box.getMaxY() - box.getMinY()) / 2;
        double distanceFromCenter = Math.abs(centerY - y);

        // Use a linear gradient for probability (adjust as needed)
        double gradient = 1.0 - (distanceFromCenter / (box.getMaxY() - box.getMinY()));
        return Math.max(0.1, gradient); // Ensure at least some probability
    }

    private boolean canReplaceBlock(Block b){
        if(!b.isEmpty() && !b.isReplaceable()) return false;
        return isBlockSupported(b);
    }

    private boolean isBlockSupported(Block b) {
        Block ground = b.getRelative(BlockFace.DOWN);
        return ground.isSolid();
    }

    private static CruxBlockWrapper wrapper(Key m){
        return Crux.handlers().block().getBlockWrapper(m);
    }
    private void placeOreBlock(Block b) {
        cfg().ABYSS_OUTPOST_LOOT_HOLDER_BLOCK_LOOT().ifPresent(table ->{
            LootContext ctx = LootContext.builder()
                .location(b.getLocation())
                .looted(b)
                .build();
            Key key = CruxCollection.getRandom(table.populateLoot(ctx));
            if(key == null) return;
            CruxBlockWrapper block = wrapper(key);
            if(block == null){
                Crux.log(Level.WARNING, "[ABYSS OUTPOST LOOT HOLDER] No CruxBlockWrapper of " + key + "! Skipping...");
                return;
            }
            block.setBlock(b,false);
        });
    }
}

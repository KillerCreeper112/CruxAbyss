package killercreepr.cruxabyss.core.world.abyss;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxabyss.core.world.generation.BlockGenerator;
import killercreepr.cruxabyss.core.world.generation.decoration.RockPopulator;
import killercreepr.cruxabyss.core.world.generation.populator.AbyssPopulator;
import killercreepr.cruxabyss.core.world.module.SimpleWorldEventsModule;
import killercreepr.cruxblocks.core.registries.CruxBlocksRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.core.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.core.world.SimpleWorld;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntityWorldSpawner;
import killercreepr.usurvive.core.block.USurviveBlocks;
import killercreepr.usurvive.core.world.generation.OreGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AbyssWorld extends SimpleWorld implements Loadable, Listener {
    public static final Map<String, List<UUID>> WORLD_TO_ABYSS_OUTPOST_OWNERS = new HashMap<>();

    public static @Nullable AbyssWorld getOrCreate(@NotNull CruxPlugin plugin, @NotNull String worldName){
        return (AbyssWorld) CruxCore.inst().worldManager().getOrCreateWorld(AbyssWorldTypes.ABYSS, worldName);
    }

    protected final NaturalEntitySpawnManager entitySpawnManager;

    public AbyssWorld(@NotNull World world, @NotNull Collection<CruxWorldModuleCreator> modules) {
        this(world, new Random(world.getSeed()), modules);
    }

    public AbyssWorld(@NotNull World world, @NotNull Random random, @NotNull Collection<CruxWorldModuleCreator> modules) {
        super(world, random, modules);
        entitySpawnManager = new NaturalEntitySpawnManager(this, createEntitySpawner());
        WorldEventsModule module = new SimpleWorldEventsModule(this);
        this.modules.add(module);
        this.tickedModules.add(module);
    }

    protected int wave = 1;
    protected float difficulty = 1f;
    protected int daysPassed = 0;

    public SimpleNaturalEntityWorldSpawner createEntitySpawner(){
        return new SimpleNaturalEntityWorldSpawner(CruxAbyss.inst(), random,
            AbyssRegistries.ABYSS_NATURAL_ENTITY_SPAWN_GROUP,
            500, 34, 10);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDelete() {
        StructureWorldModule module = getModule(StructureWorldModule.class);
        if(module != null){
            List<UUID> abyssOwners = new ArrayList<>();
            module.getStored(stored -> stored.has(AbyssComponents.ABYSS_OUTPOST_DATA)).forEach(stored ->{
                AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
                Objects.requireNonNull(data);
                if(data.owner == null) return;
                abyssOwners.add(data.owner);
            });
            if(abyssOwners.isEmpty()){
                WORLD_TO_ABYSS_OUTPOST_OWNERS.remove(getName());
            }else{
                WORLD_TO_ABYSS_OUTPOST_OWNERS.put(getName(), abyssOwners);
            }
        }

        super.onDelete();
    }

    @Override
    public void tick(){
        super.tick();
        entitySpawnManager.tick();
        if(Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) && world.getTime() == 0){
            if(world.getPlayers().isEmpty()) return;
            dayEvent();
        }
    }

    private void dayEvent(){
        daysPassed++;
        Component c = MiniMessage.miniMessage().deserialize(
            "<gradient:#E62525:#6E1212>The world feels slightly less stable...</gradient>"
        );
        for(Player p : world.getPlayers()){
            p.sendMessage(c);
        }
        wave++;
        if(wave % 5 == 0){
            difficulty = CruxMath.clamp(difficulty + .1f, .5f, 1.5f);
        }
    }

    public final OreGenerator ORBIT_ORE = new OreGenerator(
        NumberProvider.uniform(1, 3), NumberProvider.uniform(1, 6),
        NumberProvider.constant(15), NumberProvider.constant(75),//was 60%

        CruxNoise.fast().frequency(.09f)
            .noiseType(CruxNoise.NoiseType.OpenSimplex2)
            .fractalType(CruxNoise.FractalType.FBm)
            .fractalOctaves(3),
        null,
        NumberProvider.uniform(16, 32)
    ){
        @Override
        public void place(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            USurviveBlocks.ORBIT_ORE.getBaseBlock().setBlock(limitedRegion, x, y, z);
        }

        @Override
        public boolean canSpawnClumpedOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            return true;
        }

        @Override
        public boolean canSpawnOre(@NotNull LimitedRegion limitedRegion, int x, int y, int z) {
            for(int xAddon = -1; xAddon < 2; xAddon++){
                for(int zAddon = -1; zAddon < 2; zAddon++){
                    for(int yAddon = -1; yAddon < 2; yAddon++){
                        if(xAddon == 0 && yAddon == 0 && zAddon == 0) continue;
                        if(!limitedRegion.isInRegion(x+xAddon, y+yAddon, z+zAddon)) continue;
                        if(!testBlock(limitedRegion.getBlockData(x+xAddon, y+yAddon, z+zAddon))) return false;
                    }
                }
            }
            return true;
        }

        private boolean testBlock(BlockData data){
            Material m = data.getMaterial();
            return MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(m) ||
                MaterialSetTag.DEEPSLATE_ORE_REPLACEABLES.isTagged(m) ||
                AbyssBlocks.PLAGUE_STONE.getBlock(data) != null ||
                AbyssBlocks.PLAGUE_DIRT.getBlock(data) != null;
        }
    };

    @Override
    public void onInitiate() {
        super.onInitiate();
        world.getPopulators().add(new RockPopulator(new BlockGenerator() {
            @Override
            public void set(@NotNull LimitedRegion region, int x, int y, int z) {
                region.setType(x,y,z, Material.IRON_ORE);
            }
        }));
        AbyssPopulator master = new AbyssPopulator();
        world.getPopulators().add(master);
        world.getPopulators().add(ORBIT_ORE);

        CruxAbyss.inst().registerListeners(this);
    }

    @Override
    public void onUnload(boolean save) {
        super.onUnload(save);
        HandlerList.unregisterAll(this);
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }

    public NaturalEntitySpawnManager getEntitySpawnManager() {
        return entitySpawnManager;
    }

    public int getWave() {
        return wave;
    }

    public float getDifficulty() {
        return Math.min(difficulty, 2f);
    }

    public int getDaysPassed() {
        return daysPassed;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent event) {
        if(!event.getBlock().getLocation().getWorld().equals(world)) return;
        float yield = event.getYield();
        int blocksToFling = (int) (event.blockList().size() * yield);
        event.setYield(0f);
        Location center = event.getBlock().getLocation().toCenterLocation();
        int flung = 0;
        for(Block b : event.blockList()){
            if(flung >= blocksToFling) break;
            flung++;
            Vector dir = b.getLocation().toCenterLocation().toVector().subtract(center.toVector());
            dir.setY(CruxMath.random(.8f, 1f));
            dir.multiply(CruxMath.random(.7f, 1f));
            center.getWorld().spawn(b.getLocation().toCenterLocation(), FallingBlock.class, falling ->{
                falling.setBlockData(b.getBlockData());
                falling.setVelocity(dir);
                if(CruxBlocksRegistries.BLOCK.getByBlockData(b.getBlockData()) != null) falling.setCancelDrop(true);
            });
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!event.getLocation().getWorld().equals(world)) return;
        float yield = event.getYield();
        int blocksToFling = (int) (event.blockList().size() * yield);
        event.setYield(0f);
        Location center = event.getLocation().toCenterLocation();
        int flung = 0;
        for(Block b : event.blockList()){
            if(flung >= blocksToFling) break;
            flung++;
            Vector dir = b.getLocation().toCenterLocation().toVector().subtract(center.toVector());
            dir.setY(CruxMath.random(.8f, 1f));
            dir.multiply(CruxMath.random(.7f, 1f));

            center.getWorld().spawn(b.getLocation().toCenterLocation(), FallingBlock.class, falling ->{
                falling.setBlockData(b.getBlockData());
                falling.setVelocity(dir);
                if(CruxBlocksRegistries.BLOCK.getByBlockData(b.getBlockData()) != null) falling.setCancelDrop(true);
            });
        }
    }
}

package killercreepr.cruxabyss.world.abyss;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.Crux;
import killercreepr.crux.data.Loadable;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.CruxAbyss;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.registries.AbyssRegistries;
import killercreepr.cruxabyss.world.generation.BlockGenerator;
import killercreepr.cruxabyss.world.generation.decoration.RockPopulator;
import killercreepr.cruxabyss.world.generation.populator.AbyssPopulator;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxworlds.world.CruxWorld;
import killercreepr.cruxworlds.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.world.SimpleWorld;
import killercreepr.cruxworlds.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.world.entity.entity.NaturalEntitySpawner;
import killercreepr.cruxworlds.world.entity.entity.impl.SimpleNaturalEntitySpawner;
import killercreepr.cruxworlds.world.manager.CruxWorldManager;
import killercreepr.usurvive.block.USurviveBlocks;
import killercreepr.usurvive.persistence.USurvivePersist;
import killercreepr.usurvive.world.generation.OreGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Random;

public class AbyssWorld extends SimpleWorld implements Loadable, Listener {
    public static @Nullable AbyssWorld getOrCreate(@NotNull CruxPlugin plugin, @NotNull String worldName){
        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        CruxWorld activeWorld = worldManager.getWorld(worldName);
        if(activeWorld != null){
            if(!(activeWorld instanceof AbyssWorld a)) throw new UnsupportedOperationException(worldName + " is not an AbyssWorld!");
            return a;
        }

        World world = new WorldCreator(worldName).type(WorldType.AMPLIFIED)/*.generator(new AbyssChunkGenerator())*/.createWorld();
        if(world==null) return null;

        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(1000D);

        USurvivePersist.DIMENSION.set(world, "abyss");

        activeWorld = worldManager.getWorld(worldName);
        if(!(activeWorld instanceof AbyssWorld a)) throw new UnsupportedOperationException(worldName + " is not an AbyssWorld!");
        return a;
    }

    protected final NaturalEntitySpawnManager entitySpawnManager;

    public AbyssWorld(@NotNull World world, @NotNull Collection<CruxWorldModuleCreator> modules) {
        this(world, new Random(world.getSeed()), modules);
    }

    public AbyssWorld(@NotNull World world, @NotNull Random random, @NotNull Collection<CruxWorldModuleCreator> modules) {
        super(world, random, modules);
        entitySpawnManager = new NaturalEntitySpawnManager(this, createEntitySpawner());
    }

    protected int wave = 1;
    protected float difficulty = 1f;
    protected int daysPassed = 0;

    public NaturalEntitySpawner createEntitySpawner(){
        return new SimpleNaturalEntitySpawner(CruxAbyss.inst(), random,
            AbyssRegistries.ABYSS_NATURAL_ENTITY_SPAWN_GROUP,
            500, 34, 10);
    }

    public @NotNull BukkitRunnable buildRunnable(){
        return new BukkitRunnable(){
            @Override
            public void run() {
                if(!isActive()){
                    cancel();
                    return;
                }
                tick();
            }
        };
    }

    public void tick(){
        entitySpawnManager.tick();
        if(Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) && world.getTime() == 0){
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
        if(wave % 5 == 0) difficulty += .1f;
    }

    protected boolean active = false;
    public boolean isActive(){
        return active;
    }

    public final OreGenerator ORBIT_ORE = new OreGenerator(
        NumberProvider.uniform(1, 3), NumberProvider.uniform(1, 6),
        NumberProvider.constant(15), NumberProvider.constant(50),

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
                AbyssBlocks.PLAGUE_STONE.getBlock(data) != null ||
                AbyssBlocks.PLAGUE_MOSS_DIRT.getBlock(data) != null;
        }
    };

    @Override
    public void onInitiate() {
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
    public void onLoad() {
        active = true;
        Bukkit.broadcastMessage("abyss world loaded!");
        buildRunnable().runTaskTimerAsynchronously(Crux.getMainPlugin(), 0L, 1L);
    }

    @Override
    public void onUnload() {
        active = false;
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
        return difficulty;
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
                if(CruxBlocksRegistries.BLOCKS.getByBlockData(b.getBlockData()) != null) falling.setCancelDrop(true);
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
                if(CruxBlocksRegistries.BLOCKS.getByBlockData(b.getBlockData()) != null) falling.setCancelDrop(true);
            });
        }
    }
}

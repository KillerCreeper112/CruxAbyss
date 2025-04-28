package killercreepr.cruxabyss.core.world.abyss;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxKey;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.statistic.AbyssStatistic;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxabyss.core.world.generation.BlockGenerator;
import killercreepr.cruxabyss.core.world.generation.decoration.RockPopulator;
import killercreepr.cruxabyss.core.world.generation.populator.AbyssPopulator;
import killercreepr.cruxabyss.core.world.module.SimpleWorldEventsModule;
import killercreepr.cruxblocks.api.event.CustomBlockExplodeEvent;
import killercreepr.cruxblocks.api.event.CustomEntityExplodeEvent;
import killercreepr.cruxblocks.core.registries.CruxBlocksRegistries;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxgeneration.util.CruxNoise;
import killercreepr.cruxstatistics.api.bukkit.BukkitStatisticHolder;
import killercreepr.cruxstatistics.api.statistic.CruxStatisticHolder;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.core.world.NaturalEntitySpawnManager;
import killercreepr.cruxworlds.core.world.SimpleWorld;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntityWorldSpawner;
import killercreepr.usurvive.core.block.USurviveBlocks;
import killercreepr.usurvive.core.world.generation.OreGenerator;
import net.kyori.adventure.key.Key;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AbyssWorld extends SimpleWorld implements Loadable, Listener {
    public static final Map<Key, List<AbyssOutpostData>> WORLD_TO_ABYSS_OUTPOST_OWNERS = new HashMap<>();

    protected final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();
    public PlayerData getOrCreateData(Player p){
        return PLAYER_DATA.computeIfAbsent(p.getUniqueId(), (e ->{
            PlayerData fromFile = loadDataFromFile(e);
            return fromFile == null ? new PlayerData() : fromFile;
        }));
    }

    public void saveDataToFile(UUID uuid, PlayerData data){
        DataFile file = getWorldFile(data != null && !data.isEmpty());
        if(file == null) return;
        FileObject playerDataObject;
        if(file.getElement("player_data") instanceof FileObject o) playerDataObject = o;
        else playerDataObject = new FileObject();
        playerDataObject.add(uuid.toString(), file.fileRegistry().serializeToFile(data));
        file.serialize("player_data", playerDataObject);
        file.save();
    }

    public PlayerData loadDataFromFile(UUID uuid){
        DataFile file = getWorldFile(false);
        if(file == null) return null;
        if(!(file.getElement("player_data") instanceof FileObject a)){
            file.close();
            return null;
        }
        file.close();
        if(!(a.get(uuid.toString()) instanceof FileElement ele)) return null;
        return file.fileRegistry().deserializeFromFile(PlayerData.class, ele);
    }

    public PlayerData getData(Player p){
        return PLAYER_DATA.get(p.getUniqueId());
    }
    public PlayerData removePlayerData(Player p){
        return PLAYER_DATA.remove(p.getUniqueId());
    }

    public static @Nullable AbyssWorld getOrCreate(@NotNull CruxPlugin plugin, @NotNull String worldName){
        return (AbyssWorld) CruxCore.inst().worldManager().getOrCreateWorld(AbyssWorldTypes.ABYSS, Key.key(worldName));
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
            List<AbyssOutpostData> abyssOwners = new ArrayList<>();
            module.getStored(stored -> stored.has(AbyssComponents.ABYSS_OUTPOST_DATA)).forEach(stored ->{
                AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
                Objects.requireNonNull(data);
                if(data.owner == null) return;
                abyssOwners.add(data);
            });
            if(abyssOwners.isEmpty()){
                WORLD_TO_ABYSS_OUTPOST_OWNERS.remove(key());
            }else{
                WORLD_TO_ABYSS_OUTPOST_OWNERS.put(key(), abyssOwners);
            }
        }

        DataFile file = getWorldFile(false);
        if(file != null) file.delete();

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
        /*Component c = MiniMessage.miniMessage().deserialize(
            "<gradient:#E62525:#6E1212>The world feels slightly less stable...</gradient>"
        );
        for(Player p : world.getPlayers()){
            p.sendMessage(c);
        }*/
        wave++;
        if(wave % 5 == 0){
            //difficulty = CruxMath.clamp(difficulty + .1f, .5f, 1.5f);
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
    public void onLoad() {
        super.onLoad();
        load();
    }

    @Override
    public void onUnload(boolean save) {
        super.onUnload(save);
        HandlerList.unregisterAll(this);
        if(save) save();
    }

    public DataFile getWorldFile(boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(
            CruxFolder.file(Crux.getMainPlugin(), "data/cruxabyss/world/" + CruxKey.toFileName(key()) + ".json"),
            createIfNeeded
        );
    }

    @Override
    public void save() {
        DataFile file = getWorldFile(!PLAYER_DATA.isEmpty());
        if(file == null) return;
        FileObject aPlayerData = new FileObject();
        PLAYER_DATA.forEach((uuid, data) ->{
            aPlayerData.add(uuid.toString(), file.fileRegistry().serializeToFile(data));
        });
        file.serialize("player_data", aPlayerData);
        file.save();
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        var data = removePlayerData(p);
        if(data==null) return;
        saveDataToFile(p.getUniqueId(), data);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssOutpostCapture(AbyssOutpostCaptureEvent event) {
        World world = event.getOutpost().getActive().getChunk().getWorld();
        if(!key().equals(world.key())) return;
        StoredStructure structure = event.getOutpost().getActive().getData();
        Player p = event.getPlayer();
        PlayerData data = getOrCreateData(p);
        if(data.hasClaimedOutpost(structure)) return;
        data.addClaimedOutpost(structure);
        CruxStatisticHolder holder = BukkitStatisticHolder.statisticHolder(p);
        if(holder != null) holder.incrementStatistic(AbyssStatistic.ABYSS_OUTPOSTS_CAPTURED, 1);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplode(CustomBlockExplodeEvent event) {
        if(!event.getBlock().getLocation().getWorld().equals(world)) return;
        switch (event.getResult()){
            case DESTROY, DESTROY_WITH_DECAY -> {}
            default -> { return; }
        }
        float yield = event.getYield();
        int blocksToFling = (int) (event.blockList().size() * yield);
        event.setYield(0f);
        Location center = event.getBlock().getLocation().toCenterLocation();
        int flung = 0;
        for(Block b : event.blockList()){
            if(flung >= blocksToFling) break;
            BlockData data = b.getBlockData().clone();
            Crux.handlers().block().setType(b, Material.AIR);
            flung++;
            Vector dir = b.getLocation().toCenterLocation().toVector().subtract(center.toVector());
            dir.setY(CruxMath.random(.8f, 1f));
            dir.multiply(CruxMath.random(.7f, 1f));
            center.getWorld().spawn(b.getLocation().toCenterLocation(), FallingBlock.class, falling ->{
                falling.setBlockData(data);
                falling.setVelocity(dir);
                if(CruxBlocksRegistries.BLOCK.getByBlockData(data) != null) falling.setCancelDrop(true);
            });
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(CustomEntityExplodeEvent event) {
        if (!event.getLocation().getWorld().equals(world)) return;
        switch (event.getResult()){
            case DESTROY, DESTROY_WITH_DECAY -> {}
            default -> { return; }
        }
        float yield = event.getYield();
        int blocksToFling = (int) (event.blockList().size() * yield);
        event.setYield(0f);
        Location center = event.getLocation().toCenterLocation();
        int flung = 0;
        for (Block b : event.blockList()) {
            if (flung >= blocksToFling) break;
            BlockData data = b.getBlockData().clone();
            Crux.handlers().block().setType(b, Material.AIR);
            flung++;
            Vector dir = b.getLocation().toCenterLocation().toVector().subtract(center.toVector());
            dir.setY(CruxMath.random(.8f, 1f));
            dir.multiply(CruxMath.random(.7f, 1f));

            center.getWorld().spawn(b.getLocation().toCenterLocation(), FallingBlock.class, falling -> {
                falling.setBlockData(data);
                falling.setVelocity(dir);
                if (CruxBlocksRegistries.BLOCK.getByBlockData(data) != null) falling.setCancelDrop(true);
            });
        }
    }

    public static class PlayerData{
        protected final Collection<BlockPos> claimedOutposts = new ArrayList<>();
        public boolean isEmpty(){
            return claimedOutposts.isEmpty();
        }
        public boolean hasClaimedOutpost(BlockPos pos){
            return claimedOutposts.contains(pos);
        }
        public void setClaimedOutposts(Collection<BlockPos> poses){
            claimedOutposts.clear();
            claimedOutposts.addAll(poses);
        }
        public boolean hasClaimedOutpost(StoredStructure structure){
            return hasClaimedOutpost(BlockPos.asBlock(structure.getPosition()));
        }
        public boolean addClaimedOutpost(BlockPos pos){
            return claimedOutposts.add(pos);
        }
        public boolean addClaimedOutpost(StoredStructure structure){
            return addClaimedOutpost(BlockPos.asBlock(structure.getPosition()));
        }

        public Collection<BlockPos> getClaimedOutposts() {
            return claimedOutposts;
        }
    }
}

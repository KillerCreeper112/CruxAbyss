package killercreepr.cruxabyss.game;

import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawner;
import killercreepr.cruxblocks.registeries.CruxBlockRegistry;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class GameManager implements Listener {
    private static final Map<UUID, GameManager> REGISTRY = new HashMap<>();
    public static @NotNull Map<UUID, GameManager> getRegistry(){ return REGISTRY; }
    public static @Nullable GameManager register(@NotNull GameManager manager){
        return REGISTRY.put(manager.getWorld().getUID(), manager);
    }

    public static @Nullable GameManager get(@NotNull UUID uuid){
        return REGISTRY.getOrDefault(uuid, null);
    }

    public static @Nullable GameManager get(@NotNull World world){
        return get(world.getUID());
    }

    public static @Nullable GameManager get(@NotNull Entity e){
        return get(e.getWorld());
    }

    public static @Nullable GameManager unregister(@NotNull GameManager manager){
        return REGISTRY.remove(manager.getWorld().getUID());
    }
    public static void clearRegistry(){
        REGISTRY.clear();
    }

    protected final @NotNull CruxPlugin plugin;
    private final World world;
    private final NaturalEntitySpawner naturalEntitySpawner = new NaturalEntitySpawner(this);
    private int wave = 1;
    private float difficulty = 1f;
    private boolean stop = true;
    public GameManager(@NotNull CruxPlugin plugin, @NotNull World world) {
        this.plugin = plugin;
        this.world = world;
    }

    public @NotNull World getWorld() {
        return world;
    }

    private int naturalSpawnTick = 0;
    private final Set<Location> recentlyChecked = new HashSet<>();
    private int daysPassed = 0;
    public void start(){
        if(!stop) return;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        stop = false;
        loadSavedData();

        new BukkitRunnable(){
            @Override
            public void run() {
                if(stop){
                    cancel();
                    return;
                }
                if(naturalSpawnTick != -1 && !world.getPlayers().isEmpty()){
                    naturalSpawnTick++;
                    if(naturalSpawnTick >= CruxMath.random(100, 200)){
                        naturalSpawnTick = 0;
                        if(naturalEntitySpawner.belowGlobalCap()){
                            recentlyChecked.clear();
                            plugin.log(Level.INFO, "Navigating natural mob spawns.");
                            for(Player p : world.getPlayers()){
                                if(p.getGameMode() == GameMode.SPECTATOR || nearChecked(p)) continue;
                                recentlyChecked.add(p.getLocation());
                                naturalEntitySpawner.navigate(p);
                            }
                        }
                    }
                }
                if(Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) && world.getTime() == 0){
                    dayEvent();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void loadSavedData(){
        /*todo GameConfig cfg = new GameConfig(world.getUID().toString());
        if(cfg.existedBefore()){
            this.wave = cfg.getInt(GameConfig.V.WAVE.path(), 1);
            this.difficulty = cfg.getFloat(GameConfig.V.DIFFICULTY.path(), 1f);
            this.daysPassed = cfg.getInt(GameConfig.V.DAYS_PASSED.path(), 0);
        }*/
    }

    public void naturalSpawnerChecked(@NotNull Player p){
        naturalSpawnTick = 0;
    }

    public boolean inGame(@NotNull Player p){
        return world.equals(p.getWorld());
    }

    private boolean nearChecked(@NotNull Player p){
        Location x = p.getLocation();
        double radius = naturalEntitySpawner.getRadius() * .6D;
        for(Location b : recentlyChecked){
            if(x.distanceSquared(b) < (radius*radius)) return true;
        }
        return false;
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

    public void start(@NotNull Set<Player> players){
        if(!stop) return;
        start();
        for(Player p : players){
            p.teleport(world.getSpawnLocation());
        }
    }

    public void stopAndSave(){
        stop();
        save();
    }

    public void save(){
        plugin.log(Level.INFO, "Saving game world '" + world.getName() + "'.");
        /*todo GameConfig cfg = new GameConfig(world.getUID().toString());
        cfg.json().addProperty(GameConfig.V.WAVE.path(), wave);
        cfg.json().addProperty(GameConfig.V.DIFFICULTY.path(), difficulty);
        cfg.json().addProperty(GameConfig.V.DAYS_PASSED.path(), daysPassed);
        cfg.save();*/
    }

    public void stop(){
        stop = true;
        HandlerList.unregisterAll(this);
    }

    public boolean isStopped(){ return stop; }

    public boolean join(@NotNull Player p){
        p.teleport(world.getSpawnLocation());
        //TeamUtility.setTeam(p, GrimTeam.IN_GAME);
        return true;
    }

    public int getWave() {
        return wave;
    }

    public float getDifficulty() {
        return difficulty;
    }

    @NotNull
    public CruxPlugin getPlugin() {
        return plugin;
    }

    public NaturalEntitySpawner getNaturalEntitySpawner() {
        return naturalEntitySpawner;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getNaturalSpawnTick() {
        return naturalSpawnTick;
    }

    public void setNaturalSpawnTick(int naturalSpawnTick) {
        this.naturalSpawnTick = naturalSpawnTick;
    }

    public Set<Location> getRecentlyChecked() {
        return recentlyChecked;
    }

    public int getDaysPassed() {
        return daysPassed;
    }

    public void setDaysPassed(int daysPassed) {
        this.daysPassed = daysPassed;
    }

    public int getLastMobAmount() {
        return lastMobAmount;
    }

    public void setLastMobAmount(int lastMobAmount) {
        this.lastMobAmount = lastMobAmount;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        //if(event.getFrom().equals(world)) TeamUtility.removeTeam(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTimeSkip(TimeSkipEvent event) {
        if(!event.getWorld().equals(world)) return;
        Bukkit.broadcast(Component.text("Time skip: " +  event.getSkipAmount()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if(!event.getBlock().getLocation().getWorld().equals(world)) return;
        float yield = event.getYield();
        int blocksToFling = (int) (event.blockList().size() * yield);
        event.setYield(0f);
        Location center = event.getBlock().getLocation().toCenterLocation();
        int flung = 0;
        for(Block b : event.blockList()){
            if(flung < blocksToFling){
                Vector dir = b.getLocation().toCenterLocation().toVector().subtract(center.toVector());
                dir.setY(CruxMath.random(.8f, 1f));
                dir.multiply(CruxMath.random(.7f, 1f));
                FallingBlock falling = center.getWorld().spawnFallingBlock(b.getLocation().toCenterLocation(),
                        b.getBlockData());
                falling.setVelocity(dir);
                if(CruxBlocksRegistries.BLOCKS.getByBlockData(b.getBlockData()) != null) falling.setCancelDrop(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!event.getLocation().getWorld().equals(world)) return;
        float yield = event.getYield();
        int blocksToFling = (int) (event.blockList().size() * yield);
        event.setYield(0f);
        Location center = event.getLocation().toCenterLocation();
        int flung = 0;
        for(Block b : event.blockList()){
            if(flung < blocksToFling){
                Vector dir = b.getLocation().toCenterLocation().toVector().subtract(center.toVector());
                dir.setY(CruxMath.random(.8f, 1f));
                dir.multiply(CruxMath.random(.7f, 1f));
                FallingBlock falling = center.getWorld().spawnFallingBlock(b.getLocation().toCenterLocation(),
                        b.getBlockData());
                falling.setVelocity(dir);
                if(CruxBlocksRegistries.BLOCKS.getByBlockData(b.getBlockData()) != null) falling.setCancelDrop(true);
            }
        }
    }

    private int lastMobAmount;
    private long lastChecked;
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(!event.getEntity().getWorld().equals(world)) return;
        if(System.currentTimeMillis() > lastChecked){
            lastChecked = System.currentTimeMillis() + (50L*20);
            lastMobAmount = naturalEntitySpawner.getNaturalSpawnedMobs();
        }
        if(!naturalEntitySpawner.belowGlobalCap(lastMobAmount)){
            event.setCancelled(true);
            return;
        }
        CruxTag.set(event.getEntity(), "spawn_reason", PersistentDataType.STRING, event.getSpawnReason().toString().toLowerCase());
        /*if(TeamUtility.getTeamCacheRaw(event.getEntity()) == null){
            TeamUtility.setTeam(event.getEntity(), GrimTeam.MOB);
            GrimTag.set(event.getEntity(), "spawn_reason", PersistentDataType.STRING, event.getSpawnReason().toString().toLowerCase());
        }
        if(GrimTag.has(event.getEntity(), "entity")) return;
        event.setCancelled(true);
        GrimEntity spawner = GrimEntity.getOrBuild(event.getEntity());
        spawner.spawn(this, event.getLocation());*/
    }
}

package killercreepr.cruxabyss.game;

import killercreepr.crux.Crux;
import killercreepr.crux.game.GenericStatus;
import killercreepr.crux.game.Statutable;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.persistence.AbyssPersist;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawner;
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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class GameManager implements Statutable, Listener {
    protected final @NotNull CruxPlugin plugin;
    protected final World world;
    protected final NaturalEntitySpawner naturalEntitySpawner = new NaturalEntitySpawner(this);
    protected int wave = 1;
    protected float difficulty = 1f;
    protected GenericStatus state = GenericStatus.IDLE;

    protected int naturalSpawnTick = 0;
    protected final Set<Location> recentlyCheckedMobSpawns = new HashSet<>();
    protected int daysPassed = 0;

    protected int lastMobAmount;
    protected long lastCheckedMobAmount;
    public GameManager(@NotNull CruxPlugin plugin, @NotNull World world) {
        this.plugin = plugin;
        this.world = world;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public void naturalSpawnTick(){
        if(naturalSpawnTick < 0 || world.getPlayers().isEmpty()) return;
        naturalSpawnTick++;
        if(naturalSpawnTick < CruxMath.random(100, 200)) return;
        naturalSpawnTick = 0;
        naturalEntitySpawner.belowGlobalCapMainThread().whenComplete((value, throwable) ->{
            if(throwable !=  null) Crux.log(Level.WARNING, throwable.getMessage());
            if(!value) return;

            recentlyCheckedMobSpawns.clear();
            plugin.log(Level.INFO, "Navigating natural mob spawns.");
            for(Player p : world.getPlayers()){
                if(p.getGameMode() == GameMode.SPECTATOR || nearChecked(p)) continue;
                recentlyCheckedMobSpawns.add(p.getLocation());
                naturalEntitySpawner.navigate(p);
            }
        });
                /*if(naturalEntitySpawner.belowGlobalCap()){
                    recentlyCheckedMobSpawns.clear();
                    plugin.log(Level.INFO, "Navigating natural mob spawns.");
                    for(Player p : world.getPlayers()){
                        if(p.getGameMode() == GameMode.SPECTATOR || nearChecked(p)) continue;
                        recentlyCheckedMobSpawns.add(p.getLocation());
                        naturalEntitySpawner.navigate(p);
                    }
                }*/
    }

    public void tick(){
        naturalSpawnTick();
        if(Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) && world.getTime() == 0){
            dayEvent();
        }
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
        for(Location b : recentlyCheckedMobSpawns){
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

    public void stopAndSave(){
        setStopped();
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


    @Override
    public @NotNull GenericStatus getStatus() {
        return state;
    }

    @Override
    public void setStatus(@NotNull GenericStatus state) {
        if(this.state == state) return;
        this.state = state;

        switch (getStatus()){
            case STARTED ->{
                Bukkit.getPluginManager().registerEvents(this, plugin);
                loadSavedData();

                buildRunnable().runTaskTimerAsynchronously(plugin, 0L, 1L);
            }
            case STOPPED, IDLE -> {
                HandlerList.unregisterAll(this);
            }
        }
    }

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

    public int getNaturalSpawnTick() {
        return naturalSpawnTick;
    }

    public void setNaturalSpawnTick(int naturalSpawnTick) {
        this.naturalSpawnTick = naturalSpawnTick;
    }

    public Set<Location> getRecentlyCheckedMobSpawns() {
        return recentlyCheckedMobSpawns;
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

    public long getLastCheckedMobAmount() {
        return lastCheckedMobAmount;
    }

    public void setLastCheckedMobAmount(long lastCheckedMobAmount) {
        this.lastCheckedMobAmount = lastCheckedMobAmount;
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

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity e = event.getEntity();
        if(!e.getWorld().equals(world)) return;
        if(System.currentTimeMillis() > lastCheckedMobAmount){
            lastCheckedMobAmount = System.currentTimeMillis() + (50L*20);
            lastMobAmount = naturalEntitySpawner.getNaturalSpawnedMobs();
        }
        if(!naturalEntitySpawner.belowGlobalCap(lastMobAmount)){
            event.setCancelled(true);
            return;
        }
        AbyssPersist.SPAWN_REASON.set(e, event.getSpawnReason().toString().toLowerCase());
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

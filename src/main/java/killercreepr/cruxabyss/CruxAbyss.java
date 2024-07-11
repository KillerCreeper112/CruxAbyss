package killercreepr.cruxabyss;

import killercreepr.crux.Crux;
import killercreepr.crux.data.BlockPos;
import killercreepr.crux.data.StoredChunk;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.cruxabyss.config.handler.FileAbyssOutpost;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxabyss.structure.AbyssOutpost;
import killercreepr.cruxabyss.world.WorldManager;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.generation.GenerationListener;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxstructures.event.StructurePlaceEvent;
import killercreepr.cruxstructures.manager.StructureManager;
import killercreepr.cruxstructures.registries.StructureRegistries;
import killercreepr.cruxstructures.structure.Structure;
import killercreepr.cruxstructures.structure.impl.FAWEStructure;
import killercreepr.cruxstructures.structure.stored.SimpleStoredStructure;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CruxAbyss extends CruxPlugin implements Listener {
    private static CruxAbyss instance;
    public static CruxAbyss inst(){ return instance; }

    protected final StructureManager structureManager = new StructureManager(this);
    @Override
    public void enabled() {
        instance = this;
        BiomeManager.register();
        registerListeners(
            new GenerationListener(),
            this,
            structureManager
        );
        CfgRegistries.JSON.registerHandler(AbyssOutpost.class, new FileAbyssOutpost());

        getServer().getScheduler().runTaskLater(this, task ->{
            game = createNewGame();
            game.setStarted();
        }, 100L);
        super.enabled();
        StructureRegistries.STRUCTURES.register(new FAWEStructure(Crux.key("abyss_outpost"), "abyss_outpost"){
            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public @Nullable StoredStructure buildStored(@NotNull Location center) {
                Bukkit.broadcastMessage("buildStored");
                return new AbyssOutpost(this, StoredChunk.from(center), BlockPos.from(center));
            }
        });
        structureManager.buildRunnable().runTaskTimer(this, 20L, 1L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        Structure structure = StructureRegistries.STRUCTURES.get(Crux.key("abyss_outpost"));
        structure.place(p.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructurePlace(StructurePlaceEvent event) {
        Location l = event.getLocation();
        Bukkit.broadcast(Component.text("Structure spawned " + l.getX() + ", " + l.getY() + ", " + l.getZ()));
    }

    protected GameManager game;
    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        p.teleport(game.getWorld().getSpawnLocation());
    }

    public @Nullable GameManager createNewGame(){
        World gameWorld = WorldManager.getOrCreateGrimWorld("game_world_1");
        if(gameWorld == null) return null;
        gameWorld.getWorldBorder().setCenter(0, 0);
        gameWorld.getWorldBorder().setSize(1000D);
        GameManager game = new GameManager(this, gameWorld);
        return game;
    }

    @Override
    public void disabled() {
        super.disabled();
        structureManager.saveAllWorlds();
    }

    @Override
    public void reload() {
        super.reload();
        structureManager.loadConfiguration();
    }
}

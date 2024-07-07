package killercreepr.cruxabyss;

import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxabyss.world.WorldManager;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.generation.GenerationListener;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.Nullable;

public class CruxAbyss extends CruxPlugin implements Listener {
    private static CruxAbyss instance;
    public static CruxAbyss inst(){ return instance; }

    @Override
    public void enabled() {
        instance = this;
        BiomeManager.register();
        getServer().getPluginManager().registerEvents(new GenerationListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

        game = createNewGame();
        game.start();
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
    }

    @Override
    public void reload() {
        super.reload();
    }
}

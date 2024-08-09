package killercreepr.cruxabyss;

import killercreepr.crux.Crux;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.config.handler.FileAbyssOutpost;
import killercreepr.cruxabyss.config.handler.FileTestStructure;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxabyss.item.AbyssItems;
import killercreepr.cruxabyss.listener.AbyssAltarPortalListener;
import killercreepr.cruxabyss.structure.AbyssOutpost;
import killercreepr.cruxabyss.structure.StoredAbyssOutpost;
import killercreepr.cruxabyss.structure.StoredTestStructure;
import killercreepr.cruxabyss.structure.TestStructure;
import killercreepr.cruxabyss.world.WorldManager;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.entity.NaturalMobContainer;
import killercreepr.cruxabyss.world.entity.NaturalMobSettings;
import killercreepr.cruxabyss.world.generation.GenerationListener;
import killercreepr.cruxconfig.config.common.yaml.context.YamlContext;
import killercreepr.cruxconfig.config.common.yaml.element.YamlElement;
import killercreepr.cruxconfig.config.common.yaml.element.YamlObject;
import killercreepr.cruxconfig.config.common.yaml.parsed.CfgParsedObjectHandler;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxstructures.event.StructurePlaceEvent;
import killercreepr.cruxstructures.structure.impl.CfgFAWEStructure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CruxAbyss extends CruxPlugin implements Listener {
    private static CruxAbyss instance;
    public static CruxAbyss inst(){ return instance; }

    @Override
    public void onLoad() {
        super.onLoad();
        CfgRegistries.JSON.registerHandler(StoredAbyssOutpost.class, new FileAbyssOutpost());
        CfgRegistries.JSON.registerHandler(StoredTestStructure.class, new FileTestStructure());
        CfgRegistries.YAML.PARSED_OBJECT_HANDLERS.register(new CfgParsedObjectHandler<CfgFAWEStructure>() {
            @Override
            public int getPriority() {
                return 0;
            }

            @Override
            public @NotNull Class<CfgFAWEStructure> getTargetType() {
                return CfgFAWEStructure.class;
            }

            @Override
            public @Nullable CfgFAWEStructure parse(@NotNull YamlElement e, @NotNull YamlContext ctx,
                                                    @NotNull CfgFAWEStructure base,
                                                    @Nullable CfgFAWEStructure current) {
                if(!(e instanceof YamlObject o) || current == null) return current;
                String type = ctx.getRegistry().deserialize(String.class, o.get("type"));
                if(type==null) return current;
                switch (type.toLowerCase()){
                    case "test" ->{
                        return new TestStructure(current.key(), current.getHolder(), current.isPersistent());
                    }
                    case "abyss_outpost" ->{
                        return new AbyssOutpost(current.key(), current.getHolder(), current.isPersistent());
                    }
                }
                return current;
            }

            @Override
            public @NotNull Key key() {
                return Crux.key("abyss_structures");
            }
        });
    }

    @Override
    public void enabled() {
        instance = this;
        BiomeManager.register();
        registerListeners(
            new GenerationListener(),
            this,
            new AbyssAltarPortalListener()
        );
        AbyssBlocks.register();
        AbyssItems.register();

        getServer().getScheduler().runTaskLater(this, task ->{
            game = createNewGame();
            game.setStarted();
        }, 100L);
        super.enabled();
        NaturalMobSettings.register();
        NaturalMobContainer.register();
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructurePlace(StructurePlaceEvent event) {
        Location l = event.getLocation();
        Bukkit.broadcast(Component.text("Structure spawned " + l.getX() + ", " + l.getY() + ", " + l.getZ()));
    }

    public GameManager game;
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

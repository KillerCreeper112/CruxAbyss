package killercreepr.cruxabyss;

import io.papermc.paper.registry.event.RegistryEvents;
import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.*;
import killercreepr.crux.module.StandardModules;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.command.AbyssCommands;
import killercreepr.cruxabyss.config.Config;
import killercreepr.cruxabyss.config.handler.FileAbyssOutpost;
import killercreepr.cruxabyss.config.handler.FileAbyssSafezone;
import killercreepr.cruxabyss.config.handler.FileTestStructure;
import killercreepr.cruxabyss.config.handler.component.CfgAbyssComponents;
import killercreepr.cruxabyss.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.item.AbyssItems;
import killercreepr.cruxabyss.lang.Lang;
import killercreepr.cruxabyss.listener.AbyssAltarPortalListener;
import killercreepr.cruxabyss.listener.AbyssSafezoneListener;
import killercreepr.cruxabyss.listener.AbyssWoodFunctionListener;
import killercreepr.cruxabyss.listener.DisableElytraListener;
import killercreepr.cruxabyss.registries.AbyssRegistries;
import killercreepr.cruxabyss.structure.*;
import killercreepr.cruxabyss.values.DefaultValues;
import killercreepr.cruxabyss.values.ValuesProvider;
import killercreepr.cruxabyss.world.abyss.AbyssWorld;
import killercreepr.cruxabyss.world.abyss.entity.StandardAbyssGroups;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.base.parsed.FileParsedObjectHandler;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.structure.impl.CfgFAWEStructure;
import killercreepr.cruxworlds.world.manager.CruxWorldManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CruxAbyss extends CruxPlugin implements Listener, LangProvider {
    private static CruxAbyss instance;
    public static CruxAbyss inst(){ return instance; }

    protected ValuesProvider values;

    public ValuesProvider values() {
        return values;
    }

    public void values(@NotNull ValuesProvider values) {
        this.values = values;
    }

    protected LangProvider langProvider;
    @Override
    public void onLoad() {
        super.onLoad();
        AbyssMobCategory.register();
        CfgAbyssComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());
        new AbyssCommands(this).register();
        CfgRegistries.JSON_REGISTRY.forEach(registry ->{
            registry.registerFileHandler(StoredAbyssOutpost.class, new FileAbyssOutpost());
            registry.registerFileHandler(StoredAbyssSafezone.class, new FileAbyssSafezone());
            registry.registerFileHandler(StoredTestStructure.class, new FileTestStructure());
        });

        CfgRegistries.SIMPLE_REGISTRY.forEach(registry -> {
            registry.getParsedObjectRegistry().register(new FileParsedObjectHandler<CfgFAWEStructure>() {
                @Override
                public int getPriority() {
                    return 0;
                }

                @Override
                public @NotNull Class<CfgFAWEStructure> getTargetType() {
                    return CfgFAWEStructure.class;
                }

                @Override
                public @Nullable CfgFAWEStructure parse(@NotNull FileElement e, @NotNull FileContext<?> ctx,
                                                        @NotNull CfgFAWEStructure base,
                                                        @Nullable CfgFAWEStructure current) {
                    if(!(e instanceof FileObject o) || current == null) return current;
                    FileRegistry registry = ctx.getRegistry();
                    String type = registry.deserializeFromFile(String.class, o.get("type"));
                    if(type==null) return current;
                    switch (type.toLowerCase()){
                        case "test" ->{
                            return new TestStructure(current.key(), current.getHolder(), current.isPersistent(), current.getBeforePlacementModules(), current.getModules());
                        }
                        case "abyss_outpost" ->{
                            return new AbyssOutpost(current.key(), current.getHolder(),
                                current.isPersistent(), current.getBeforePlacementModules(), current.getModules());
                        }
                        case "abyss_safezone" ->{
                            Vector expand = registry.deserializeFromFile(Vector.class, o.get("outer_box_expansion"));
                            if(expand == null) expand = new Vector();
                            return new AbyssSafezone(current.key(), current.getHolder(), current.isPersistent(),
                                current.getBeforePlacementModules(), current.getModules(), expand);
                        }
                    }
                    return current;
                }

                @Override
                public @NotNull Key key() {
                    return Crux.key("abyss_structures");
                }
            });
        });

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register("world_abyss", AbyssWorld::new);
    }

    @Override
    public void enabled() {
        instance = this;
        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            values(new Config(this, "config"));
        }else{
            values(new DefaultValues());
        }
        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            langProvider = new SimpleLangConfig(this, "lang", this::lang, Lang.class);
        }else{
            langProvider = this;
            LangPopulator.populate(lang, Msg.class);
        }
        BiomeManager.register();
        registerListeners(
            this,
            new AbyssAltarPortalListener(),
            new DisableElytraListener(),
            new AbyssWoodFunctionListener(),
            new AbyssSafezoneListener(this, CruxCore.inst().structureManager())
        );
        AbyssBlocks.register();
        AbyssItems.register();

        super.enabled();
        StandardAbyssGroups.register(AbyssRegistries.ABYSS_NATURAL_ENTITY_SPAWN_GROUP);
    }

    @Override
    public void disabled() {
        super.disabled();
    }

    @Override
    public void reload() {
        super.reload();
        values.reload(this);
        langProvider.reload(this);
    }

    protected final CreateLang lang = Lang.setLang(new SimpleCreateLang());
    @NotNull
    public CreateLang lang() {
        return lang;
    }
}

package killercreepr.cruxabyss.core;

import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.communication.lang.LangPopulator;
import killercreepr.crux.core.communication.lang.Msg;
import killercreepr.crux.core.communication.lang.SimpleCreateLang;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.plugin.module.StandardModules;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.command.AbyssCommands;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.config.Config;
import killercreepr.cruxabyss.core.config.handler.component.CfgAbyssComponents;
import killercreepr.cruxabyss.core.data.entity.AbyssHolder;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.item.AbyssItems;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.listener.*;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.structure.generation.AbyssOutpostSetLocationList;
import killercreepr.cruxabyss.core.values.DefaultValues;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxabyss.core.world.abyss.entity.StandardAbyssGroups;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.base.parsed.FileParsedObjectHandler;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.core.CruxStructuresModule;
import killercreepr.cruxstructures.core.config.FileCfgStructureGen;
import killercreepr.cruxstructures.core.config.FileInstantLocationSetListStructureGen;
import killercreepr.cruxstructures.core.structure.CfgFAWEStructure;
import killercreepr.cruxstructures.core.structure.generation.InstantLocationSetListStructureGen;
import killercreepr.cruxstructures.core.structure.generation.LocationSetListStructureGen;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Listener;
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
        AbyssMob.register();
        AbyssComponents.register();
        AbyssMobCategory.register();
        CfgAbyssComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());
        registerCruxStructure();
        new AbyssCommands(this).register();
        CfgRegistries.JSON_REGISTRY.forEach(registry ->{
            /*registry.registerFileHandler(StoredAbyssOutpost.class, new FileAbyssOutpost());
            registry.registerFileHandler(StoredAbyssSafezone.class, new FileAbyssSafezone());*/
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
                       /* case "test" ->{
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
                        case "abyss_outpost_loot_holder" ->{
                            return new SimpleLootHolderStructure(
                                current.key(), current.getHolder(), current.isPersistent(), current.getBeforePlacementModules(),
                                current.getModules()
                            );
                        }*/
                    }
                    return current;
                }

                @Override
                public @NotNull Key key() {
                    return Crux.key("abyss_structures");
                }
            });
        });

        /*FileStructureModule reg = CruxRegistries.MODULES.getModule(CruxStructuresModule.class).getFileStructureModule();
        reg.typeHandlers().register("abyss_outpost", new PureYamlFileHandler<AbyssOutpost>(){
            @Nullable
            @Override
            public AbyssOutpost deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileElement fileElement) {
                return new AbyssOutpost();
            }
        });*/

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register("world_abyss", AbyssWorld::new);
        worldManager.getWorldTypeRegistry().register(AbyssWorldTypes.ABYSS);

        EntityMemory.registerFunction(this, (mem) ->{
            if(!(mem instanceof PlayerMemory m)) return;
            m.getDataHolders().register(new AbyssHolder(m, Crux.getMainPlugin()));
        });
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
        registerListeners(
            this,
            new AbyssAltarPortalListener(),
            new AbyssSpecificsListener(values),
            new AbyssWoodFunctionListener(),
            new AbyssSafezoneListener(this),
            new AbyssTravelTrackingListener(values),
            new AbyssalMobsListener(),
            new CustomProjectileListener()
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

    public void registerCruxStructure(){
        CruxStructuresModule module = CruxCore.core().cruxStructures();
        FileCfgStructureGen fileCfgStructureGen = module.getFileCfgStructureGen();
        fileCfgStructureGen.typeHandlers().register("abyss_world/instant_set_location_list", new FileInstantLocationSetListStructureGen(){
            @Override
            public @Nullable LocationSetListStructureGen deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
                InstantLocationSetListStructureGen gen = (InstantLocationSetListStructureGen) super.deserializeFromFile(ctx, e);
                if(gen == null) return null;
                return new AbyssOutpostSetLocationList(
                    gen.getStructurePool(), gen.getChunkRangeX(), gen.getChunkRangeZ(), gen.getMinDistanceApart(), gen.getId()
                );
            }
        });
    }
}

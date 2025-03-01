package killercreepr.cruxabyss.core;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.loot.LootPool;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.communication.lang.LangPopulator;
import killercreepr.crux.core.communication.lang.Msg;
import killercreepr.crux.core.communication.lang.SimpleCreateLang;
import killercreepr.crux.core.loot.SimpleLootTable;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.plugin.module.StandardModules;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxabyss.api.loot.MobWaveGroupLootTable;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.advancement.objective.AbyssOutpostCaptureObjective;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.command.AbyssCommands;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.config.Config;
import killercreepr.cruxabyss.core.config.WorldEventConfigs;
import killercreepr.cruxabyss.core.config.handler.component.CfgAbyssComponents;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.tickable.AbyssTickables;
import killercreepr.cruxabyss.core.game.entity.MobWave;
import killercreepr.cruxabyss.core.game.entity.MobWaveGroup;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.listener.*;
import killercreepr.cruxabyss.core.loot.SimpleMobWaveGroupLootTable;
import killercreepr.cruxabyss.core.loot.condition.AbyssOutpostCaptureCondition;
import killercreepr.cruxabyss.core.menu.action.AbyssOutpostUpgradeAction;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.statistic.AbyssStatistic;
import killercreepr.cruxabyss.core.structure.generation.AbyssOutpostSetLocationList;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssOutpostUpgrades;
import killercreepr.cruxabyss.core.text.tags.object.ActiveAbyssOutpostTags;
import killercreepr.cruxabyss.core.text.tags.object.StoredAbyssOutpostTags;
import killercreepr.cruxabyss.core.values.DefaultValues;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxabyss.core.world.abyss.entity.StandardAbyssGroups;
import killercreepr.cruxabyss.core.world.module.SimpleWorldEventsModule;
import killercreepr.cruxadvancements.api.advancement.objective.AdvancementObjective;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.config.CruxAdvanceCfgData;
import killercreepr.cruxadvancements.core.config.handler.FileAdvancementObjective;
import killercreepr.cruxadvancements.core.config.handler.FileSimpleAdvanceObjective;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileLootCondition;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileSimpleLootPool;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileSimpleLootTable;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.SimpleFileLootCondition;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxmenus.api.menu.config.handler.FileMenuHolder;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxstructures.core.CruxStructuresModule;
import killercreepr.cruxstructures.core.config.FileCfgStructureGen;
import killercreepr.cruxstructures.core.config.FileInstantLocationSetListStructureGen;
import killercreepr.cruxstructures.core.structure.generation.InstantLocationSetListStructureGen;
import killercreepr.cruxstructures.core.structure.generation.LocationSetListStructureGen;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.creator.CruxWorldModuleCreator;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.module.WorldModule;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    protected WorldEventConfigs worldEventCfgs;
    public WorldEventConfigs worldEventCfgs(){
        return worldEventCfgs;
    }

    protected LangProvider langProvider;
    @Override
    public void onLoad() {
        super.onLoad();
        AbyssMob.register();
        AbyssComponents.register();
        AbyssMobCategory.register();
        AbyssStatistic.register();
        CfgAbyssComponents.register(BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers());
        registerCruxStructure();
        AbyssTickables.register();
        new AbyssCommands(this).register();
        CfgRegistries.SIMPLE_REGISTRY.forEach(reg ->{
            reg.registerFileHandler(
                new AutoFileHandler<>(MobWave.class, AutoFileOptions.builder()
                    .addTypeToken("one_time_spawns", new TypeToken<List<NaturalEntitySpawnGroup>>() {})
                    .build()),
                new AutoFileHandler<>(MobWaveGroup.class, AutoFileOptions.builder()
                    .addTypeToken("waves", new TypeToken<List<MobWave>>() {})
                    .build())
            );
            reg.registerFileHandler(
                MobWaveGroupLootTable.class, new FileSimpleLootTable<>(MobWaveGroup.class, new FileSimpleLootPool<>(MobWaveGroup.class)){
                    @Nullable
                    @Override
                    public SimpleLootTable<MobWaveGroup> createLootTable(@NotNull Key key, @NotNull NumberProvider rolls, @NotNull List<LootPool<MobWaveGroup>> lootPools) {
                        return new SimpleMobWaveGroupLootTable(key, rolls, lootPools);
                    }
                }
            );
        });
        /*CfgRegistries.JSON_REGISTRY.forEach(registry ->{
            registry.registerFileHandler(StoredAbyssOutpost.class, new FileAbyssOutpost());
            registry.registerFileHandler(StoredAbyssSafezone.class, new FileAbyssSafezone());
        });*/

        /*CfgRegistries.SIMPLE_REGISTRY.forEach(registry -> {
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
                       *//* case "test" ->{
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
                        }*//*
                    }
                    return current;
                }

                @Override
                public @NotNull Key key() {
                    return Crux.key("abyss_structures");
                }
            });
        });*/
        registerLootConditions(BukkitCfgHandlers.LOOT_CONDITION);
        registerObjectives(CruxAdvanceCfgData.fileAdvancementObjective());

        /*FileStructureModule reg = CruxRegistries.MODULES.getModule(CruxStructuresModule.class).getFileStructureModule();
        reg.typeHandlers().register("abyss_outpost", new PureYamlFileHandler<AbyssOutpost>(){
            @Nullable
            @Override
            public AbyssOutpost deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileElement fileElement) {
                return new AbyssOutpost();
            }
        });*/

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register(Key.key("world_abyss"), AbyssWorld::new);
        worldManager.getWorldTypeRegistry().register(AbyssWorldTypes.ABYSS);
        worldManager.getModuleCreatorRegistry().register(Key.key("world"), new CruxWorldModuleCreator() {
            @Override
            public @NotNull WorldModule create(@NotNull CruxWorld cruxWorld) {
                return new SimpleWorldEventsModule(cruxWorld);
            }
        });

        CruxMenusModule menus = CruxCore.core().cruxMenus();
        menus.menuRegistry().menuActions().register(new AbyssOutpostUpgradeAction(Crux.key("abyss_outpost_upgrade")));
        registerTextTags(Crux.tags());

        registerMenuModules(
            menus.menuRegistry().menuModule(),
            menus.menuModuleRegistry()
        );
    }

    @Override
    public void enabled() {
        instance = this;
        if(CruxRegistries.MODULES.containsKey(StandardModules.CRUX_CONFIGS)){
            values(new Config(this, "config"));
            langProvider = new SimpleLangConfig(this, "lang", this::lang, Lang.class);
            worldEventCfgs = new WorldEventConfigs(this);
        }else{
            values(new DefaultValues());
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
            new AbyssalListener(),
            new CustomProjectileListener(),
            new ObjectiveListener(),
            new HostileMobListener()
        );
        AbyssBlocks.register();
        AbyssOutpostUpgrades.register();

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
        worldEventCfgs.reload();

        CruxCore.inst().cruxMenus().menuRegistry().loadConfiguration(
            new CruxFolder(this, "menus").file()
        );
    }

    protected final CreateLang lang = Lang.setLang(new SimpleCreateLang());
    @NotNull
    public CreateLang lang() {
        return lang;
    }

    public void registerTextTags(TagParser tags){
        tags.register(List.of(
            new ActiveAbyssOutpostTags(),
            new StoredAbyssOutpostTags()
        ));
    }

    public void registerMenuModules(@NotNull FileMenuHolder<?> fileMenuHolder, @NotNull KeyedRegistry<MenuModuleBuilder> registry) {
    }

    public void registerObjectives(FileAdvancementObjective file){
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("abyss_outpost_capture")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new AbyssOutpostCaptureObjective(data, maxProgress);
            }
        });
    }

    public void registerLootConditions(FileLootCondition file){
        file.registerCustomHandler(new SimpleFileLootCondition<>(key("abyss_outpost_capture")) {
            @Override
            public @Nullable LootCondition deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull String target) {
                return new AbyssOutpostCaptureCondition(target);
            }
        });
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

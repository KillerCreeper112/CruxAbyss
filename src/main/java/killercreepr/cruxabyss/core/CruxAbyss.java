package killercreepr.cruxabyss.core;

import com.google.common.reflect.TypeToken;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import io.papermc.paper.world.PaperWorldLoader;
import io.papermc.paper.world.migration.WorldFolderMigration;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.api.communication.lang.LangProvider;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.LootPool;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.loot.conditions.LootCondition;
import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.communication.lang.LangPopulator;
import killercreepr.crux.core.communication.lang.Msg;
import killercreepr.crux.core.communication.lang.SimpleCreateLang;
import killercreepr.crux.core.entity.tag.BaseEntityTag;
import killercreepr.crux.core.loot.SimpleLootTable;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.plugin.module.StandardModules;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxWorldUtil;
import killercreepr.cruxabyss.api.loot.MobWaveGroupLootTable;
import killercreepr.cruxabyss.api.structure.outpost.AbyssOutpostManager;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.advancement.objective.*;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.challenge.AbyssChallengeManager;
import killercreepr.cruxabyss.core.challenge.ChallengeRoll;
import killercreepr.cruxabyss.core.command.AbyssCommands;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.config.Config;
import killercreepr.cruxabyss.core.config.WorldEventConfigs;
import killercreepr.cruxabyss.core.config.handler.component.CfgAbyssComponents;
import killercreepr.cruxabyss.core.config.loader.ChallengeRollLootTableLoader;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.tickable.AbyssTickables;
import killercreepr.cruxabyss.core.game.entity.MobWave;
import killercreepr.cruxabyss.core.game.entity.MobWaveGroup;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.listener.*;
import killercreepr.cruxabyss.core.loot.SimpleMobWaveGroupLootTable;
import killercreepr.cruxabyss.core.loot.condition.AbyssOutpostCaptureCondition;
import killercreepr.cruxabyss.core.menu.AbyssOutpostCraftingMenuHolder;
import killercreepr.cruxabyss.core.menu.AbyssOutpostCraftingRecipeListHolder;
import killercreepr.cruxabyss.core.menu.action.AbyssOutpostLockerOpenAction;
import killercreepr.cruxabyss.core.menu.action.AbyssOutpostQuestLineAction;
import killercreepr.cruxabyss.core.menu.action.AbyssOutpostUpgradeAction;
import killercreepr.cruxabyss.core.menu.action.AbyssRecallAnchorTeleportAction;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.statistic.AbyssStatistic;
import killercreepr.cruxabyss.core.structure.generation.AbyssOutpostSetLocationList;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.SimpleAbyssOutpostManager;
import killercreepr.cruxabyss.core.structure.outpost.questline.AbyssQuestLineLoader;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssOutpostUpgrades;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssRecallAnchor;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveAbyssalRecallUpgrade;
import killercreepr.cruxabyss.core.text.tags.object.AbyssPlayerTags;
import killercreepr.cruxabyss.core.text.tags.object.AbyssalRecallAnchorTags;
import killercreepr.cruxabyss.core.text.tags.object.ActiveAbyssOutpostTags;
import killercreepr.cruxabyss.core.text.tags.object.StoredAbyssOutpostTags;
import killercreepr.cruxabyss.core.values.DefaultValues;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxabyss.core.world.abyss.entity.StandardAbyssGroups;
import killercreepr.cruxabyss.core.world.abyss.generation.AbyssGeneration;
import killercreepr.cruxadvancements.api.advancement.objective.AdvancementObjective;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.config.CruxAdvanceCfgData;
import killercreepr.cruxadvancements.core.config.handler.FileAdvancementObjective;
import killercreepr.cruxadvancements.core.config.handler.FileSimpleAdvanceObjective;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileLootCondition;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileSimpleLootPool;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileSimpleLootTable;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.SimpleFileLootCondition;
import killercreepr.cruxconfig.config.bukkit.loader.KeyLootTableLoader;
import killercreepr.cruxconfig.config.bukkit.standard.SimpleLangConfig;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcrafting.api.crafting.CruxCraftingRecipeManager;
import killercreepr.cruxcrafting.core.config.CruxCraftingCfg;
import killercreepr.cruxcrafting.core.config.loader.CruxCraftingRecipeLoader;
import killercreepr.cruxcrafting.core.crafting.SimpleCraftingRecipeManager;
import killercreepr.cruxcrafting.core.registries.CruxCraftingRegistries;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxmenus.CruxMenusModule;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.config.handler.FileMenuHolder;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.api.menu.module.config.MenuModuleBuilder;
import killercreepr.cruxmenus.core.menu.module.standard.SimpleFilePagedCfg;
import killercreepr.cruxmenus.core.menu.module.standard.SimplePagedMenuModule;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
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
import killercreepr.cruxworlds.core.world.module.SimpleWorldEventsModule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

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

    protected final CruxCraftingRecipeManager craftingManager = CruxCraftingRegistries.RECIPE_MANAGER.register(
        new SimpleCraftingRecipeManager(Crux.key("abyss/outpost"))
    );

    public CruxCraftingRecipeManager getCraftingManager() {
        return craftingManager;
    }

    protected AbyssOutpostManager abyssOutpostManager = new SimpleAbyssOutpostManager();

    public AbyssOutpostManager getAbyssOutpostManager() {
        return abyssOutpostManager;
    }

    public void setAbyssOutpostManager(AbyssOutpostManager abyssOutpostManager) {
        this.abyssOutpostManager = abyssOutpostManager;
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

        AbyssQuestLineLoader.load();

        CruxRegistries.ENTITY_TAG.register(new BaseEntityTag(Crux.key("abyssal")) {
            @Override
            public boolean isTagged(@NotNull Entity entity) {
                return CruxMob.isInCategory(entity, AbyssMobCategory.ABYSSAL);
            }
        });

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
            //todo deprecated reg.registerFileHandler(AbyssWorld.PlayerData.class, new FileAbyssWorldPlayerData());
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
        CruxWorldUtil.CUSTOM_WORLD_CREATORS.put(
          "world_abyss",
          name ->{
              try {
                  ResourceKey var10000 = LevelStem.OVERWORLD;
                  ResourceKey<LevelStem> actualDimension = var10000;
                  ResourceKey<net.minecraft.world.level.Level> dimensionKey = PaperWorldLoader.dimensionKey(var10000);

                  DedicatedServer console;
                  try{
                      var field = (CraftServer.class.getDeclaredField("console"));
                      field.setAccessible(true);
                      console = (DedicatedServer) field
                        .get(getServer());
                  } catch (NoSuchFieldException | IllegalAccessException e){
                      throw new  RuntimeException(e);
                  }

                  LevelStem configuredStem = (LevelStem) console.registryAccess().lookupOrThrow(Registries.LEVEL_STEM).getValue(actualDimension);
                  if (configuredStem == null) {
                      throw new IllegalStateException("Missing configured level stem " + String.valueOf(actualDimension));
                  }
                  try {
                      WorldFolderMigration.migrateApiWorld(console.storageSource, console.registryAccess(), name, actualDimension, dimensionKey);
                  } catch (IOException ex) {
                      throw new RuntimeException("Failed to migrate legacy world " + name, ex);
                  }
                  WorldGenSettings worldGenSettings = (WorldGenSettings) LevelStorageSource.readExistingSavedData(console.storageSource, dimensionKey, console.registryAccess(), WorldGenSettings.TYPE).result().orElse(null);
                  if(worldGenSettings == null) {
                      var seed = CruxMath.random().nextLong();
                      return new WorldCreator(name)
                        .seed(seed)
                        .generator(AbyssGeneration.INSTANCE.buildGenerator(seed, AbyssGeneration.INSTANCE.getDefaultWorldDetails()));
                  }
                  var seed = worldGenSettings.options().seed();
                  return new WorldCreator(name)
                    .seed(seed)
                    .generator(AbyssGeneration.INSTANCE.buildGenerator(seed, AbyssGeneration.INSTANCE.getDefaultWorldDetails()));
              } catch (Exception e) {
                  e.printStackTrace();
              }
              var seed = CruxMath.random().nextLong();
              return new WorldCreator(name)
                .seed(seed)
                .generator(AbyssGeneration.INSTANCE.buildGenerator(seed, AbyssGeneration.INSTANCE.getDefaultWorldDetails()));
          }
        );

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
        menus.menuRegistry().menuActions().register(new AbyssRecallAnchorTeleportAction(Crux.key("abyss/outpost/upgrade/recall/teleport")));
        menus.menuRegistry().menuActions().register(new AbyssOutpostLockerOpenAction(Crux.key("abyss/outpost/upgrade/outpost_locker/open")));
        menus.menuRegistry().menuActions().register(new AbyssOutpostQuestLineAction(Crux.key("abyss/outpost/quest_line")));
        registerTextTags(Crux.tags());

        registerMenuModules(
            menus.menuRegistry().menuModule(),
            menus.menuModuleRegistry()
        );
        menus.menuRegistry().menuHolders().register(new AbyssOutpostCraftingMenuHolder(
            Crux.key("abyss/outpost/crafting"),
            "<white><crux_space:-8><font:\"crux:abyss\">2<reset><crux_space:-145>Abyss Outpost Crafting",
            NumberProvider.constant(27),
            MenuItems.items(new TreeMap<>()), DataExchange.empty(), Set.of()
        ));
        menus.menuRegistry().menuHolders().register(new AbyssOutpostCraftingRecipeListHolder(
            Crux.key("abyss/outpost/crafting_recipe_list"),
            "<white><crux_space:-8><font:\"crux:crafting\">1<reset><crux_space:-145>Abyss Outpost Recipes",
            NumberProvider.constant(45),
            MenuItems.items(new TreeMap<>()), DataExchange.single("crafting_recipe_manager", () -> craftingManager), Set.of()
        ));
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
            //new AbyssSafezoneListener(this),
            new AbyssTravelTrackingListener(values),
            new AbyssalListener(),
            new CustomProjectileListener(),
            new ObjectiveListener(),
            new HostileMobListener(),
            new AbyssOutpostListener(),
            new CustomObjectivesListener(),

            new RewardsListener(),

            new NetheriteListener()
        );
        AbyssBlocks.register();
        AbyssOutpostUpgrades.register();
        AbyssGeneration.INSTANCE.register();

        if(getServer().getPluginManager().getPlugin("CruxChallenges") != null){
            var manager = new AbyssChallengeManager(null);
            AbyssChallengeManager.setManager(manager);
            manager.load(this);

            new BukkitRunnable(){
                @Override
                public void run() {
                    if(AbyssChallengeManager.getMain() == null) return;
                    AbyssChallengeManager.getMain().tick();
                }
            }.runTaskTimerAsynchronously(this, 100L, 100L);
        }

        super.enabled();
        StandardAbyssGroups.register(AbyssRegistries.ABYSS_NATURAL_ENTITY_SPAWN_GROUP);
    }

    @Override
    public void disabled() {
        super.disabled();
        if(AbyssChallengeManager.getMain() != null) AbyssChallengeManager.getMain().save(this);
    }

    public void onComplete(World world, Chunk chunk) {
        List<AbyssWorld.OutpostSnapshot> previousOwners = AbyssWorld.WORLD_TO_ABYSS_OUTPOST_OWNERS.remove(world.key());
        if(previousOwners == null || previousOwners.isEmpty()) return;

        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) return;

        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;

        List<AbyssOutpostData> dataList = new ArrayList<>();
        module.getStored(stored -> stored.has(AbyssComponents.ABYSS_OUTPOST_DATA)).forEach(stored ->{
            AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
            Objects.requireNonNull(data);
            dataList.add(data);
        });
        if(dataList.isEmpty()){
            Crux.log(Level.WARNING, world.getName() + " abyss world had previous outpost owners but no abyss outposts were generated!");
            return;
        }
        if(dataList.size() < previousOwners.size()){
            Crux.log(Level.WARNING, world.getName() + " abyss world had previous outpost owners but the generated abyss outposts that were generated is less than the previous owner amount!" +
                " PreviousOwners=" + previousOwners.size() + ", AbyssOutposts=" + dataList.size());
            Crux.log(Level.WARNING, world.getName() + " abyss world... Attempting to set previous owners anyway.");
        }

        Collections.shuffle(previousOwners);
        Collections.shuffle(dataList);

        int index = -1;
        for(var oldSnapshot : previousOwners){
            index++;
            if(index >= dataList.size()) break;
            AbyssOutpostData data = dataList.get(index);

            AbyssOutpostData oldData = oldSnapshot.getData();
            data.owner = oldData.owner;
            data.timeCaptured = oldData.timeCaptured;
            data.timeLastInvasion = oldData.timeLastInvasion;
            data.timeInvaded = oldData.timeInvaded;
            data.defeatedPlagueTyrant = oldData.defeatedPlagueTyrant;
            oldData.getUpgrades().forEach((up, level) ->{
                data.setUpgradeLevel(up, level);

                var upgradeSnapshot = oldSnapshot.getSnapshotData().get(up);
                if(upgradeSnapshot == null) return;
                var ticked = data.getTickedOutpostUpgrade(up);
                if(ticked == null){
                    Crux.logError("Upgrade: " + up.key() + " has snapshot data but there is no ticked upgrade!");
                    return;
                }
                ticked.acceptSnapshot(upgradeSnapshot);
            });
        }
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

        new CruxCraftingRecipeLoader(CruxCraftingCfg.FILE_CRUX_CRAFTING_RECIPE, recipe ->{
            craftingManager.addRecipe(recipe);
            Crux.log(Level.INFO, "CruxAbyss abyss outpost crafting recipe registered: " + ((Keyed) recipe).key());
        }).loadConfiguration(
            new CruxFolder(this, "crafting/recipe/abyss_outpost").file()
        );

        if(AbyssChallengeManager.getMain() != null){
            DataFile file = BukkitDataFile.parseFromGeneralPath(CruxFolder.file(this, "abyss_challenges_table.json"));
            if(file != null){
                LootTable<ChallengeRoll> table = ChallengeRollLootTableLoader.CHALLENGE_ROLL_LOOT_TABLE.deserializeFromFile(
                    new FileContext<>(file.fileRegistry()), file.getRoot()
                );
                file.close();
                AbyssChallengeManager.getMain().setAvailableChallenges(table);
            }
        }
    }

    protected final CreateLang lang = Lang.setLang(new SimpleCreateLang());
    @NotNull
    public CreateLang lang() {
        return lang;
    }

    public void registerTextTags(TagParser tags){
        tags.register(List.of(
            new ActiveAbyssOutpostTags(),
            new StoredAbyssOutpostTags(),
            new AbyssPlayerTags(),
            new AbyssalRecallAnchorTags()
        ));
    }

    public void registerMenuModules(@NotNull FileMenuHolder<?> fileMenuHolder, @NotNull KeyedRegistry<MenuModuleBuilder> registry) {
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/abyss/outpost/owned")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<AbyssOutpostData>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<AbyssOutpostData>> getValues(@NotNull CfgMenu cfgMenu) {
                        Player player = cfgMenu.info().getOrThrow(Player.class);
                        return () -> {
                            List<AbyssOutpostData> list = new ArrayList<>(abyssOutpostManager.getAllOwnedAbyssOutposts(player.getUniqueId()));
                            list.sort(Comparator.comparing(e -> e.timeCaptured));
                            return list;
                        };
                    }
                };
            }
        });
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/abyss/outpost/friendly")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<AbyssOutpostData>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<AbyssOutpostData>> getValues(@NotNull CfgMenu cfgMenu) {
                        Player player = cfgMenu.info().getOrThrow(Player.class);
                        return () -> {
                            List<AbyssOutpostData> list = new ArrayList<>(abyssOutpostManager.getAllFriendlyAbyssOutposts(player.getUniqueId()));
                            list.sort(Comparator.comparing(e -> e.timeCaptured));
                            return list;
                        };
                    }
                };
            }
        });
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/abyss/outpost/members")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<OfflinePlayer>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<OfflinePlayer>> getValues(@NotNull CfgMenu cfgMenu) {
                        AbyssOutpostData data = cfgMenu.info().getOrThrow(AbyssOutpostData.class);
                        return () -> {
                            List<OfflinePlayer> list = new ArrayList<>();
                            for(UUID uuid : data.getMembers()){
                                list.add(getServer().getOfflinePlayer(uuid));
                            }
                            list.sort(Comparator.comparing(e -> e.getName() + ""));
                            return list;
                        };
                    }
                };
            }
        });
        registry.register(new SimpleFilePagedCfg(fileMenuHolder, Crux.key("paged/abyss/outpost/upgrade/recall/anchors")) {
            @NotNull
            @Override
            public MenuModule parsePaged(@NotNull String id,
                                         @NotNull NumberProvider indexes,
                                         @Nullable String valuesFilter,
                                         @Nullable MenuItems valueItems,
                                         @Nullable MenuItems emptyItems) {
                return new SimplePagedMenuModule<AbyssRecallAnchor>(id, indexes, valuesFilter, valueItems, emptyItems, this) {
                    @Override
                    public @NotNull Holder<List<AbyssRecallAnchor>> getValues(@NotNull CfgMenu cfgMenu) {
                        AbyssOutpostData data = cfgMenu.info().getOrThrow(AbyssOutpostData.class);
                        return () -> {
                            if(!(data.getTickedOutpostUpgrade(AbyssOutpostUpgrades.ABYSSAL_RECALL) instanceof ActiveAbyssalRecallUpgrade upgrade)) return List.of();
                            List<AbyssRecallAnchor> list = new ArrayList<>(upgrade.getRespawnAnchors());
                            list.sort(Comparator.comparing(AbyssRecallAnchor::getCharges));
                            return list;
                        };
                    }
                };
            }
        });
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
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("abyss_outpost_deactivate")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new AbyssOutpostDeactivateObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("abyss_survive_1_minute")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new Survive1MinuteAbyssObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("travel_through_abyss_portal_gateway")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new TravelThroughAbyssPortalGatewayObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("abyss_altar_build")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new AbyssAltarBuildObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("abyss_altar_activate_portal")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new AbyssAltarActivatePortalObjective(data, maxProgress);
            }
        });
        file.registerCustomHandler(new FileSimpleAdvanceObjective<>(key("abyss_outpost_upgrade")) {
            @Override
            public @Nullable AdvancementObjective deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e, @NotNull ObjectiveCommonData data) {
                Integer maxProgress = e.getObject(Integer.class, "amount");
                if(maxProgress==null) maxProgress = 1;
                return new AbyssOutpostUpgradeObjective(data, maxProgress);
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

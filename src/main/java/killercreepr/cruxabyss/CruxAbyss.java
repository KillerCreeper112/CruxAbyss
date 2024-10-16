package killercreepr.cruxabyss;

import killercreepr.crux.Crux;
import killercreepr.crux.component.TypedDataComponent;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.command.AbyssCommands;
import killercreepr.cruxabyss.component.AbyssComponents;
import killercreepr.cruxabyss.component.impl.AbyssConquestNode;
import killercreepr.cruxabyss.config.handler.FileAbyssOutpost;
import killercreepr.cruxabyss.config.handler.FileTestStructure;
import killercreepr.cruxabyss.item.AbyssItems;
import killercreepr.cruxabyss.listener.AbyssAltarPortalListener;
import killercreepr.cruxabyss.listener.AbyssWoodFunctionListener;
import killercreepr.cruxabyss.listener.DisableElytraListener;
import killercreepr.cruxabyss.registries.AbyssRegistries;
import killercreepr.cruxabyss.structure.AbyssOutpost;
import killercreepr.cruxabyss.structure.StoredAbyssOutpost;
import killercreepr.cruxabyss.structure.StoredTestStructure;
import killercreepr.cruxabyss.structure.TestStructure;
import killercreepr.cruxabyss.world.abyss.AbyssWorld;
import killercreepr.cruxabyss.world.abyss.entity.StandardAbyssGroups;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxconfig.config.bukkit.handler.BukkitCfgHandlers;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
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
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CruxAbyss extends CruxPlugin implements Listener {
    private static CruxAbyss instance;
    public static CruxAbyss inst(){ return instance; }

    @Override
    public void onLoad() {
        super.onLoad();
        new AbyssCommands(this).register();
        CfgRegistries.JSON_REGISTRY.forEach(registry ->{
            registry.registerFileHandler(StoredAbyssOutpost.class, new FileAbyssOutpost());
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
                            return new AbyssOutpost(current.key(), current.getHolder(), current.isPersistent(), current.getBeforePlacementModules(), current.getModules());
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

        BukkitCfgHandlers.TYPED_DATA_COMPONENT.typeHandlers().register("abyss_conquest_node", new FileDataComponentType<AbyssConquestNode>() {
            @Override
            public @Nullable TypedDataComponent<AbyssConquestNode> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e) {
                return TypedDataComponent.create(
                    AbyssComponents.ABYSS_CONQUEST_NODE, new AbyssConquestNode()
                );
            }
        });

        CruxWorldManager worldManager = CruxCore.inst().worldManager();
        worldManager.getCreatorRegistry().register("world_abyss", AbyssWorld::new);
    }

    @Override
    public void enabled() {
        instance = this;
        BiomeManager.register();
        registerListeners(
            this,
            new AbyssAltarPortalListener(),
            new DisableElytraListener(),
            new AbyssWoodFunctionListener()
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
    }
}

package killercreepr.cruxabyss.core.config.handler.component;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.component.impl.AbyssConquestNode;
import killercreepr.cruxabyss.core.component.impl.AbyssPortalGateway;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.loot.AbyssOutpostLootHolder;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import killercreepr.cruxconfig.config.bukkit.registry.FileDataComponentRegistry;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CfgAbyssComponents {
    public static void register(@NotNull FileDataComponentRegistry registry){
        registry.register("abyss_entity_spawner", new FileAbyssEntitySpawnerComponent());
        registry.register("abyss_conquest_node", new FileDataComponentType<AbyssConquestNode>() {
            @Override
            public @Nullable TypedDataComponent<AbyssConquestNode> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e) {
                FileRegistry reg = ctx.getRegistry();
                NumberProvider requiredExp = reg.deserializeFromFile(NumberProvider.class, e.get("required_exp"));
                if(requiredExp == null) requiredExp = NumberProvider.constant(100);
                NumberProvider takeOverTime = reg.deserializeFromFile(NumberProvider.class, e.get("take_over_time"));
                if(takeOverTime == null) takeOverTime = NumberProvider.constant(100);
                NumberProvider deactivateTime = reg.deserializeFromFile(NumberProvider.class, e.get("deactivate_time"));
                if(deactivateTime == null) deactivateTime = NumberProvider.constant(100);
                NumberProvider fireworksRange = reg.deserializeFromFile(NumberProvider.class, e.get("fireworks_range"));
                if(fireworksRange == null) fireworksRange = NumberProvider.constant(40);
                NumberProvider fireworksRangeY = reg.deserializeFromFile(NumberProvider.class, e.get("fireworks_range_y"));
                if(fireworksRangeY == null) fireworksRangeY = NumberProvider.constant(2);

                CreateSound takeOverSound = reg.deserializeFromFile(CreateSound.class, e.get("take_over_sound"));

                return TypedDataComponent.create(
                    AbyssComponents.ABYSS_CONQUEST_NODE, new AbyssConquestNode(
                        takeOverTime, requiredExp,
                        deactivateTime, fireworksRange,
                        fireworksRangeY, takeOverSound
                    )
                );
            }
        });
        registry.register("abyss_portal_gateway", new FileDataComponentType<AbyssPortalGateway>() {
            @Override
            public @Nullable TypedDataComponent<AbyssPortalGateway> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject e) {
                FileRegistry registry = ctx.getRegistry();
                NumberProvider tickPeriod = registry.deserializeFromFile(NumberProvider.class, e.get("tick_period"));
                if(tickPeriod == null) tickPeriod = NumberProvider.constant(20);
                NumberProvider checkRange = registry.deserializeFromFile(NumberProvider.class, e.get("check_range"));
                if(checkRange == null) checkRange = NumberProvider.constant(3);
                NumberProvider cooldown = registry.deserializeFromFile(NumberProvider.class, e.get("cooldown"));
                if(cooldown == null) cooldown = NumberProvider.constant(10);

                FileObject o = e.get("destination").getAsFileObject();
                String worldName = o.getObject(String.class, "world");
                double x = o.getObject(Double.class, "x", 0D);
                double y = o.getObject(Double.class, "y", 0D);
                double z = o.getObject(Double.class, "z", 0D);
                float yaw = o.getObject(Float.class, "yaw", 0f);
                float pitch = o.getObject(Float.class, "pitch", 0f);

                CreateSound spawnSound = registry.deserializeFromFile(CreateSound.class, e.get("spawn_sound"));
                CreateSound despawnSound = registry.deserializeFromFile(CreateSound.class, e.get("despawn_sound"));

                return TypedDataComponent.create(
                    AbyssComponents.ABYSS_PORTAL_GATEWAY, new AbyssPortalGateway(
                        tickPeriod,
                        checkRange,
                        cooldown,
                        () -> new Location(Crux.getServer().getWorld(worldName), x, y, z, yaw, pitch),
                        spawnSound, despawnSound
                    )
                );
            }
        });
        registry.register("structure/abyss_outpost", new FileDataComponentType<AbyssOutpost>(){
            @Override
            public @Nullable TypedDataComponent<AbyssOutpost> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(AbyssComponents.ABYSS_OUTPOST, new AbyssOutpost());
            }
        });
        registry.register("structure/abyss_outpost_loot_holder", new FileDataComponentType<AbyssOutpostLootHolder>(){
            @Override
            public @Nullable TypedDataComponent<AbyssOutpostLootHolder> deserializeFromFile(@NotNull FileContext<?> fileContext, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(AbyssComponents.ABYSS_OUTPOST_LOOT_HOLDER, new AbyssOutpostLootHolder());
            }
        });
        registry.register("abyss_hologram_offset", new FileDataComponentType<Vector>(){
            @Override
            public @Nullable TypedDataComponent<Vector> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(AbyssComponents.ABYSS_HOLOGRAM_OFFSET, ctx.getRegistry().deserializeFromFile(Vector.class, fileObject.get("value")));
            }
        });
        registry.register("abyss_hologram_format", new FileDataComponentType<String>(){
            @Override
            public @Nullable TypedDataComponent<String> deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileObject fileObject) {
                return TypedDataComponent.create(AbyssComponents.ABYSS_HOLOGRAM_FORMAT, fileObject.getObject(String.class, "value"));
            }
        });
    }
}

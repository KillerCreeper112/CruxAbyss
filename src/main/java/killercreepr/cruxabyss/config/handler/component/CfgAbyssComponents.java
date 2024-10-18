package killercreepr.cruxabyss.config.handler.component;

import killercreepr.crux.registry.MappedRegistry;
import killercreepr.cruxconfig.config.bukkit.handler.impl.component.FileDataComponentType;
import org.jetbrains.annotations.NotNull;

public class CfgAbyssComponents {
    public static void register(@NotNull MappedRegistry<String, FileDataComponentType<?>> registry){
        registry.register("abyss_entity_spawner", new FileAbyssEntitySpawnerComponent());
    }
}

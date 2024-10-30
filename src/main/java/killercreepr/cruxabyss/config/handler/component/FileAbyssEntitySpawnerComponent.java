package killercreepr.cruxabyss.config.handler.component;

import killercreepr.crux.component.DataComponentType;
import killercreepr.cruxabyss.component.AbyssComponents;
import killercreepr.cruxabyss.component.impl.AbyssEntitySpawner;
import killercreepr.cruxblocks.block.standard.component.EntitySpawnerComponent;
import killercreepr.cruxblocks.config.handler.component.FileEntitySpawnerComponent;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileObject;
import org.jetbrains.annotations.NotNull;

public class FileAbyssEntitySpawnerComponent extends FileEntitySpawnerComponent<AbyssEntitySpawner> {
    @Override
    public AbyssEntitySpawner createSpawner(@NotNull FileContext<?> ctx, @NotNull FileObject e) {
        EntitySpawnerComponent s = createGenericSpawner(ctx, e);
        if(s == null) return null;
        return new AbyssEntitySpawner(
            s.spawnDelay, s.spawnRange, s.innerSpawnDistance, s.spawnCount, s.requiredPlayerRange, s.maxSpawnAttempts,
            s.groupSpawnAmount, s.yCheck, s.failedDelay, s.spawns, s.ignoreCreativePlayers
        );
    }

    @Override
    public DataComponentType<AbyssEntitySpawner> componentType() {
        return AbyssComponents.ABYSS_ENTITY_SPAWNER;
    }
}

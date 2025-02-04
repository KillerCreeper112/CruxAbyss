package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.core.block.active.ActiveAbyssEntitySpawner;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.core.block.component.standard.EntitySpawnerComponent;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbyssEntitySpawner extends EntitySpawnerComponent {
    public AbyssEntitySpawner(@NotNull NumberProvider spawnDelay,
                              @NotNull NumberProvider spawnRange,
                              @NotNull NumberProvider innerSpawnDistance,
                              @NotNull NumberProvider spawnCount,
                              @NotNull NumberProvider requiredPlayerRange,
                              @NotNull NumberProvider maxSpawnAttempts,
                              @NotNull NumberProvider groupSpawnAmount,
                              @NotNull NumberProvider yCheck,
                              @NotNull NumberProvider failedDelay,
                              @NotNull Collection<NaturalEntitySpawnGroup> spawns,
                              boolean ignoreCreativePlayers,
                              boolean persistEntities) {
        super(spawnDelay, spawnRange, innerSpawnDistance, spawnCount, requiredPlayerRange,
            maxSpawnAttempts, groupSpawnAmount, yCheck, failedDelay, spawns,
            ignoreCreativePlayers,
            persistEntities);
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveAbyssEntitySpawner(block, crux, this, spawner);
    }
}

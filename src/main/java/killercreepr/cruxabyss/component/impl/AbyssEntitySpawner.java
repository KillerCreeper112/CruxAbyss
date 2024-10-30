package killercreepr.cruxabyss.component.impl;

import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.block.active.ActiveAbyssEntitySpawner;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.standard.component.EntitySpawnerComponent;
import killercreepr.cruxworlds.world.entity.NaturalEntitySpawnGroup;
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
                              boolean ignoreCreativePlayers) {
        super(spawnDelay, spawnRange, innerSpawnDistance, spawnCount, requiredPlayerRange, maxSpawnAttempts, groupSpawnAmount, yCheck, failedDelay, spawns, ignoreCreativePlayers);
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveAbyssEntitySpawner(block, crux, this, spawner);
    }
}

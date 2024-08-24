package killercreepr.cruxabyss.world.entity.impl;

import killercreepr.crux.data.world.CruxPosition;
import killercreepr.cruxabyss.world.entity.SpawnContext;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SimpleSpawnContext implements SpawnContext {
    protected final @NotNull World world;
    protected final @NotNull CruxPosition position;
    protected final @NotNull Random random;

    public SimpleSpawnContext(@NotNull World world, @NotNull CruxPosition position, @NotNull Random random) {
        this.world = world;
        this.position = position;
        this.random = random;
    }

    @NotNull
    @Override
    public CruxPosition getPosition() {
        return position;
    }

    @NotNull
    @Override
    public World getWorld() {
        return world;
    }

    @NotNull
    @Override
    public Random getRandom() {
        return random;
    }
}

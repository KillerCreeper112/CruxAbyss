package killercreepr.cruxabyss.world.generation;

import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

public abstract class BlockGenerator {
    public abstract void set(@NotNull LimitedRegion region, int x, int y, int z);
}

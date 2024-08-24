package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.data.world.CruxPosition;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface NaturalEntitySpawner {
    void navigate(@NotNull World world, @NotNull CruxPosition center);
    boolean canNavigate(@NotNull World world);
}

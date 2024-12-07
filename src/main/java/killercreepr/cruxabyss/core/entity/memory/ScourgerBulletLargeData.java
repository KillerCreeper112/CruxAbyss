package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class ScourgerBulletLargeData extends ScourgerBulletData{
    public static final Key KEY = Crux.key("scourger_bullet_large");
    public ScourgerBulletLargeData(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
    }

    public ScourgerBulletLargeData(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }
}

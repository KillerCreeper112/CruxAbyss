package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssalForgeUpgrade extends AbstractOutpostUpgrade{
    public AbyssalForgeUpgrade(Key key) {
        super(key);
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade createStored(@NotNull AbyssOutpostData data, int level) {
        return null;
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade createActive(@NotNull ActiveAbyssOutpost outpost, int level) {
        return null;
    }
}

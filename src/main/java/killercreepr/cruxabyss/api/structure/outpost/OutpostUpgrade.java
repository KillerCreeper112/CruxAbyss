package killercreepr.cruxabyss.api.structure.outpost;

import killercreepr.crux.api.data.CruxKeyed;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface OutpostUpgrade extends CruxKeyed {
    @Nullable TickedOutpostUpgrade createStored(@NotNull AbyssOutpostData data, int level);
    @Nullable TickedOutpostUpgrade createActive(@NotNull ActiveAbyssOutpost outpost, int level);
}

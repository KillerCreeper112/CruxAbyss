package killercreepr.cruxabyss.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.key.KeyLootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public interface AbyssOutpostLootHolderCfg {
    @NotNull NumberProvider ABYSS_OUTPOST_LOOT_HOLDER_TICK_TIME();
    @NotNull NumberProvider ABYSS_OUTPOST_LOOT_HOLDER_GENERATE_TIME();
    @NotNull Holder<KeyLootTable> ABYSS_OUTPOST_LOOT_HOLDER_BLOCK_LOOT();
}

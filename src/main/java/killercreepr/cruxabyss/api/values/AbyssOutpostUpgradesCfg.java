package killercreepr.cruxabyss.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.key.KeyLootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface AbyssOutpostUpgradesCfg {
    @NotNull Holder<String> ABYSS_OUTPOST_UPGRADE_REGENERATION_RANGE();
}

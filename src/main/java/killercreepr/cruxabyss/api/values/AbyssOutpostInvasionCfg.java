package killercreepr.cruxabyss.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.core.game.entity.MobWaveGroup;
import org.jetbrains.annotations.NotNull;

public interface AbyssOutpostInvasionCfg {
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_STORED_CHANCE();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_ACTIVE_CHANCE();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_COOLDOWN();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_WALL_MIN_SPAWN_DISTANCE();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_WALL_MAX_SPAWN_DISTANCE();

    @NotNull Holder<LootTable<MobWaveGroup>> ABYSS_OUTPOST_INVASION_WAVES();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_MAX_CAPTURE_TIME();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_OUTSIDE_WALLS_CAPTURE_TIME_DECREASE();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_MAX_PER_ENTITY();
    @NotNull NumberProvider ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_FALL_OFF_RATE();
}

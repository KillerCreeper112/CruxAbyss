package killercreepr.cruxabyss.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider SPAWN_TELEPORT_TIME = NumberProvider.constant(100);
    public static final NumberProvider ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE = NumberProvider.uniform(50, 100);
    public static final NumberProvider ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE = NumberProvider.uniform(24, 48);

    @NotNull
    @Override
    public Holder<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE() {
        return ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE() {
        return ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE;
    }
}

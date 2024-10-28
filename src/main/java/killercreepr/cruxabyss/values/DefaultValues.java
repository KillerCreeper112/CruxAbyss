package killercreepr.cruxabyss.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider SPAWN_TELEPORT_TIME = NumberProvider.constant(100);

    @NotNull
    @Override
    public Holder<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS() {
        return null;
    }
}

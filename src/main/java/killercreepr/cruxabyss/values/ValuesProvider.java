package killercreepr.cruxabyss.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.data.Reloadable;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ValuesProvider extends Reloadable {
    @NotNull
    Holder<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS();
}

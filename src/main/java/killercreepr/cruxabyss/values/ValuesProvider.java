package killercreepr.cruxabyss.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.data.Reloadable;
import killercreepr.crux.valueproviders.number.NumberProvider;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ValuesProvider extends Reloadable {
    @NotNull
    Holder<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS();
    @NotNull
    NumberProvider ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE();
    @NotNull
    NumberProvider ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_TICK_EVERY();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE();
    @NotNull
    NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL();
    @NotNull
    NumberProvider ABYSS_RIFT_SHOW_WARNING_IF_BELOW();
    @NotNull
    NumberProvider ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW();
}

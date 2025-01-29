package killercreepr.cruxabyss.api.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.data.Reloadable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

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
    @NotNull
    Holder<Map<Key, Collection<PotionEffect>>> ABYSS_WATER_EFFECTS();
    @NotNull NumberProvider ANIMAL_DEATH_RANGE();
    @NotNull Holder<Collection<PotionEffect>> ANIMAL_DEATH_EFFECTS_NEARBY();
    @NotNull NumberProvider ABYSS_NATURAL_HEALING_MULTIPLIER();
    @NotNull NumberProvider ABYSS_OUTPOST_INVADE_CONQUEST_COOLDOWN();
}

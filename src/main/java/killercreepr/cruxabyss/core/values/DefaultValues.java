package killercreepr.cruxabyss.core.values;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE = NumberProvider.uniform(50, 100);
    public static final NumberProvider ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE = NumberProvider.uniform(24, 48);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_TICK_EVERY = NumberProvider.uniform(60, 100);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT = NumberProvider.uniform(3, 5);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START = NumberProvider.uniform(1D, 3D);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT = NumberProvider.uniform(1D, 3D);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = NumberProvider.uniform(.01, .03);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE = NumberProvider.uniform(-.5, .5);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL = NumberProvider.uniform(-.5, .5);
    public static final NumberProvider ABYSS_RIFT_SHOW_WARNING_IF_BELOW = NumberProvider.constant(1);
    public static final NumberProvider ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW = NumberProvider.constant(3);
    public static final NumberProvider ANIMAL_DEATH_RANGE = NumberProvider.uniform(.7, 1.2);

    @NotNull
    @Override
    public Holder<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS() {
        return Holder.empty();
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
    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_TICK_EVERY() {
        return ABYSS_SAFEZONE_GUIDE_TICK_EVERY;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_RIFT_SHOW_WARNING_IF_BELOW() {
        return ABYSS_RIFT_SHOW_WARNING_IF_BELOW;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW() {
        return ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW;
    }

    @NotNull
    @Override
    public Holder<Map<Key, Collection<PotionEffect>>> ABYSS_WATER_EFFECTS() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider ANIMAL_DEATH_RANGE() {
        return ANIMAL_DEATH_RANGE;
    }

    @NotNull
    @Override
    public Holder<Collection<PotionEffect>> ANIMAL_DEATH_EFFECTS_NEARBY() {
        return null;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_NATURAL_HEALING_MULTIPLIER() {
        return null;
    }
}

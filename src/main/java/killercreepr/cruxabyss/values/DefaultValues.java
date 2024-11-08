package killercreepr.cruxabyss.values;

import killercreepr.crux.data.Holder;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.crux.valueproviders.number.UniformNumber;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DefaultValues implements ValuesProvider {
    public static final NumberProvider ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE = NumberProvider.uniform(50, 100);
    public static final NumberProvider ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE = NumberProvider.uniform(24, 48);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_TICK_EVERY = new UniformNumber(60, 100);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT = new UniformNumber(3, 5);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START = new UniformNumber(1D, 3D);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT = new UniformNumber(1D, 3D);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new UniformNumber(.01, .03);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE = new UniformNumber(-.5, .5);
    public static final NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL = new UniformNumber(-.5, .5);

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
}

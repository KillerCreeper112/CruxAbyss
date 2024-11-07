package killercreepr.cruxabyss.config;

import killercreepr.crux.data.Holder;
import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.values.DefaultValues;
import killercreepr.cruxabyss.values.ValuesProvider;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Set;

public class Config extends Cfg implements ValuesProvider {
    public final CfgValue<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS = new CommonValue<>(Set.of(
        new PotionEffect(PotionEffectType.SPEED, 200, 1, false, false, true)
    )){};
    public final NumCfgValue ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE = new NumCfgValue(DefaultValues.ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE);
    public final NumCfgValue ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE = new NumCfgValue(DefaultValues.ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE);
    public Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Config(@NotNull File file) {
        super(file);
    }

    public Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }

    @Override
    public void reload(@NotNull CruxPlugin plugin) {
        setup();
    }

    @NotNull
    @Override
    public Holder<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS() {
        return ABYSS_OUTPOST_TAKE_OVER_EFFECTS;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE() {
        return ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE() {
        return ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE.value();
    }
}

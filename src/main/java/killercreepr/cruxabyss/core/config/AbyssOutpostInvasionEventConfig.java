package killercreepr.cruxabyss.core.config;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.api.values.AbyssOutpostInvasionCfg;
import killercreepr.cruxabyss.core.game.entity.MobWaveGroup;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.bukkit.file.JsonCfg;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.cruxconfig.config.common.annotations.Config;
import killercreepr.cruxconfig.config.common.json.registry.JsonRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Config(autoUpdate = true)
public class AbyssOutpostInvasionEventConfig extends JsonCfg implements AbyssOutpostInvasionCfg {
    public final CfgValue<LootTable<MobWaveGroup>> ABYSS_OUTPOST_INVASION_WAVES = new CommonValue<>(){};
    public final NumCfgValue ABYSS_OUTPOST_INVASION_MAX_CAPTURE_TIME = new NumCfgValue(NumberProvider.uniform(3000, 4200));
    public final NumCfgValue ABYSS_OUTPOST_INVASION_OUTSIDE_WALLS_CAPTURE_TIME_DECREASE = new NumCfgValue(NumberProvider.uniform(10f, 20f));
    public final NumCfgValue ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_MAX_PER_ENTITY = new NumCfgValue(10f);
    public final NumCfgValue ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_FALL_OFF_RATE = new NumCfgValue(10f);
    public final NumCfgValue ABYSS_OUTPOST_INVASION_WALL_MIN_SPAWN_DISTANCE = new NumCfgValue(NumberProvider.constant(16D));
    public final NumCfgValue ABYSS_OUTPOST_INVASION_WALL_MAX_SPAWN_DISTANCE = new NumCfgValue(NumberProvider.constant(32D));
    public final NumCfgValue ABYSS_OUTPOST_INVASION_STORED_CHANCE = new NumCfgValue(NumberProvider.constant(1f));
    public final NumCfgValue ABYSS_OUTPOST_INVASION_ACTIVE_CHANCE = new NumCfgValue(NumberProvider.constant(6f));
    public final NumCfgValue ABYSS_OUTPOST_INVASION_COOLDOWN = new NumCfgValue(NumberProvider.constant(1200*5));

    public AbyssOutpostInvasionEventConfig(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull File file) {
        super(file);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull CruxJson cfg) {
        super(cfg);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull File file, @NotNull JsonRegistry jsonRegistry, boolean reloadIfExists) {
        super(file, jsonRegistry, reloadIfExists);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull Plugin plugin, @NotNull String path, @NotNull JsonRegistry jsonRegistry, boolean reloadIfExists) {
        super(plugin, path, jsonRegistry, reloadIfExists);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull File file, @NotNull JsonRegistry jsonRegistry) {
        super(file, jsonRegistry);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull Plugin plugin, @NotNull String path, @NotNull JsonRegistry jsonRegistry) {
        super(plugin, path, jsonRegistry);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull File file, boolean reloadIfExists) {
        super(file, reloadIfExists);
    }

    public AbyssOutpostInvasionEventConfig(@NotNull Plugin plugin, @NotNull String path, boolean reloadIfExists) {
        super(plugin, path, reloadIfExists);
    }

    @NotNull
    @Override
    public Holder<LootTable<MobWaveGroup>> ABYSS_OUTPOST_INVASION_WAVES() {
        return ABYSS_OUTPOST_INVASION_WAVES;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_MAX_CAPTURE_TIME() {
        return ABYSS_OUTPOST_INVASION_MAX_CAPTURE_TIME.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_OUTSIDE_WALLS_CAPTURE_TIME_DECREASE() {
        return ABYSS_OUTPOST_INVASION_OUTSIDE_WALLS_CAPTURE_TIME_DECREASE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_MAX_PER_ENTITY() {
        return ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_MAX_PER_ENTITY.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_FALL_OFF_RATE() {
        return ABYSS_OUTPOST_INVASION_CAPTURE_AMOUNT_FALL_OFF_RATE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_STORED_CHANCE() {
        return ABYSS_OUTPOST_INVASION_STORED_CHANCE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_ACTIVE_CHANCE() {
        return ABYSS_OUTPOST_INVASION_ACTIVE_CHANCE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_COOLDOWN() {
        return ABYSS_OUTPOST_INVASION_COOLDOWN.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_WALL_MIN_SPAWN_DISTANCE() {
        return ABYSS_OUTPOST_INVASION_WALL_MIN_SPAWN_DISTANCE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_INVASION_WALL_MAX_SPAWN_DISTANCE() {
        return ABYSS_OUTPOST_INVASION_WALL_MAX_SPAWN_DISTANCE.value();
    }
}

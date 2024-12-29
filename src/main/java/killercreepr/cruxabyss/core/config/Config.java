package killercreepr.cruxabyss.core.config;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.loot.key.KeyLootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.loot.SimpleLootPool;
import killercreepr.crux.core.loot.SimpleLootPoolObject;
import killercreepr.crux.core.loot.key.SimpleKeyLootTable;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxabyss.api.values.AbyssOutpostLootHolderCfg;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.values.DefaultValues;
import killercreepr.cruxconfig.config.bukkit.file.Cfg;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.loader.KeyLootTableLoader;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.bukkit.value.CommonValue;
import killercreepr.cruxconfig.config.bukkit.value.NumCfgValue;
import killercreepr.usurvive.core.block.USurviveBlocks;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@killercreepr.cruxconfig.config.common.annotations.Config(autoUpdate = true)
public class Config extends Cfg implements ValuesProvider, AbyssOutpostLootHolderCfg {
    public final CfgValue<Collection<PotionEffect>> ABYSS_OUTPOST_TAKE_OVER_EFFECTS = new CommonValue<>(Set.of(
        new PotionEffect(PotionEffectType.SPEED, 200, 1, false, false, true)
    )){};
    public final NumCfgValue ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE = new NumCfgValue(DefaultValues.ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE);
    public final NumCfgValue ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE = new NumCfgValue(DefaultValues.ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_TICK_EVERY = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_TICK_EVERY);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE);
    public final NumCfgValue ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL = new NumCfgValue(DefaultValues.ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL);
    public final NumCfgValue ABYSS_RIFT_SHOW_WARNING_IF_BELOW = new NumCfgValue(DefaultValues.ABYSS_RIFT_SHOW_WARNING_IF_BELOW);
    public final NumCfgValue ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW = new NumCfgValue(DefaultValues.ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW);
    public final CfgValue<Map<Key, Collection<PotionEffect>>> ABYSS_WATER_EFFECTS = new CommonValue<>(Map.of(
        Crux.key("toxic_mire"), List.of(new PotionEffect(PotionEffectType.POISON, 60, 0)),
        Crux.key("toxic_grasslands"), List.of(new PotionEffect(PotionEffectType.POISON, 60, 0)),
        Crux.key("eldritch_wastes"), List.of(new PotionEffect(PotionEffectType.NAUSEA, 100, 0), new PotionEffect(PotionEffectType.HUNGER, 60, 1))
    )){};
    public final NumCfgValue ANIMAL_DEATH_RANGE = new NumCfgValue(DefaultValues.ANIMAL_DEATH_RANGE);
    public final CfgValue<Collection<PotionEffect>> ANIMAL_DEATH_EFFECTS_NEARBY = new CommonValue<>(Set.of(
        new PotionEffect(PotionEffectType.POISON, 70, 1)
    )){};
    public final NumCfgValue ABYSS_NATURAL_HEALING_MULTIPLIER = new NumCfgValue(.5f);
    public final NumCfgValue ABYSS_OUTPOST_LOOT_HOLDER_TICK_TIME = new NumCfgValue(300);
    public final NumCfgValue ABYSS_OUTPOST_LOOT_HOLDER_GENERATE_TIME = new NumCfgValue(NumberProvider.uniform(72000, 108000));
    public final CommonValue<KeyLootTable> ABYSS_OUTPOST_LOOT_HOLDER_BLOCK_LOOT = new CommonValue<>(/*new SimpleKeyLootTable(
        Crux.key("none"), NumberProvider.constant(1), List.of(
            new SimpleLootPool<>(null, null, NumberProvider.constant(1), List.of(
                new SimpleLootPoolObject<>(10, 0f, Holder.direct(List.of(Crux.key("orbit_ore")))),
                new SimpleLootPoolObject<>(10, 0f, Holder.direct(List.of(Key.key("iron_ore")))),
                new SimpleLootPoolObject<>(10, 0f, Holder.direct(List.of(Key.key("gold_ore"))))
            ))
    ))*/){};

    public Config(@NotNull Plugin plugin, @NotNull String path) {
        super(plugin, path);
    }

    public Config(@NotNull File file) {
        super(file);
    }

    public Config(@NotNull CruxConfig cfg) {
        super(cfg);
    }

    @NotNull
    @Override
    public NumberProvider ANIMAL_DEATH_RANGE() {
        return ANIMAL_DEATH_RANGE.value();
    }

    @NotNull
    @Override
    public Holder<Collection<PotionEffect>> ANIMAL_DEATH_EFFECTS_NEARBY() {
        return ANIMAL_DEATH_EFFECTS_NEARBY;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_NATURAL_HEALING_MULTIPLIER() {
        return ABYSS_NATURAL_HEALING_MULTIPLIER.value();
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

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_TICK_EVERY() {
        return ABYSS_SAFEZONE_GUIDE_TICK_EVERY.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT_START.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_SHIFT.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_SPEED_DISTANCE_MULTIPLIER.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_SIDE.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL() {
        return ABYSS_SAFEZONE_GUIDE_PARTICLE_OFFSET_VERTICAL.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_RIFT_SHOW_WARNING_IF_BELOW() {
        return ABYSS_RIFT_SHOW_WARNING_IF_BELOW.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW() {
        return ABYSS_RIFT_SAFEZONE_GUIDE_IF_BELOW.value();
    }

    @NotNull
    @Override
    public Holder<Map<Key, Collection<PotionEffect>>> ABYSS_WATER_EFFECTS() {
        return ABYSS_WATER_EFFECTS;
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_LOOT_HOLDER_TICK_TIME() {
        return ABYSS_OUTPOST_LOOT_HOLDER_TICK_TIME.value();
    }

    @NotNull
    @Override
    public NumberProvider ABYSS_OUTPOST_LOOT_HOLDER_GENERATE_TIME() {
        return ABYSS_OUTPOST_LOOT_HOLDER_GENERATE_TIME.value();
    }

    @NotNull
    @Override
    public Holder<KeyLootTable> ABYSS_OUTPOST_LOOT_HOLDER_BLOCK_LOOT() {
        return ABYSS_OUTPOST_LOOT_HOLDER_BLOCK_LOOT;
    }
}

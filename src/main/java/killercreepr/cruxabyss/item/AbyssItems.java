package killercreepr.cruxabyss.item;

import killercreepr.crux.Crux;
import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.util.CruxString;
import killercreepr.cruxitems.item.CruxedItem;
import killercreepr.cruxitems.item.plugin.GenericPluginItem;
import killercreepr.cruxitems.item.plugin.PluginItem;
import killercreepr.cruxitems.registries.CruxItemRegistries;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssItems {
    public static void register(){}
    public static final PluginItem PLAGUE_MOSS = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_moss"), 1));
    public static final PluginItem PLAGUE_MOSS_DIRT = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_moss_dirt"), 2));
    public static final PluginItem PLAGUE_STONE = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_stone"), 3));
    public static final PluginItem PLAGUE_STEM = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_stem"), 4));
    public static final PluginItem PLAGUE_WART = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_wart"), 5));

    public static final PluginItem RED_ABYSS_CRYSTAL = CruxItemRegistries.ITEMS.register(new GenericPluginItem(Crux.key("red_abyss_crystal")) {
        @Override
        public @NotNull CruxedItem build(@Nullable Entity entity, @Nullable MergedTagContainer mergedTagContainer) {
            CruxedItem item = new CruxedItem(Material.PAPER);
            item.customModelData(6).displayName(CruxString.toTitleCase(key.value()));
            return item;
        }
    });

    public static final PluginItem GREEN_ABYSS_CRYSTAL = CruxItemRegistries.ITEMS.register(new GenericPluginItem(Crux.key("green_abyss_crystal")) {
        @Override
        public @NotNull CruxedItem build(@Nullable Entity entity, @Nullable MergedTagContainer mergedTagContainer) {
            CruxedItem item = new CruxedItem(Material.PAPER);
            item.customModelData(7).displayName(CruxString.toTitleCase(key.value()));
            return item;
        }
    });

    public static final PluginItem BLUE_ABYSS_CRYSTAL = CruxItemRegistries.ITEMS.register(new GenericPluginItem(Crux.key("blue_abyss_crystal")) {
        @Override
        public @NotNull CruxedItem build(@Nullable Entity entity, @Nullable MergedTagContainer mergedTagContainer) {
            CruxedItem item = new CruxedItem(Material.PAPER);
            item.customModelData(8).displayName(CruxString.toTitleCase(key.value()));
            return item;
        }
    });

    public static final PluginItem PLAGUE_VEIL = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_veil"), 6));
}

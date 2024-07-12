package killercreepr.cruxabyss.item;

import killercreepr.crux.Crux;
import killercreepr.cruxitems.item.plugin.PluginItem;
import killercreepr.cruxitems.registries.CruxItemRegistries;

public class AbyssItems {
    public static void register(){}
    public static final PluginItem PLAGUE_MOSS = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_moss"), 1));
    public static final PluginItem PLAGUE_MOSS_DIRT = CruxItemRegistries.ITEMS.register(new BlockPluginItem(Crux.key("plague_moss_dirt"), 1));
}

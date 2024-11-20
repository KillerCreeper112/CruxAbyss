package killercreepr.cruxabyss.entity.mob;

import killercreepr.crux.core.Crux;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleMobCategory;
import killercreepr.cruxentities.registries.CruxEntityRegistries;

public class AbyssMobCategory {
    public static void register(){}
    /**
     * Represents friendly mobs that spawn within abyss safezones.
     */
    public static MobCategory ABYSS_SAFEZONE = CruxEntityRegistries.MOB_CATEGORY.register(new SimpleMobCategory(Crux.key("abyss_safezone")));
    /**
     * Represents aggressive mobs that spawn within abyss outposts.
     */
    public static MobCategory ABYSS_OUTPOST = CruxEntityRegistries.MOB_CATEGORY.register(new SimpleMobCategory(Crux.key("abyss_outpost")));
}

package killercreepr.cruxabyss.item;

import killercreepr.crux.ItemTag;
import killercreepr.cruxitems.CruxedItemTag;

public class AbyssItemTags {
    public static final CruxedItemTag ABYSS_GEMS = new CruxedItemTag.Builder()
        .add(
            AbyssItems.GREEN_ABYSS_CRYSTAL,
            AbyssItems.RED_ABYSS_CRYSTAL,
            AbyssItems.BLUE_ABYSS_CRYSTAL
        )
        .build();
}

package killercreepr.cruxabyss.item;


import killercreepr.crux.ItemTag;
import killercreepr.crux.SimpleItemTypeTag;

public class AbyssItemTags {
    public static final ItemTag ABYSS_GEMS = new SimpleItemTypeTag.Builder()
        .add(
            AbyssItems.GREEN_ABYSS_CRYSTAL.key(),
            AbyssItems.RED_ABYSS_CRYSTAL.key(),
            AbyssItems.BLUE_ABYSS_CRYSTAL.key()
        )
        .build();
}

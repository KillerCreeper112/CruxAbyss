package killercreepr.cruxabyss.api.component;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AbyssAltarItemComponent {
    boolean canPlaceOn(@NotNull AbyssAltar altar);
    void place(@NotNull AbyssAltar altar, @NotNull Player user, @NotNull CruxItem item);
}

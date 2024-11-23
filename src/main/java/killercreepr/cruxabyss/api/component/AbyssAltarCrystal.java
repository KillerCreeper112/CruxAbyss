package killercreepr.cruxabyss.api.component;

import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

public interface AbyssAltarCrystal extends AbyssAltarItemComponent{
    @Nullable
    Color portalColor();
    @Nullable String teleportType();
}

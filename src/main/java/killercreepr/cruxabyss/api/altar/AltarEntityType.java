package killercreepr.cruxabyss.api.altar;

import net.kyori.adventure.key.Keyed;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface AltarEntityType extends Keyed {
    @NotNull
    AltarEntity createAltarEntity(@NotNull AbyssAltar altar, @NotNull Entity from);
}

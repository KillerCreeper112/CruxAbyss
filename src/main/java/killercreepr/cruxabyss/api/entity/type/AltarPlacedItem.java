package killercreepr.cruxabyss.api.entity.type;

import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface AltarPlacedItem extends CruxMob {
    @NotNull
    Entity place(@NotNull Location location, ItemStack display, @Nullable Consumer<Entity> consumer);

    @NotNull
    default Entity place(@NotNull Location location, ItemStack display){
        return place(location, display, null);
    }
}

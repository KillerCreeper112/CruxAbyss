package killercreepr.cruxabyss.api.entity.type;

import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface AltarPlacedItem extends CruxMob {
    @NotNull
    default Entity place(@NotNull Location location, ItemStack display, @Nullable Consumer<Entity> consumer){
        return place(location, null, display, consumer);
    }

    @NotNull
    Entity place(@NotNull Location location, AbyssAltar altar, ItemStack display, @Nullable Consumer<Entity> consumer);

    default @NotNull
    Entity place(@NotNull Location location, AbyssAltar altar, ItemStack display){
        return place(location, altar, display, null);
    }

    @NotNull
    default Entity place(@NotNull Location location, ItemStack display){
        return place(location, display, null);
    }
}

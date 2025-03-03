package killercreepr.cruxabyss.core.menu;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxcrafting.core.menu.GenericRecipeListMenu;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.core.menu.holder.SimpleMenuHolder;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbyssOutpostCraftingRecipeListHolder extends SimpleMenuHolder {
    public AbyssOutpostCraftingRecipeListHolder(@NotNull Key key, @Nullable String title,
                                                @NotNull NumberProvider size, @NotNull MenuItems items,
                                                @NotNull DataExchange info, @NotNull Collection<MenuModule> modules) {
        super(key, title, size, items, info, modules);
    }

    @Override
    public @NotNull CfgMenu createMenu(@NotNull DataExchange data) {
        return createMenu(data, null);
    }

    @Override
    public @NotNull CfgMenu createMenu(@NotNull DataExchange data, @Nullable MergedTagContainer tags) {
        return new GenericRecipeListMenu(this, data.append("crafting_recipe_manager", () -> CruxAbyss.inst().getCraftingManager()), tags);
    }
}

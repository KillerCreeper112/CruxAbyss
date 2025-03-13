package killercreepr.cruxabyss.core.menu;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxmenus.api.event.MenuOpenEvent;
import killercreepr.cruxmenus.api.menu.CfgMenu;
import killercreepr.cruxmenus.api.menu.Menu;
import killercreepr.cruxmenus.api.menu.container.MenuContainer;
import killercreepr.cruxmenus.api.menu.holder.MenuItems;
import killercreepr.cruxmenus.api.menu.module.MenuModule;
import killercreepr.cruxmenus.core.menu.holder.SimpleMenuHolder;
import killercreepr.cruxmenus.core.registries.Menus;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbyssOutpostCraftingMenuHolder extends SimpleMenuHolder {
    public AbyssOutpostCraftingMenuHolder(@NotNull Key key, @Nullable String title,
                                          @NotNull NumberProvider size, @NotNull MenuItems items,
                                          @NotNull DataExchange info, @NotNull Collection<MenuModule> modules) {
        super(key, title, size, items, info, modules);
    }

    @Override
    public @NotNull CfgMenu createMenu(@NotNull DataExchange data) {
        return new AbyssOutpostCraftingMenu(this, data);
    }

    @Override
    public @NotNull CfgMenu createMenu(@NotNull DataExchange data, @Nullable MergedTagContainer tags) {
        return new AbyssOutpostCraftingMenu(this, data, tags);
    }

    @Override
    public @NotNull MenuOpenEvent open(@NotNull HumanEntity p, @NotNull DataExchange data, @Nullable MergedTagContainer tags) {
        MenuContainer container = MenuContainer.createNew();
        Menu opened = Menus.getOpened(p);
        if(opened != null){
            container.addOpenedMenu(opened);
        }
        DataExchange.Builder builder = DataExchange.builder().putAll(data);
        builder.put("viewer", p).put("menu_container", container);
        CfgMenu menu = this.createMenu(builder.build(), tags);
        menu.load();
        MenuOpenEvent event = menu.open(p);
        container.addOpenedMenu(event);
        return event;
    }
}

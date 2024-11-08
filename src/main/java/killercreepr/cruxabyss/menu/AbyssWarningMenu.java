package killercreepr.cruxabyss.menu;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxItem;
import killercreepr.cruxmenus.core.menu.BukkitMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class AbyssWarningMenu extends BukkitMenu {
    protected final Runnable tpTask;

    public AbyssWarningMenu(Runnable tpTask) {
        this.tpTask = tpTask;
    }

    protected boolean close = false;
    @Override
    public void onClose(@NotNull Player p) {
        super.onClose(p);
        if(close) return;
        Crux.scheduler().runTask(() ->{
            AbyssWarningMenu menu = new AbyssWarningMenu(tpTask);
            menu.load();
            menu.open(p);
        });
    }

    public boolean shouldClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    @Override
    public void onRefresh() {
        reconstruct(9 , Component.text("WARNING!"));
        super.onRefresh();

        setItem(3, CruxItem.create(Material.BARRIER)
            .itemName("<red>Cancel")
            .addLoreFromString(
                "",
                "<gray>You will <bold>not</bold> receive your",
                "<gray>abyss crystal back if you cancel.",
                "",
                "<yellow><latinfont:Click to cancel>"
            )
            .item(), new SimpleFixedSlot(this, 3){
            @Override
            public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                super.onClick(p, event);
                close = true;
                p.closeInventory();
            }
        });

        setItem(4, CruxItem.create(Material.OAK_SIGN)
            .itemName("<white>You are about to enter")
            .addLoreFromString(
                "<white>the abyss.",
                "<white>The abyss is incredibly <red>dangerous<white>!",
                "<white>Once you enter, the only way out",
                "<white>is death or to find a safezone!",
                "",
                "<white>Because it is your first time, particles",
                "<white>will guide you to the nearest safezone.",
                "",
                "<yellow>Are you sure you want to proceed?"
            )
            .item(), new SimpleFixedSlot(this, 4));

        setItem(5, CruxItem.create(Material.BARRIER)
            .itemName("<green>Proceed")
            .addLoreFromString(
                "",
                "<yellow><latinfont:Click to teleport>"
            )

            .item(), new SimpleFixedSlot(this, 5){
            @Override
            public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                super.onClick(p, event);
                close = true;
                p.closeInventory();
                tpTask.run();
            }
        });
    }
}

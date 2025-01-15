package killercreepr.cruxabyss.core.menu;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxmenus.core.menu.BukkitMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class AbyssWarningMenu extends BukkitMenu {
    protected final Runnable tpTask;

    public AbyssWarningMenu(Runnable tpTask) {
        this.tpTask = tpTask;
    }

    protected boolean close = false;
    @Override
    public void onClose(@NotNull HumanEntity p) {
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
        reconstruct(54 , Crux.format().deserialize("<white><font:\"crux:spacing\"><unicode_spacing:-8></font><font:\"crux:abyss\">1"));
        super.onRefresh();

        for(int i = getInventory().getSize()-9; i < getInventory().getSize(); i++){
            setItem(i, CruxItem.create(Material.OAK_SIGN)
                .itemName("<white>You are about to enter")
                .addLoreFromString(
                    "<white>the abyss.",
                    "<white>The abyss is incredibly <red>dangerous<white>!",
                    "",
                    "<white>Once you enter, you will be teleported and a",
                    "<white>rift will open up on the other side. You will",
                    "<white>have <bold>1 minute</bold> to check your",
                    "<white>surroundings and decide if you want to stay.",
                    "<white>After that 1 minute is up and the rift closes, the",
                    "<white>only way out is death or to find a safezone!",
                    "",
                    "<white>Because it is your first time, particles",
                    "<white>will guide you to the nearest safezone.",
                    "",
                    "<yellow>Are you sure you want to proceed?"
                )
                .itemModel(Crux.key("gui/nothing"))
                .item(), new SimpleFixedSlot(this, 4));
        }

        int[] indexes = new int[]{52, 51};
        for(int i : indexes){
            setItem(i, CruxItem.create(Material.BARRIER)
                .itemName("<red>Cancel")
                .addLoreFromString(
                    "",
                    "<gray>You will <bold>not</bold> receive your",
                    "<gray>abyss crystal back if you cancel.",
                    "",
                    "<yellow><latinfont:Click to cancel>"
                )
                .itemModel(Crux.key("gui/nothing"))
                .item(), new SimpleFixedSlot(this, 3){
                @Override
                public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                    super.onClick(p, event);
                    close = true;
                    p.closeInventory();
                }
            });
        }

        indexes = new int[]{47, 46};
        for(int i : indexes){
            setItem(i, CruxItem.create(Material.STRUCTURE_VOID)
                .itemName("<green>Proceed")
                .addLoreFromString(
                    "",
                    "<yellow><latinfont:Click to teleport>"
                )
                .itemModel(Crux.key("gui/nothing"))
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
}

package killercreepr.cruxabyss.core.menu;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveOutpostLockerUpgrade;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.BukkitMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OutpostLockerMenu extends BukkitMenu {
    protected final ActiveOutpostLockerUpgrade upgrade;
    public OutpostLockerMenu(ActiveOutpostLockerUpgrade upgrade) {
        this.upgrade = upgrade;
    }

    public OutpostLockerMenu(@NotNull UUID uuid, ActiveOutpostLockerUpgrade upgrade) {
        super(uuid);
        this.upgrade = upgrade;
    }

    public OutpostLockerMenu(@NotNull UUID uuid, Inventory inventory, ActiveOutpostLockerUpgrade upgrade) {
        super(uuid, inventory);
        this.upgrade = upgrade;
    }

    protected int page = 0;
    @Override
    public void load(){
        load(0);
    }

    public void load(int page){
        this.page = page;
        int size = upgrade.getStorage()[page].getSize();
        int start = size-9;
        for(int i = start; i < size; i++){
            upgrade.getStorage()[page].setItem(i, CruxItem.create(Material.BLACK_STAINED_GLASS_PANE)
                .customName("")
                .editMeta(meta -> CruxTag.set(meta, "menu_item", PersistentDataType.BOOLEAN, true))
                .item());
        }
        reconstruct(upgrade.getStorage()[page]);

        setItem(start+3, CruxItem.create(Material.ARROW)
            .itemName("Previous Page")
            .item(),
            buildPageSlot(start+3, -1)
        );
        setItem(start+5, CruxItem.create(Material.ARROW)
            .itemName("Next Page")
            .item(),
            buildPageSlot(start+5, 1)
        );
    }

    public Slot buildPageSlot(int slot, int add){
        return new SimpleFixedSlot(this, slot){
            @Override
            public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
                super.onClick(p, event);
                if(upgrade.getStorage() == null) return;
                int newPage = page + add;
                if(newPage < 0 || newPage >= upgrade.getStorage().length) return;
                load(page);
                open(p);
                CreateSound.sound(Sound.BLOCK_ENDER_CHEST_OPEN, 2f).playFor(p);
            }
        };
    }

    @Override
    public void onInvClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(false);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(false);
    }

    @Override
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        super.onMenuClick(event);

        int slot = event.getSlot();
        int size = inventory.getSize();
        if(slot >= (size-9)){
            event.setCancelled(true);
        }else {
            event.setCancelled(false);
        }
    }
}

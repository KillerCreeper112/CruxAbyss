package killercreepr.cruxabyss.core.menu;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcrafting.api.crafting.CruxCraftingRecipeManager;
import killercreepr.cruxcrafting.api.crafting.crafter.CruxCraftingCrafter;
import killercreepr.cruxcrafting.core.crafting.crafter.SimpleCraftingCrafter;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.ConfigMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import killercreepr.cruxmenus.core.menu.slot.SimpleSlot;
import killercreepr.cruxmenus.core.menu.slot.SimpleTempStoredSlot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbyssOutpostCraftingMenu extends ConfigMenu {
    public AbyssOutpostCraftingMenu(@NotNull MenuHolder holder, @NotNull DataExchange info) {
        this(holder, info, null);
    }

    public AbyssOutpostCraftingMenu(@NotNull MenuHolder holder, @NotNull DataExchange info, @Nullable MergedTagContainer tags) {
        super(holder, info, tags);
        setupSlots();
    }

    protected CruxCraftingCrafter crafter;
    @Override
    public void onRefresh() {
        super.onRefresh();
        crafter = new Crafter(CruxAbyss.inst().getCraftingManager(), inventory);
        setItem(recipes.getIndex(), recipes.getSlottedItemReplacement());
    }

    protected static final int[] matrix = new int[]{
        1, 2, 3,
        10, 11, 12,
        19, 20, 21
    };
    protected static final int resultSlot = 15;
    protected static final int recipesSlot = 9;

    protected final Slot recipes = new SimpleFixedSlot(this, recipesSlot){
        @Nullable
        @Override
        public ItemStack getSlottedItemReplacement() {
            return CruxItem.create(Material.KNOWLEDGE_BOOK)
                .itemName("Recipes")
                .loreFromString(List.of(
                    "",
                    "<gray>The abyss outpost has",
                    "<gray>exclusive crafting recipes",
                    "<gray>that you and your team",
                    "<gray>may use!",
                    "",
                    "<yellow><latinfont:Click to view recipes>"
                ))
                .item();
        }

        @Override
        public void onClick(@NotNull HumanEntity p, @NotNull InventoryClickEvent event) {
            super.onClick(p, event);
            CruxCore.core().cruxMenus().menuRegistry().menuHolders()
                .get(Crux.key("abyss/outpost/crafting_recipe_list"))
                .open(p, DataExchange.single("previous_menu_holder", Holder.direct(holder)));
            CreateSound.sound(Sound.UI_BUTTON_CLICK).playFor(p);
        }
    };
    public void setupSlots(){
        for(int slot : matrix){
            addSlot(buildCraftingSlot(slot));
        }
        addSlot(buildResultSlot(resultSlot));
        addSlot(recipes);
    }

    @Override
    public void onMenuClick(@NotNull InventoryClickEvent event) {
        super.onMenuClick(event);
        crafter.handleCrafting(event);
    }

    @Override
    public void onInvClick(@NotNull InventoryClickEvent event) {
        super.onInvClick(event);
        crafter.handleCrafting(event);

    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        super.onDrag(event);
        crafter.updateCraftingInv();
    }

    public Slot buildResultSlot(int slot){
        return new SimpleSlot(this, slot){
            @Override
            public boolean mayPlace(@NotNull HumanEntity p, @Nullable ItemStack item) {
                return false;
            }

            @Override
            public boolean mayTake(@NotNull HumanEntity p, @Nullable ItemStack item) {
                return false;
            }
        };
    }

    public Slot buildCraftingSlot(int slot){
        return new SimpleTempStoredSlot(this, slot);
    }

    public static class Crafter extends SimpleCraftingCrafter{
        public Crafter(CruxCraftingRecipeManager craftingManager, Inventory inv) {
            super(craftingManager, inv);
        }

        @Override
        public boolean isResultSlot(int slot) {
            return slot == resultSlot;
        }

        @Override
        public void setItem(int slot, ItemStack item) {
            super.setItem(matrix[slot], item);
        }

        @Override
        public ItemStack[] getMatrix() {
            List<ItemStack> list = new ArrayList<>();
            for(int slot : matrix){
                ItemStack item = inv.getItem(slot);
                list.add(item);
            }
            return list.toArray(new ItemStack[0]);
        }

        @Override
        public void setResults(List<ItemStack> list) {
            if(list == null || list.isEmpty()){
                inv.setItem(resultSlot, null);
                return;
            }
            inv.setItem(resultSlot, Crux.handlers().item().update(list.getFirst()));
        }

        @Override
        public List<ItemStack> getResults() {
            ItemStack item = inv.getItem(resultSlot);
            if(CruxItem.isEmpty(item)) return List.of();
            return List.of(item);
        }
    }
}

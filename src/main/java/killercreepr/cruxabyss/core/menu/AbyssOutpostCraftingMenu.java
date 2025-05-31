package killercreepr.cruxabyss.core.menu;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxcrafting.api.crafting.CrafterHolder;
import killercreepr.cruxcrafting.api.crafting.CruxCraftingRecipeManager;
import killercreepr.cruxcrafting.api.crafting.context.CruxIngredientContext;
import killercreepr.cruxcrafting.api.crafting.crafter.CruxCraftingCrafter;
import killercreepr.cruxcrafting.api.crafting.ingredient.CruxRecipeIngredient;
import killercreepr.cruxcrafting.api.crafting.recipe.CruxCraftingRecipe;
import killercreepr.cruxcrafting.core.crafting.crafter.SimpleCraftingCrafter;
import killercreepr.cruxcrafting.core.menu.CraftingRecipeMenuViewer;
import killercreepr.cruxmenus.api.menu.container.MenuContainer;
import killercreepr.cruxmenus.api.menu.holder.MenuHolder;
import killercreepr.cruxmenus.api.menu.slot.Slot;
import killercreepr.cruxmenus.core.menu.ConfigMenu;
import killercreepr.cruxmenus.core.menu.slot.SimpleFixedSlot;
import killercreepr.cruxmenus.core.menu.slot.SimpleSlot;
import killercreepr.cruxmenus.core.menu.slot.SimpleTempStoredSlot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbyssOutpostCraftingMenu extends ConfigMenu implements CrafterHolder.Crafting {
    public AbyssOutpostCraftingMenu(@NotNull MenuHolder holder, @NotNull DataExchange info) {
        this(holder, info, null);
    }

    public AbyssOutpostCraftingMenu(@NotNull MenuHolder holder, @NotNull DataExchange info, @Nullable MergedTagContainer tags) {
        super(holder, info, tags);
        setupSlots();
    }

    @NotNull
    @Override
    public CruxCraftingCrafter getCrafter() {
        return crafter;
    }

    public CruxCraftingRecipe getSelectedRecipe(){
        return info.get("selected_recipe", CruxCraftingRecipe.class);
    }

    public CraftingRecipeMenuViewer buildRecipeViewer(CruxCraftingRecipe recipe){
        return new CraftingRecipeMenuViewer(inventory, recipe){
            @Override
            public void display() {
                for(int slot : matrix){
                    addSlot(buildCraftingSlot(slot));
                }

                this.setIngredients(this.recipe.mapIngredientDisplay(this.craftingWidth(), this.craftingHeight()));
            }

            @Override
            public void setIngredient(int index, CruxRecipeIngredient ingredient) {
                for(HumanEntity viewer : this.inv.getViewers()){
                    int slot = this.calculateSlotFromIngredientIndex(index);
                    CruxIngredientContext ctx = findIngredient(viewer.getInventory().getContents(), ingredient);
                    if(ctx == null) setIngredientDisplay(slot, null);
                    else{
                        ItemStack item = ctx.ingredient();
                        ItemStack clone = item.clone();
                        ingredient.removeItem(ctx);
                        int difference = clone.getAmount() - item.getAmount();
                        clone.setAmount(difference);
                        setIngredientDisplay(slot, clone);
                    }
                }
            }
        };
    }

    public CruxIngredientContext findIngredient(ItemStack[] inv, CruxRecipeIngredient ingredient){
        for(ItemStack item : inv){
            if(CruxItem.isEmpty(item)) continue;
            CruxIngredientContext ctx = CruxIngredientContext.ingredientContext(item);
            if(!ingredient.test(ctx)) continue;
            return ctx;
        }
        return null;
    }

    protected CruxCraftingCrafter crafter;
    @Override
    public void onRefresh() {
        super.onRefresh();
        crafter = new Crafter(CruxAbyss.inst().getCraftingManager(), inventory);
        setItem(recipes.getIndex(), recipes.getSlottedItemReplacement());
        refreshSelectedRecipe();
    }

    public void refreshSelectedRecipe(){
        var recipe = getSelectedRecipe();
        if(recipe == null){
            setItem(selectedRecipeSlot, null);
            return;
        }
        CruxItem display = CruxItem.wrap(
            Crux.handlers().item().update(CruxCollection.getFirst(recipe.getDisplayedResultItems()).clone())
        );
        ItemStack item = display.item();
        ItemMeta meta = item.getItemMeta();
        Component name = meta.hasDisplayName() ? meta.displayName() : meta.hasItemName() ? meta.itemName() : Component.translatable(item.getType());
        display.insertLore(0, Component.empty().decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(name));
        display.customName("<!italic><yellow><latinfont:selected recipe>");
        setItem(selectedRecipeSlot, display.item());
    }

    protected static final int[] matrix = new int[]{
        1, 2, 3,
        10, 11, 12,
        19, 20, 21
    };
    protected static final int resultSlot = 15;
    protected static final int selectedRecipeSlot = 17;
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

            info.getOrThrow("menu_container", MenuContainer.class)
                    .addOpenedMenu(
                        CruxCore.core().cruxMenus().menuRegistry().menuHolders()
                            .get(Crux.key("abyss/outpost/crafting_recipe_list"))
                            .open(p, info)
                    );
            CreateSound.sound(Sound.UI_BUTTON_CLICK, 0.2f, 1f).playFor(p);
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

        if(event.getSlot() == selectedRecipeSlot){
            var recipe = getSelectedRecipe();
            if(recipe == null) return;
            var p = event.getWhoClicked();
            var holder = CruxCore.core().cruxMenus().menuRegistry().menuHolders().get(Crux.key("crafting/recipe/view"));
            if(holder != null){
                MenuContainer container = info().get("menu_container", MenuContainer.class);

                var opened = holder.open(p, DataExchange.builder(info)
                    .put("crafting_recipe", recipe)
                    .build()
                );
                if(container != null) container.addOpenedMenu(opened);
                CreateSound.sound(Sound.UI_BUTTON_CLICK, 0.2f, 1f).playFor(p);
            }
            return;
        }

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

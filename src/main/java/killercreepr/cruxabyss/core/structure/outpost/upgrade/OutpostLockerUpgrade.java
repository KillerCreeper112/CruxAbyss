package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveOutpostLockerUpgrade;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class OutpostLockerUpgrade extends AbstractOutpostUpgrade{
    public static final int INVENTORY_SIZE = 36;
    public OutpostLockerUpgrade(Key key) {
        super(key);
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade createStored(@NotNull AbyssOutpostData data, int level) {
        return new ActiveOutpostLockerUpgrade(level, data);
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade createActive(@NotNull ActiveAbyssOutpost outpost, int level) {
        return null;
    }

    public Inventory deserializeInv(FileElement ele, int invIndex){
        if(!(ele instanceof FileArray a)) return null;
        Inventory inv = Crux.getServer().createInventory(null, INVENTORY_SIZE, Component.text("Outpost Locker #" + invIndex+1));
        int index = 0;
        for (FileElement ee : a) {
            String itemBase = ee.getAsString();
            ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(itemBase));
            inv.setItem(index, item);
            index++;
        }
        return inv;
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade deserialize(@NotNull AbyssOutpostData data, int level, @NotNull FileContext<?> ctx, @Nullable FileElement e) {
        ActiveOutpostLockerUpgrade upgrade = new ActiveOutpostLockerUpgrade(level, data);
        if(!(e instanceof FileObject o)) return upgrade;
        if(o.get("storage") instanceof FileArray a){
            List<Inventory> list = new ArrayList<>();
            int index = -1;
            for (FileElement ele : a) {
                index++;
                var inventory = deserializeInv(ele, index);
                if(inventory == null){
                    continue;
                }
                list.add(inventory);
            }
            upgrade.setStorage(list.toArray(new Inventory[0]));
        }
        return upgrade;
    }
}

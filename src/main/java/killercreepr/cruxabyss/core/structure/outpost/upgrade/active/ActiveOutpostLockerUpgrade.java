package killercreepr.cruxabyss.core.structure.outpost.upgrade.active;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.menu.OutpostLockerMenu;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.OutpostLockerUpgrade;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ActiveOutpostLockerUpgrade extends SimpleActiveOutpostUpgrade {
    protected final AbyssOutpostData data;
    public ActiveOutpostLockerUpgrade(int level, AbyssOutpostData data) {
        super(level);
        this.data = data;
        updateStorage(level);
    }

    protected Inventory[] storage;

    public Inventory[] getStorage() {
        return storage;
    }

    public void setStorage(Inventory[] storage){
        this.storage = storage;
    }

    public boolean openStorage(HumanEntity entity){
        if(storage ==null) return false;
        OutpostLockerMenu menu = new OutpostLockerMenu(this);
        menu.load();
        return !menu.open(entity).isCancelled();
    }

    @Override
    public void tick(int tick, int rate) {}

    public void updateStorage(int level){
        List<Inventory> newStorage = storage == null ? new ArrayList<>() :  new ArrayList<>(Arrays.asList(storage));
        int levels = getStorageLevels(level);
        if(levels == newStorage.size()) return;

        if(newStorage.size() > levels){
            while(newStorage.size() > levels && !newStorage.isEmpty()){
                newStorage.removeLast();
            }
            setStorage(newStorage.toArray(new Inventory[0]));
            return;
        }
        while(newStorage.size() < levels){
            newStorage.add(Crux.getServer().createInventory(null, OutpostLockerUpgrade.INVENTORY_SIZE, Component.text("Outpost Locker #" + (newStorage.size()+1))));
        }
        setStorage(newStorage.toArray(new Inventory[0]));
    }

    @Override
    public void setLevel(int level) {
        if(this.level == level) return;
        super.setLevel(level);
        updateStorage(level);
    }

    public int getStorageLevels(int level){
        return level;
    }

    public FileElement serializeInventory(Inventory inv){
        FileArray a = new FileArray();
        int size = inv.getSize();
        int start = size-9;
        for(int i = 0; i < size; i++){
            if(i >= start) break;
            ItemStack item = inv.getItem(i);
            if(CruxItem.isEmpty(item)) continue;
            a.add(Base64.getEncoder().encodeToString(item.serializeAsBytes()));
        }
        return a;
    }

    @Nullable
    @Override
    public FileElement serialize(@NotNull FileContext<?> ctx) {
        FileObject o = new FileObject();
        if(storage != null){
            FileArray aInvs = new FileArray(storage.length);
            for(Inventory inv : storage){
                aInvs.add(serializeInventory(inv));
            }
            o.add("storage", aInvs);
        }
        return o;
    }
}

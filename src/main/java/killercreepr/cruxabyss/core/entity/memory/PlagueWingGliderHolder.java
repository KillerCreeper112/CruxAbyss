package killercreepr.cruxabyss.core.entity.memory;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.util.CruxEntityUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class PlagueWingGliderHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("plague_wing_glider");
    protected final ItemStack item;

    public PlagueWingGliderHolder(@NotNull Key key, @NotNull EntityMemory parent, ItemStack item) {
        super(key, parent);
        this.item = item;
    }
    public PlagueWingGliderHolder(@NotNull EntityMemory parent, ItemStack item) {
        this(KEY, parent, item);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        return super.shouldRemoveFromMemory(e) || remove;
    }

    protected boolean remove = false;

    protected Entity entity;
    public void onDismount(){
        remove = true;
        Entity glide = parent.value();
        if(glide == null) return;
        Crux.scheduler().runTask(() ->{
            glide.remove();
            if(CruxEntityUtil.isValid(entity) && entity instanceof HumanEntity p){
                CruxEntityUtil.giveOrDrop(p, item);
            }else{
                glide.getWorld().dropItem(glide.getLocation(), item);
            }
            CreateSound.sound(Sound.ENTITY_WITHER_SHOOT, 2f).playAt(glide);
            CreateSound.sound(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.5f).playAt(glide);
        });
    }

    public void onLand(){
        onDismount();
    }

    public Entity getDriver(Entity e){
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(e);
        if(modeled == null) return e.getPassengers().isEmpty() ? null : e.getPassengers().getFirst();
        return modeled.getMountData().getMainMountManager().getDriver();
    }

    @Override
    public void tick(@NotNull Entity e) {
        Entity driver = getDriver(e);
        if(driver == null || (entity != null && !driver.equals(entity))){
            onDismount();
            return;
        }
        if(entity == null){
            entity = driver;
        }
    }
}

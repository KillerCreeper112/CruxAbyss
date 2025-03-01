package killercreepr.cruxabyss.core.entity.memory;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxabyss.core.lang.Lang;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlagueWingGliderHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("plague_wing_glider");
    protected final ItemStack item;
    protected final int itemDamage;

    public PlagueWingGliderHolder(@NotNull Key key, @NotNull EntityMemory parent, ItemStack item, int itemDamage) {
        super(key, parent);
        this.item = item;
        this.itemDamage = itemDamage;
    }
    public PlagueWingGliderHolder(@NotNull EntityMemory parent, ItemStack item, int itemDamage) {
        this(KEY, parent, item, itemDamage);
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
            if(!CruxItem.isEmpty(item)){
                if(CruxEntityUtil.isValid(entity) && entity instanceof HumanEntity p){
                    CruxEntityUtil.giveOrDrop(p, item);
                }else{
                    glide.getWorld().dropItem(glide.getLocation(), item);
                }
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

    public void itemDamageTick(Entity e){
        if(itemDamage < 1 || tick % 20 != 0) return;

        int maxDamage = CruxItem.getMaxDurability(item);
        if(maxDamage < 1) return;

        Crux.handlers().item().damageItem(item, itemDamage, null);
        Integer dmg = item.getData(DataComponentTypes.DAMAGE);
        if(dmg == null) return;

        if(dmg < maxDamage){
            warnTick(dmg, maxDamage);
            return;
        }
        onDismount();
        CreateSound.sound(Sound.ENTITY_ITEM_BREAK).playAt(e);
    }

    public void warnTick(int dmg, int maxDamage){
        if(entity == null) return;
        int difference = maxDamage - dmg;
        if(difference > 10) return;
        Lang.PLAGUE_WING_DURABILITY_WARNING.use(
            entity, TagContainer.merged().add(Tag.parsed("durability", difference + ""))
        );
    }

    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity e) {
        tick++;
        Entity driver = getDriver(e);
        if(driver == null || (entity != null && !driver.equals(entity))){
            onDismount();
            return;
        }
        if(entity == null){
            entity = driver;
        }
        itemDamageTick(e);
    }
}

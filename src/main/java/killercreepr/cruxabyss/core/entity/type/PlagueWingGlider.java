package killercreepr.cruxabyss.core.entity.type;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.component.impl.PlagueWingGliderComponent;
import killercreepr.cruxabyss.core.entity.memory.PlagueWingGliderHolder;
import killercreepr.cruxabyss.core.modelengine.mount.controller.AbyssMountControllerTypes;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlagueWingGlider extends SimpleCruxMob {
    public PlagueWingGlider() {
        super(Crux.key("plague_wing_glider"));
    }

    public Entity createAndMountGlider(Player p, PlagueWingGliderComponent data, ItemStack item){
        Entity glider = spawn(p.getLocation());
        if(glider instanceof LivingEntity d){
            if(data.getGliderPotions() != null){
                d.addPotionEffects(data.getGliderPotions());
            }
        }
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(glider);
        modeled.getMountData().getMainMountManager().setCanDrive(true);
        modeled.getMountData().getMainMountManager().setCanRide(true);

        PlagueWingGliderHolder holder = EntityMemory.getOrCreateDataHolder(glider, PlagueWingGliderHolder.class, mem ->
            new PlagueWingGliderHolder(mem, item, data.getItemDamagePerSecond()));

        modeled.getMountData().getMainMountManager().mountDriver(
            p, AbyssMountControllerTypes.plagueWingGlider(holder, data.getMoveSpeed())
        );
        return glider;
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.OBJECT};
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(m).setBaseEntityVisible(false).getOrAddModelAsync("plague_wing_glider").whenComplete(
            (mod, throwable) ->{
                mod.getAnimationHandler().playAnimation("falling", 0D, 0D, 1D, true);
            }
        );
        Bukkit.getMobGoals().removeAllGoals(m);
        /*CruxGoalUtil.addIfNotPresent(m, AbyssReturnPortalGoal.class, 0, () ->{
            Bukkit.getMobGoals().removeAllGoals(m);
            return new AbyssReturnPortalGoal(m).model(model);
        });*/
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            if(e instanceof Mob mob){
                mob.setSilent(true);
                mob.setCollidable(false);
                //mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0D);
            }
            if(consumer != null) consumer.accept(e);
            load(e);
        });
    }
}

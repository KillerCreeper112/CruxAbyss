package killercreepr.cruxabyss.core.entity.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.persistence.CruxPersistence;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.entity.goal.AbyssReturnPortalGoal;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AbyssReturnPortal extends SimpleCruxMob {
    public AbyssReturnPortal() {
        super(Crux.key("abyss_return_portal"));
    }

    public @NotNull Entity spawn(@NotNull Location at, @NotNull Location returnTo){
        return spawn(at, x ->{
            CruxTag.set(x, "return_to", CruxPersistence.LOCATION, returnTo);
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.OBJECT};
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CompletableFuture<ActiveModel> model = new DesignEntity(m).getOrAddModelAsync("portal_rift");
        CruxGoalUtil.addIfNotPresent(m, AbyssReturnPortalGoal.class, 0, () ->{
            Bukkit.getMobGoals().removeAllGoals(m);
            return new AbyssReturnPortalGoal(m).model(model);
        });
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            ModelEntity model = new ModelEntity(e, "portal_rift");
            //new java.awt.Color(0x21D000);
            model.getModel().setDefaultTint(Color.fromRGB(0x28FF00));
            model.getModel().setDamageTint(Color.fromRGB(0x21D000));
            model.playAnimation("spawn", true);
            model.playAnimation("portal1_loop", true);
            model.playAnimation("portal2_loop", true);
            model.playAnimation("portal3_loop", true);
            model.playAnimation("portal4_loop", true);
            e.setGravity(false);
            e.setInvulnerable(true);
            if(e instanceof Mob mob){
                mob.setSilent(true);
                mob.setCollidable(false);
                mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0D);
            }
            if(consumer != null) consumer.accept(e);
            load(e);
        });
    }
}

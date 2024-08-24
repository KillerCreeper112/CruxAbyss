package killercreepr.cruxabyss.entity.type;

import killercreepr.crux.Crux;
import killercreepr.crux.persistence.CruxPersistence;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxabyss.entity.goal.AbyssReturnPortalGoal;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        //todo change model ID to new one
        new DesignEntity(m).getModel("abyss_altar_portal").ifPresent(model ->{
            Bukkit.getMobGoals().addGoal(m, 0, new AbyssReturnPortalGoal(m, model));
        });
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            //todo look into spawn animation
            new ModelEntity(e, "abyss_altar_portal")/*.playAnimation("open", 0D, 0D, 1D, true)*/; //todo change model ID to new one
            if(e instanceof Mob mob){
                mob.setSilent(true);
                mob.setCollidable(false);
                mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);
            }
            if(consumer != null) consumer.accept(e);
        });
    }
}

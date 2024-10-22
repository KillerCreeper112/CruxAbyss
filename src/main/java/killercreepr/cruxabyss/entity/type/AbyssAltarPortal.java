package killercreepr.cruxabyss.entity.type;

import killercreepr.crux.Crux;
import killercreepr.cruxabyss.entity.goal.AbyssAltarPortalGoal;
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

import java.util.function.Consumer;

public class AbyssAltarPortal extends SimpleCruxMob {
    public AbyssAltarPortal() {
        super(Crux.key("abyss_altar_portal"));
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        new DesignEntity(m).getModel("portal_rift").ifPresent(model ->{
            Bukkit.getMobGoals().removeAllGoals(m);
            Bukkit.getMobGoals().addGoal(m, 0, new AbyssAltarPortalGoal(m, model));
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.OBJECT};
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            ModelEntity model = new ModelEntity(e, "portal_rift");
            //new java.awt.Color(0xD01F12);
            model.getModel().setDefaultTint(Color.fromRGB(0xFF2816));
            model.getModel().setDamageTint(Color.fromRGB(0xD01F12));
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
                mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);
            }
            //model.getAnimationHandler().playAnimation("open", 0D, 0D, 1D, true);
            if(consumer != null) consumer.accept(e);
            load(e);
        });
    }
}

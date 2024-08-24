package killercreepr.cruxabyss.entity.type;

import killercreepr.crux.Crux;
import killercreepr.cruxabyss.entity.mob.goal.AbyssCrystalGoal;
import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;

public class AbyssCrystal extends GenericCruxMob {
    public AbyssCrystal(@NotNull Key key) {
        super(key);
    }

    public AbyssCrystal() {
        super(Crux.key("abyss_crystal"));
    }

    @Override
    protected @NotNull Mob spawnAt(@NotNull Location l) {
        return l.getWorld().spawn(l, Pig.class, e ->{
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);
            new ModelEntity(e, key.value());
            e.setSilent(true);
            e.setGravity(false);
            e.setCollidable(false);
            load(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        new DesignEntity(m).getModel(key.value()).ifPresent(model ->{
            Bukkit.getMobGoals().addGoal(m, 0, new AbyssCrystalGoal(m, model));
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.OBJECT};
    }
}

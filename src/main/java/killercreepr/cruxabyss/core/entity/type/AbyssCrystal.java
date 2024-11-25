package killercreepr.cruxabyss.core.entity.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxabyss.api.entity.AbyssAltarItemEntity;
import killercreepr.cruxabyss.core.entity.goal.AbyssCrystalGoal;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AbyssCrystal extends SimpleCruxMob {
    public AbyssCrystal(@NotNull Key key) {
        super(key);
    }

    public AbyssCrystal() {
        super(Crux.key("abyss_crystal"));
    }

    @Override
    protected @NotNull Mob spawnAt(@NotNull Location l, @Nullable Consumer<Entity> consumer) {
        return l.getWorld().spawn(l, Pig.class, e ->{
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);
            new ModelEntity(e, key.value());
            e.setSilent(true);
            e.setGravity(false);
            e.setCollidable(false);
            e.setInvulnerable(true);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CompletableFuture<ActiveModel> model =  AbyssAltarItemEntity.wrap(e).spin(true).model();
        CruxGoalUtil.addIfNotPresent(m, AbyssCrystalGoal.class, 0, () ->{
            Bukkit.getMobGoals().removeAllGoals(m);
            return new AbyssCrystalGoal(m).model(model);
        });
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.OBJECT};
    }
}

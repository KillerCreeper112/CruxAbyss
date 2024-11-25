package killercreepr.cruxabyss.core.entity.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxabyss.api.entity.AbyssAltarItemEntity;
import killercreepr.cruxabyss.api.entity.type.AltarPlacedItem;
import killercreepr.cruxabyss.core.entity.goal.AbyssAltarPlacedItemGoal;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AbyssAltarPlacedItem extends SimpleCruxMob implements AltarPlacedItem {
    public AbyssAltarPlacedItem(@NotNull Key key) {
        super(key);
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Pig.class, e ->{
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CompletableFuture<ActiveModel> model =  AbyssAltarItemEntity.wrap(e).bob(true).spin(true).model();
        CruxGoalUtil.addIfNotPresent(m, AbyssAltarPlacedItemGoal.class, 0, () ->{
            Bukkit.getMobGoals().removeAllGoals(m);
            return new AbyssAltarPlacedItemGoal(m).model(model);
        });
    }

    @NotNull
    @Override
    public Entity place(@NotNull Location location, ItemStack display, @Nullable Consumer<Entity> consumer) {
        return spawnAt(location, e ->{
            AbyssAltarItemEntity.wrap(e).display(display);
            if(consumer != null) consumer.accept(e);
        });
    }
}

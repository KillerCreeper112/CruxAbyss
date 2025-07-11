package killercreepr.cruxabyss.core.entity.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.altar.AltarEntity;
import killercreepr.cruxabyss.api.altar.AltarEntityType;
import killercreepr.cruxabyss.api.entity.AbyssAltarItemEntity;
import killercreepr.cruxabyss.api.entity.type.AltarPlacedItem;
import killercreepr.cruxabyss.core.entity.altar.AltarItemEntity;
import killercreepr.cruxabyss.core.entity.goal.AbyssCrystalGoal;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AbyssCrystal extends SimpleCruxMob implements AltarPlacedItem, AltarEntityType {
    public AbyssCrystal(@NotNull Key key) {
        super(key);
    }

    public AbyssCrystal() {
        super(Crux.key("abyss_crystal"));
    }

    @Override
    protected @NotNull Mob spawnAt(@NotNull Location l, @Nullable Consumer<Entity> consumer) {
        return l.getWorld().spawn(l, Pig.class, e ->{
            e.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0D);
            //new ModelEntity(e, key.value());
            e.setSilent(true);
            e.setGravity(false);
            e.setCollidable(false);
            //e.setInvulnerable(true);
            CruxEntity.entity(e).set(CruxComponents.INVULNERABLE, true);
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
    public Set<MobCategory> getCategories() {
        return Set.of(MobCategory.OBJECT);
    }

    @NotNull
    @Override
    public Entity place(@NotNull Location location, AbyssAltar altar, ItemStack display, @Nullable Consumer<Entity> consumer) {
        return spawn(location, e ->{
            if((e instanceof Mob mob)){
                AbyssCrystalGoal goal = CruxGoalUtil.getGoal(mob, AbyssCrystalGoal.class);
                if(goal != null){
                    goal.setAltar(altar);
                    goal.setItem(display);
                }
            }
            if(consumer != null) consumer.accept(e);
        });
    }

    @NotNull
    @Override
    public AltarEntity createAltarEntity(@NotNull AbyssAltar altar, @NotNull Entity from) {
        return new AltarItemEntity(from);
    }
}

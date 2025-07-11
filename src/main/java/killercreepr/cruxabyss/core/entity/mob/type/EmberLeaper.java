package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.EmberLeaperGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EmberLeaper extends SimpleAbyssMob {
    public EmberLeaper() {
        super(Crux.key("ember_leaper"), EntityType.PIG);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Ember Leaper"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            if(e instanceof LivingEntity ee){
                ee.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(0D);
                ee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(30D);
                ee.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1D);
                ee.setHealth(ee.getAttribute(Attribute.MAX_HEALTH).getValue());
                ee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(-99D);
                ee.getAttribute(Attribute.SAFE_FALL_DISTANCE).setBaseValue(1.5D);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        return null;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new ModelEntity(e).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        EmberLeaperGoal goal = new EmberLeaperGoal(e);
        goal.model(active);
        goal.updateSize();
        return goal;
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(MobCategory.MONSTER, MobCategory.ENEMY, AbyssMobCategory.ABYSSAL);
    }
}

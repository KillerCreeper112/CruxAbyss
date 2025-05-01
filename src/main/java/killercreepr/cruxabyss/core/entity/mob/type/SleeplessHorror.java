package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.AbyssalHuskGoal;
import killercreepr.cruxabyss.core.entity.mob.goal.SleeplessHorrorGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SleeplessHorror extends SimpleAbyssMob {

    public SleeplessHorror() {
        super(Crux.key("sleepless_horror"), EntityType.PIG);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Sleepless Horror"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            if(e instanceof Zombie z){
                z.setShouldBurnInDay(false);
            }

            if(e instanceof LivingEntity ee){
                double movement = ee.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.3D;
                ee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movement);
                ee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(CruxMath.random(30D, 50D));
                ee.setHealth(ee.getAttribute(Attribute.MAX_HEALTH).getValue());
                CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(movement));
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(7D * (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.3D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-9));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(2D));
        return map;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new ModelEntity(e).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        return new SleeplessHorrorGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY, AbyssMobCategory.ABYSSAL};
    }
}

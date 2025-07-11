package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.PlagueTyrantGoal;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlagueTyrant extends SimpleAbyssMob {
    public PlagueTyrant() {
        super(Crux.key("plague_tyrant"), EntityType.PIG);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Plague Tyrant"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            if(e instanceof LivingEntity ee){
                ee.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(2.5D);
                ee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(200D);
                ee.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(.9D);
                ee.setHealth(ee.getAttribute(Attribute.MAX_HEALTH).getValue());

                CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED,
                    CruxAttributeModifier.baseModifier(ee.getAttribute(Attribute.MOVEMENT_SPEED).getValue()*1.2D));
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(20D *
                         (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-15));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(25));
        addAttribute(map, CruxAttribute.ARMOR, CruxAttributeModifier.baseModifier(8D));
        addAttribute(map, CruxAttribute.ARMOR_TOUGHNESS, CruxAttributeModifier.baseModifier(4D));
        addAttribute(map, CruxAttribute.SHIELD_PIERCING, CruxAttributeModifier.baseModifier(90D));
        return map;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);

        if(!CruxAttribute.hasAttributeData(mob, CruxAttribute.ATTACK_RANGE)){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
                mob.getWidth() + 1.6D
            ));
        }
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new ModelEntity(e).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(e, active);
        return new PlagueTyrantGoal(e).model(active);
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(MobCategory.MONSTER, MobCategory.ENEMY, AbyssMobCategory.ABYSSAL, AbyssMobCategory.ABYSS_OUTPOST);
    }
}

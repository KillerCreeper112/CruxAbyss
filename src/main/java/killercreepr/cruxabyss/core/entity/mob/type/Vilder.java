package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.vilder.VilderGoal;
import killercreepr.cruxabyss.core.entity.mob.goal.vilder.VilderMutation1Goal;
import killercreepr.cruxabyss.core.entity.mob.goal.vilder.VilderMutation2Goal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class Vilder extends SimpleAbyssMob {
    public static final Map<String, Function<Mob, CruxMobModeledGoal>> TYPES = Map.of(
        "vilder", VilderGoal::new,
        "vilder_2", VilderMutation1Goal::new,
        "vilder_3", VilderMutation2Goal::new
    );

    public Vilder() {
        super(Crux.key("vilder"), EntityType.VILLAGER);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Vilder"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            if(e instanceof LivingEntity ee){
                double movement = ee.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * .7D;
                ee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movement);
                ee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(CruxMath.random(40D, 70D));
                ee.setHealth(ee.getAttribute(Attribute.MAX_HEALTH).getValue());
                CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(movement));
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(12D * (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-9));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11));
        addAttribute(map, CruxAttribute.ARMOR, CruxAttributeModifier.baseModifier(4D));
        addAttribute(map, CruxAttribute.ARMOR_TOUGHNESS, CruxAttributeModifier.baseModifier(2D));
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
                mob.getWidth() + .9D
            ));
        }
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        String typeID = CruxTag.get(e, "vilder_type", PersistentDataType.STRING, null);
        if(typeID == null){
            typeID = CruxCollection.getRandom(TYPES.keySet());
            CruxTag.set(e, "vilder_type", PersistentDataType.STRING, typeID);
        }

        CompletableFuture<ActiveModel> active = new ModelEntity(e)
            .setBaseEntityVisible(false)
            .getOrAddModelAsync(typeID);
        applyWhenCompleteModel(e, active);

        return TYPES.get(typeID).apply(e).model(active);
    }

    @Override
    public Set<MobCategory> getCategories() {
        return Set.of(MobCategory.NEUTRAL, AbyssMobCategory.ABYSSAL, AbyssMobCategory.ABYSS_SAFEZONE);
    }
}

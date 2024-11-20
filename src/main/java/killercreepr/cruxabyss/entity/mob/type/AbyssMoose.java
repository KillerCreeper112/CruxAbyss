package killercreepr.cruxabyss.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.entity.mob.goal.MooseGoal;
import killercreepr.cruxabyss.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AbyssMoose extends SimpleAbyssMob {
    public AbyssMoose() {
        super(Crux.key("moose"), EntityType.LLAMA);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Moose"));
            e.setCustomNameVisible(false);
            Consumer<Entity> s = super.spawnFunction(world, l);
            if(s != null) s.accept(e);
            ModeledEntity modeled = new ModelEntity(e, key.value()).getOrCreateModeledEntity();
            modeled.setBaseEntityVisible(false);

            if(e instanceof Mob mob){
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30D);
                mob.getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(1D);
                mob.setHealth(30D);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        int wave = world == null ? 1 : world.getWave();
        float difficulty = world == null ? 1f : world.getDifficulty();
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
            CruxAttributeModifier.baseModifier(CruxMath.random(8D, 10D) * (wave * .1D) * difficulty));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.35D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-6));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(25));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK_UP, CruxAttributeModifier.baseModifier(10));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(2D));
        addAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, CruxAttributeModifier.baseModifier(15));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> model = new DesignEntity(e).getOrAddModelAsync(key.value());
        return new MooseGoal(e).model(model);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.ANIMAL};
    }
}

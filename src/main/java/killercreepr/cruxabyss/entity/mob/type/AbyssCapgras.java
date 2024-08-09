package killercreepr.cruxabyss.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.entity.mob.goal.CapgrasGoal;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AbyssCapgras extends SimpleAbyssMob {
    public AbyssCapgras() {
        super(Crux.key("capgras"), EntityType.PIG);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l) {
        return e ->{
            new ModelEntity(e, key.value()).getOrCreateModeledEntity().setBaseEntityVisible(false);
            if(e instanceof Mob mob){
                mob.setSilent(true);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable GameManager game, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                new CruxAttributeModifier(CruxMath.random(4D, 6D) *
                        (game == null ? 1D : game.getWave() * .1D) * (game == null ? 1D : game.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(.35D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-7));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(20));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(2.2D));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        ActiveModel active = new DesignEntity(e).getModel(key.value()).orElse(null);
        if(active != null) return new CapgrasGoal(e, active);
        return null;
    }

    @Override
    public MobCategory [] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER};
    }
}

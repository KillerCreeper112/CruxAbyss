package killercreepr.cruxabyss.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.entity.mob.goal.CharredBonesGoal;
import killercreepr.cruxabyss.world.abyss.AbyssWorld;
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
import org.bukkit.entity.Skeleton;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class AbyssCharredBones extends SimpleAbyssMob {
    public AbyssCharredBones() {
        super(Crux.key("charred_bones"), EntityType.SKELETON);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            new ModelEntity(e, key.value()).getOrCreateModeledEntity().setBaseEntityVisible(false);

            if(e instanceof Skeleton x) x.setShouldBurnInDay(false);
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = super.getAttributes(world, e);
        setAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                new CruxAttributeModifier(CruxMath.random(8D, 14D) *
                        (world == null ? 1D : world.getWave() * .1D) * (world == null ? 1D : world.getDifficulty())));
        setAttribute(map, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-15));
        setAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(36));
        setAttribute(map, CruxAttribute.ATTACK_KNOCKBACK_UP, new CruxAttributeModifier(12));
        setAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, new CruxAttributeModifier(6));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        ActiveModel active = new DesignEntity(e).getModel(key.value()).orElse(null);
        if(active != null) return new CharredBonesGoal(e, active);
        return null;
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.UNDEAD};
    }
}

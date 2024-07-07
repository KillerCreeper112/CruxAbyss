package killercreepr.cruxabyss.entity.mob.type;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
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
    public @Nullable Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l) {
        return e ->{
            /*todo ModeledEntity modeled = ModelEngineAPI.createModeledEntity(e);
            modeled.setBaseEntityVisible(false);
            ActiveModel model = ModelEngineAPI.createActiveModel(key.getKey());
            modeled.addModel(model, true);*/

            if(e instanceof Skeleton x) x.setShouldBurnInDay(false);
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable GameManager game, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = super.getAttributes(game, e);
        setAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                new CruxAttributeModifier(CruxMath.random(8D, 14D) *
                        (game == null ? 1D : game.getWave() * .1D) * (game == null ? 1D : game.getDifficulty())));
        setAttribute(map, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-15));
        setAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(36));
        setAttribute(map, CruxAttribute.ATTACK_KNOCKBACK_UP, new CruxAttributeModifier(12));
        setAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, new CruxAttributeModifier(6));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        /*todo ActiveModel active = activeModel(e, key.getKey());
        if(active != null) return new CharredBonesGoal(e, active);*/
        return null;
    }
}

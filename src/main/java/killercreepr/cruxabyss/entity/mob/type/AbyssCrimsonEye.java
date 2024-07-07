package killercreepr.cruxabyss.entity.mob.type;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AbyssCrimsonEye extends SimpleAbyssMob {
    public AbyssCrimsonEye() {
        super(Crux.key("crimson_eye"), EntityType.HUSK);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l) {
        return e ->{
            CruxTag.set(e, "hide", PersistentDataType.INTEGER, 1);
            /*todo ModeledEntity modeled = ModelEngineAPI.createModeledEntity(e);
            modeled.setBaseEntityVisible(false);
            ActiveModel model = ModelEngineAPI.createActiveModel(key.getKey());
            modeled.addModel(model, true);*/
            if(e instanceof Mob mob){
                mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(-999D);
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
        addAttribute(map, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-5));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(20));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(2.2D));
        addAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, new CruxAttributeModifier(9999));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        /*todo ActiveModel active = activeModel(e, key.getKey());
        if(active != null) return new CrimsonEyeGoal(e, active);*/
        return null;
    }
}

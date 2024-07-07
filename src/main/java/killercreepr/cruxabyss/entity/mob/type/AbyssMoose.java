package killercreepr.cruxabyss.entity.mob.type;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AbyssMoose extends SimpleAbyssMob {
    public AbyssMoose() {
        super(Crux.key("moose"), EntityType.LLAMA);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l) {
        return e ->{
            Consumer<Entity> s = super.spawnFunction(game, l);
            if(s != null) s.accept(e);
            /*todo ModeledEntity modeled = ModelEngineAPI.createModeledEntity(e);
            modeled.setBaseEntityVisible(false);
            ActiveModel model = ModelEngineAPI.createActiveModel(key.getKey());
            modeled.addModel(model, true);
            modeled.setStepHeight(1D);*/

            if(e instanceof Mob mob){
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30D);
                mob.setHealth(30D);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable GameManager game, @NotNull Entity e) {
        int wave = game == null ? 1 : game.getWave();
        float difficulty = game == null ? 1f : game.getDifficulty();
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                new CruxAttributeModifier(CruxMath.random(8D, 10D) * (wave * .1D) * difficulty));
        addAttribute(map, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(.35D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-6));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(25));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK_UP, new CruxAttributeModifier(10));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(2D));
        addAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, new CruxAttributeModifier(15));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        /*todo ActiveModel active = activeModel(e, key.getKey());
        if(active != null) return new MooseGoal(e, active);*/
        return null;
    }
}

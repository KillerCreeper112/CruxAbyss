package killercreepr.cruxabyss.entity.type;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.Crux;
import killercreepr.cruxabyss.entity.goal.AbyssAltarPortalGoal;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AbyssAltarPortal extends SimpleAbyssMob {
    public AbyssAltarPortal() {
        super(Crux.key("abyss_altar_portal"), EntityType.PIG);
    }

    @Nullable
    @Override
    public Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l) {
        return e ->{
            ModeledEntity modeled = ModelEngineAPI.createModeledEntity(e);
            modeled.setBaseEntityVisible(false);
            ActiveModel model = ModelEngineAPI.createActiveModel(key.key().value());
            modeled.addModel(model, true);
            if(e instanceof Mob mob){
                mob.setSilent(true);
                mob.setCollidable(false);
                mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);
            }
            model.getAnimationHandler().playAnimation("open", 0D, 0D, 1D, true);
        };
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(e);
        ActiveModel active = modeled.getModel(key.value()).orElse(null);
        if(active != null) return new AbyssAltarPortalGoal(e, active);
        return null;
    }
}

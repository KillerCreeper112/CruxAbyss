package killercreepr.cruxabyss.entity.type;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.Crux;
import killercreepr.crux.persistence.CruxPersistence;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxabyss.entity.goal.AbyssReturnPortalGoal;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AbyssReturnPortal extends SimpleAbyssMob {
    public AbyssReturnPortal() {
        super(Crux.key("abyss_return_portal"), EntityType.PIG);
    }

    @Nullable
    @Override
    public Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l) {
        return e ->{
            ModeledEntity modeled = ModelEngineAPI.getOrCreateModeledEntity(e);
            modeled.setBaseEntityVisible(false);
            ActiveModel model = ModelEngineAPI.createActiveModel("abyss_altar_portal");
            modeled.addModel(model, true);
            if(e instanceof Mob mob){
                mob.setSilent(true);
                mob.setCollidable(false);
                mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0D);
            }
            model.getAnimationHandler().playAnimation("open", 0D, 0D, 1D, true);
        };
    }

    public @NotNull Entity spawn(@NotNull Location at, @NotNull Location returnTo){
        return spawnAt(null, at, x ->{
            CruxTag.set(x, "return_to", CruxPersistence.LOCATION, returnTo);
        });
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        ModeledEntity modeled = ModelEngineAPI.getOrCreateModeledEntity(e);
        ActiveModel active = modeled.getModel("abyss_altar_portal").orElse(null);
        if(active != null) return new AbyssReturnPortalGoal(e, active);
        return null;
    }
}

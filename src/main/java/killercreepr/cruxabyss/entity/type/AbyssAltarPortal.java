package killercreepr.cruxabyss.entity.type;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import killercreepr.crux.Crux;
import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarPortal extends GenericCruxMob {
    public static final AbyssAltarPortal TESTBOI = CruxEntityRegistries.ENTITIES.register(new AbyssAltarPortal());
    public AbyssAltarPortal() {
        super(Crux.key("abyss_altar_portal"));
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location spawn) {
        return spawn.getWorld().spawn(spawn, Pig.class, e ->{
            ModeledEntity modeled = ModelEngineAPI.createModeledEntity(e);
            modeled.setBaseEntityVisible(false);
            ActiveModel model = ModelEngineAPI.createActiveModel(key.key().value());
            modeled.addModel(model, true);
            if(e instanceof Mob mob){
                mob.setSilent(true);
                mob.setAware(false);
                mob.setCollidable(false);
            }
        });
    }
}

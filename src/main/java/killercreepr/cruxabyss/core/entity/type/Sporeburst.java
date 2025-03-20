package killercreepr.cruxabyss.core.entity.type;

import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.component.impl.SporeburstChargeComponent;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.core.component.CruxAttributeComponents;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrowableProjectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Sporeburst extends SimpleCruxMob {
    public Sporeburst(@NotNull Key key) {
        super(key);
    }

    public Sporeburst() {
        super(Crux.key("sporeburst"));
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Snowball.class, e ->{
            e.setSilent(true);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    public @NotNull Entity throwBurst(SporeburstChargeComponent comp, @NotNull CruxItem item,
                                      @NotNull Location location, @Nullable Consumer<ThrowableProjectile> consumer){
        return location.getWorld().spawn(location, Snowball.class, e ->{
            CruxEntity crux = CruxEntity.entity(e);
            item.forEachOrDefaultData(data ->{
                if(data.getType() == CruxAttributeComponents.STORED_CRUX_ATTRIBUTES){
                    crux.set(CruxAttributeComponents.CRUX_ATTRIBUTES, (CruxAttributeContainer) data.getValue());
                    return;
                }
                crux.set(data);
            });
            //crux.set(AbyssComponents.SPOREBURST_CHARGE, comp);
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }
}

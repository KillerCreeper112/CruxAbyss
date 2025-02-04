package killercreepr.cruxabyss.core.entity.mob.type;

import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxentities.entity.CruxMob;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PlaguewingMounted implements CruxMob {
    protected final Key key;
    protected final CruxMob passenger;
    public PlaguewingMounted(@NotNull Key key, CruxMob passenger) {
        this.key = key;
        this.passenger = passenger;
    }
    @NotNull
    @Override
    public Entity spawn(@NotNull Location at, @Nullable Consumer<Entity> consumer) {
        Entity plagueWing = AbyssMob.PLAGUEWING.spawn(at, consumer);
        Entity spawned = passenger.spawn(at);
        plagueWing.addPassenger(spawned);
        return plagueWing;
    }

    @NotNull
    @Override
    public Key key() {
        return key;
    }
}

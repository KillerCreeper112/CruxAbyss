package killercreepr.cruxabyss.core.game.entity;

import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class MobWave {
    protected final NaturalEntitySpawnGroup spawns;
    protected final NumberProvider rolls;

    public MobWave(NaturalEntitySpawnGroup spawns, NumberProvider rolls) {
        this.spawns = spawns;
        this.rolls = rolls;
    }

    public Collection<Entity> spawn(LocationHolder mobSpawnHolder, Consumer<Entity> consumer){
        Collection<Entity> list = new HashSet<>();
        Location mobSpawn = mobSpawnHolder.value();
        SpawnContext ctx = SpawnContext.simple(mobSpawn.getBlock(), CruxMath.random());
        spawns.selectRandom(rolls.value().intValue(), ctx).forEach(e ->{
            Entity spawned = e.spawn(ctx);
            if(spawned == null) return;
            consumer.accept(spawned);
        });
        return list;
    }
}

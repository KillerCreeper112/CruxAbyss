package killercreepr.cruxabyss.core.game.entity;

import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawner;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class MobWave {
    protected final NaturalEntitySpawnGroup spawns;
    protected final List<NaturalEntitySpawnGroup> one_time_spawns;
    protected final NumberProvider mob_rolls;
    protected final NumberProvider mob_group_rolls;

    public MobWave(NaturalEntitySpawnGroup spawns, List<NaturalEntitySpawnGroup> oneTimeSpawns, NumberProvider mob_rolls, NumberProvider mobGroupRolls) {
        this.spawns = spawns;
        one_time_spawns = oneTimeSpawns;
        this.mob_rolls = mob_rolls;
        mob_group_rolls = mobGroupRolls;
    }

    public Collection<Entity> spawn(LocationHolder mobSpawnHolder, Consumer<Entity> consumer){
        Collection<Entity> list = new HashSet<>();

        if(one_time_spawns != null){
            one_time_spawns.forEach(group ->{
                Location mobSpawn = mobSpawnHolder.value();
                SpawnContext ctx = SpawnContext.simple(mobSpawn.getBlock(), CruxMath.random());
                NaturalEntitySpawner.spawn(
                    spawns.selectRandom(1, ctx), ctx
                ).forEach(e ->{
                    list.add(e);
                    if(consumer != null) consumer.accept(e);
                });
            });
        }

        int amount = mob_rolls.value().intValue();
        while(amount > 0){
            amount--;
            Location mobSpawn = mobSpawnHolder.value();
            SpawnContext ctx = SpawnContext.simple(mobSpawn.getBlock(), CruxMath.random());
            NaturalEntitySpawner.spawn(
                spawns.selectRandom(mob_group_rolls.value().intValue(), ctx), ctx
            ).forEach(e ->{
                list.add(e);
                if(consumer != null) consumer.accept(e);
            });
        }

        return list;
    }
}

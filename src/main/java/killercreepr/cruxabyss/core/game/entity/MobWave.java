package killercreepr.cruxabyss.core.game.entity;

import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawn;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawner;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.function.Consumer;

public class MobWave {
    public final NaturalEntitySpawnGroup spawns;
    public final List<NaturalEntitySpawnGroup> one_time_spawns;
    public final NumberProvider mob_rolls;
    public final NumberProvider mob_group_rolls;

    public MobWave(NaturalEntitySpawnGroup spawns, List<NaturalEntitySpawnGroup> oneTimeSpawns, NumberProvider mob_rolls, NumberProvider mobGroupRolls) {
        this.spawns = spawns;
        one_time_spawns = oneTimeSpawns;
        this.mob_rolls = mob_rolls;
        mob_group_rolls = mobGroupRolls;
    }

    public IteratorSpawner iteratorSpawner(LocationHolder mobSpawnHolder, Consumer<Entity> consumer){
        return new IteratorSpawner(listSpawns(), mobSpawnHolder, consumer, mob_group_rolls);
    }

    public List<NaturalEntitySpawnGroup> listSpawns(){
        List<NaturalEntitySpawnGroup> list = new ArrayList<>();

        if(one_time_spawns != null) list.addAll(one_time_spawns);

        int amount = mob_rolls.value().intValue();
        while(amount > 0){
            amount--;
            list.add(spawns);
        }

        return list;
    }

    public Collection<Entity> spawn(LocationHolder mobSpawnHolder, Consumer<Entity> consumer){
        Collection<Entity> list = new HashSet<>();

        if(one_time_spawns != null){
            one_time_spawns.forEach(group ->{
                Location mobSpawn = mobSpawnHolder.value();
                if(mobSpawn == null) return;
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
            if(mobSpawn == null) continue;
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

    public static class IteratorSpawner{
        protected final List<NaturalEntitySpawnGroup> spawns;
        protected final LocationHolder mobSpawnHolder;
        protected final Consumer<Entity> consumer;
        public final NumberProvider mob_group_rolls;

        public IteratorSpawner(List<NaturalEntitySpawnGroup> spawns, LocationHolder mobSpawnHolder, Consumer<Entity> consumer, NumberProvider mobGroupRolls) {
            this.spawns = spawns;
            this.mobSpawnHolder = mobSpawnHolder;
            this.consumer = consumer;
            mob_group_rolls = mobGroupRolls;
        }

        protected int index = -1;

        public boolean hasNext(){
            return index + 1 < spawns.size();
        }

        public Collection<Entity> nextSpawn(){
            index++;
            return currentSpawn();
        }

        public Collection<Entity> currentSpawn(){
            Collection<Entity> list = new HashSet<>();
            NaturalEntitySpawnGroup group = spawns.get(index);
            Location mobSpawn = mobSpawnHolder.value();
            if(mobSpawn == null) return list;
            SpawnContext ctx = SpawnContext.simple(mobSpawn.getBlock(), CruxMath.random());
            NaturalEntitySpawner.spawn(
                group.selectRandom(mob_group_rolls.value().intValue(), ctx), ctx
            ).forEach(e ->{
                list.add(e);
                if(consumer != null) consumer.accept(e);
            });
            return list;
        }
    }
}

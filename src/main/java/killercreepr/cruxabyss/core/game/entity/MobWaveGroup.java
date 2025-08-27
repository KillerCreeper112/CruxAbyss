package killercreepr.cruxabyss.core.game.entity;

import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobWaveGroup {
    protected final List<MobWave> waves;

    public MobWaveGroup(List<MobWave> waves) {
        this.waves = waves;
    }

    public Collection<Entity> spawnWave(int wave, LocationHolder mobSpawnHolder, Consumer<Entity> consumer){
        MobWave w = getWave(wave);
        return w.spawn(mobSpawnHolder, consumer);
    }

    public MobWave.IteratorSpawner waveSpawnIterator(int wave, LocationHolder mobSpawnHolder, Consumer<Entity> consumer,
                                                     Function<NumberProvider, Integer> mobRolls, Function<NumberProvider, Integer> mobGroupRolls){
        MobWave w = getWave(wave);
        return w.iteratorSpawner(
            mobSpawnHolder, consumer,
            mobRolls, mobGroupRolls
        );
    }

    public MobWave getWave(int index){
        index = index-1;
        return index < 0 || index >= waves.size() ? null : waves.get(index);
    }

    public int getMaxWave(){
        return waves.size();
    }
}

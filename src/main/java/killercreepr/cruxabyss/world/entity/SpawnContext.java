package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.data.world.CruxPosition;
import killercreepr.cruxabyss.world.entity.impl.SimpleSpawnContext;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public interface SpawnContext {
    static @NotNull SpawnContext simple(@NotNull World world, @NotNull CruxPosition position, @NotNull Random random){
        return new SimpleSpawnContext(world, position, random);
    }

    static @NotNull SpawnContext simple(@NotNull Block block, @NotNull Random random){
        return simple(block.getWorld(), CruxPosition.block(block), random);
    }

    @NotNull
    CruxPosition getPosition();
    @NotNull World getWorld();
    @NotNull
    Random getRandom();

    default @NotNull Block getBlock(){
        return getPosition().getBlock(getWorld());
    }
}

package killercreepr.cruxabyss.world.entity.impl;

import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawn;
import killercreepr.cruxabyss.world.entity.SpawnContext;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleNaturalEntitySpawn implements NaturalEntitySpawn {
    public int getGroupSize(@NotNull SpawnContext ctx){ return 1; }
    public int getGroupRadius(@NotNull SpawnContext ctx){ return CruxMath.random(5, 10); }

    protected boolean isPassableAndNotLiquid(@NotNull Block b){
        return b.isPassable() && !b.isLiquid();
    }
}

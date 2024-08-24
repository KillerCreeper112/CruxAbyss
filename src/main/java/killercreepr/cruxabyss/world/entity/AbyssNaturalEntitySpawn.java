package killercreepr.cruxabyss.world.entity;

import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxworlds.world.entity.entity.SpawnContext;
import killercreepr.cruxworlds.world.entity.entity.impl.SimpleNaturalEntitySpawn;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbyssNaturalEntitySpawn extends SimpleNaturalEntitySpawn {
    protected final @NotNull AbyssMob mob;
    public AbyssNaturalEntitySpawn(int weight, float quality, @NotNull AbyssMob mob) {
        super(weight, quality);
        this.mob = mob;
    }

    @NotNull
    public AbyssMob getMob() {
        return mob;
    }

    @Nullable
    @Override
    public Entity spawn(@NotNull SpawnContext ctx) {
        return mob.spawn(null, ctx.getBlock().getLocation().toCenterLocation().subtract(0, .5, 0));
    }

    @Override
    public boolean canSpawn(@NotNull SpawnContext ctx) {
        return false;
    }
}

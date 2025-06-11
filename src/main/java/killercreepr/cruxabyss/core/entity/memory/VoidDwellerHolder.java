package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import killercreepr.cruxabyss.core.entity.mob.goal.VoidDwellerGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class VoidDwellerHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("entity/void_dweller");
    protected final VoidDwellerGoal goal;
    public VoidDwellerHolder(@NotNull Key key, @NotNull EntityMemory parent, VoidDwellerGoal goal) {
        super(key, parent);
        this.goal = goal;
    }
    public VoidDwellerHolder(@NotNull EntityMemory parent, VoidDwellerGoal goal) {
        this(KEY, parent, goal);
    }

    protected int tick = 0;
    @Override
    public void tick(@NotNull Entity e) {
        tick++;
        if(tick % 2 == 0){
            Crux.scheduler().runTaskMain(() -> goal.tick());
        }
    }
}

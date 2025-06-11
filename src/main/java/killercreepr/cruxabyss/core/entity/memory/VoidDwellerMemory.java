package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.SimpleEntityMemoryMainThread;
import killercreepr.cruxabyss.core.entity.mob.goal.VoidDwellerGoal;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class VoidDwellerMemory extends SimpleEntityMemoryMainThread {
    protected final VoidDwellerGoal goal;
    public VoidDwellerMemory(@NotNull Entity e, VoidDwellerGoal goal) {
        super(e);
        this.goal = goal;
    }

    @Override
    public boolean tick() {
        if(!super.tick()) return false;
        tickTask();
        return true;
    }

    public void tickTask(){
        if(Crux.getCurrentTick() % 2 != 0) return;
        goal.tick();
    }
}

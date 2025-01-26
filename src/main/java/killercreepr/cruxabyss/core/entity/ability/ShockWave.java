package killercreepr.cruxabyss.core.entity.ability;

import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxform.api.scheduler.ShapeScheduler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ShockWave implements NumberProvider {
    protected double currentRange;
    protected final double maxRange;
    protected final double startRange;
    protected final double rangePerTick;

    public ShockWave(double maxRange, double startRange, double rangePerTick) {
        this.maxRange = maxRange;
        this.startRange = startRange;
        this.rangePerTick = rangePerTick;
    }


    public void start(ShapeScheduler shapeScheduler){
        this.currentRange = startRange;
        new BukkitRunnable(){
            @Override
            public void run() {
                currentRange += rangePerTick;
                shapeScheduler.scheduleAsync(0);
                if(currentRange >= maxRange){
                    cancel();
                }
            }
        }.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
    }

    @NotNull
    @Override
    public Number getMinValue() {
        return currentRange;
    }

    @NotNull
    @Override
    public Number getMaxValue() {
        return currentRange;
    }

    @NotNull
    @Override
    public Number sample(@NotNull Random random, @Nullable InputContext inputContext) {
        return currentRange;
    }
}

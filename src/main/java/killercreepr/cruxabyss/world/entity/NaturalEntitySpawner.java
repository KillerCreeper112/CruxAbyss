package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.world.CruxPosition;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface NaturalEntitySpawner {
    void navigate(@NotNull World world, @NotNull CruxPosition center,
                  @Nullable Predicate<NaturalEntitySpawner> canContinue,
                  @Nullable Consumer<NaturalEntitySpawner> onFinish);

    @NotNull
    default CompletableFuture<Boolean> checkCanNavigate(@NotNull World world){
        return CompletableFuture.supplyAsync(() ->{
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task -> future.complete(canNavigate(world)));
            return future.join();
        });
    }

    boolean canNavigate(@NotNull World world);
    int getRadius();
    int getInnerRadius();
    int getGlobalMobLimit();
    boolean isBelowGlobalMobLimit(int amount);
    int getNaturallySpawnedMobs(@NotNull World world);
}

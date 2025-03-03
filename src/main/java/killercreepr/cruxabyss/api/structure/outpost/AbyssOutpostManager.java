package killercreepr.cruxabyss.api.structure.outpost;

import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface AbyssOutpostManager {
    @NotNull Collection<AbyssOutpostData> getAllOwnedAbyssOutposts(@NotNull UUID uuid);

    /**
     * @return All outposts that the uuid owns or is a part of.
     */
    @NotNull Collection<AbyssOutpostData> getAllFriendlyAbyssOutposts(@NotNull UUID uuid);
    @NotNull Collection<AbyssOutpostData> getAllAbyssOutposts();
    @NotNull Collection<AbyssOutpostData> getAbyssOutposts(@Nullable Predicate<AbyssOutpostData> filter);
    boolean checkFirstTrue(@NotNull Predicate<AbyssOutpostData> filter);
    void forEachAbyssOutpost(@NotNull Consumer<AbyssOutpostData> consumer);
}

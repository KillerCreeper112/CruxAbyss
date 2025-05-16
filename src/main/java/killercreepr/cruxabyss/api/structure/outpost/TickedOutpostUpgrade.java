package killercreepr.cruxabyss.api.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTickedTime;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TickedOutpostUpgrade extends ManagedTickedTime {
    int getLevel();
    void setLevel(int level);

    default @Nullable FileElement serialize(@NotNull FileContext<?> ctx){
        return null;
    }

    default @Nullable OutpostSnapshotData createSnapshotData(){
        return null;
    }

    default void acceptSnapshot(@NotNull OutpostSnapshotData data){

    }
}

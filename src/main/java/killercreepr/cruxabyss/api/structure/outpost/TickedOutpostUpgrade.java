package killercreepr.cruxabyss.api.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTickedTime;

public interface TickedOutpostUpgrade extends ManagedTickedTime {
    int getLevel();
    void setLevel(int level);
}

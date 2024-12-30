package killercreepr.cruxabyss.api.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;

public interface StoredOutpostUpgrade extends ManagedTicked {
    int getLevel();
    void setLevel(int level);
}

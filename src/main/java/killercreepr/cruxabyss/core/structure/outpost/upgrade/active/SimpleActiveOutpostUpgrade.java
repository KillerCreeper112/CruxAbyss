package killercreepr.cruxabyss.core.structure.outpost.upgrade.active;

import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;

public abstract class SimpleActiveOutpostUpgrade implements TickedOutpostUpgrade {
    protected int level;

    public SimpleActiveOutpostUpgrade(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}

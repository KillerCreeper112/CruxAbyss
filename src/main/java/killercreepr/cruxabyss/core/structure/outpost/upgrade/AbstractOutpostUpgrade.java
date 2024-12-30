package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import killercreepr.crux.core.data.SimpleKeyed;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import net.kyori.adventure.key.Key;

public abstract class AbstractOutpostUpgrade extends SimpleKeyed implements OutpostUpgrade {
    public AbstractOutpostUpgrade(Key key) {
        super(key);
    }
}

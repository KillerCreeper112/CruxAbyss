package killercreepr.cruxabyss.core.world;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorldType;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.world.creator.CruxWorldType;

public class AbyssWorldTypes {
    public static final CruxWorldType ABYSS = new AbyssWorldType(Crux.key("abyss"), CruxCore.inst().worldManager());
}

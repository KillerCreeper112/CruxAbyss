package killercreepr.cruxabyss.core.persistence;

import killercreepr.crux.core.persistence.PersistTag;
import org.bukkit.persistence.PersistentDataType;

public class AbyssPersist {
    public static final PersistTag<Integer> REFLECTED_TIMES = PersistTag.register(new PersistTag<>(
        PersistentDataType.INTEGER, "reflected_times"
    ));
}

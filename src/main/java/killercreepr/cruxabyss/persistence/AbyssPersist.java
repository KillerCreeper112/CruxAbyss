package killercreepr.cruxabyss.persistence;

import killercreepr.crux.persistence.PersistTag;
import org.bukkit.persistence.PersistentDataType;

public class AbyssPersist {
    public static final PersistTag<Integer> REFLECTED_TIMES = PersistTag.register(new PersistTag<>(
        PersistentDataType.INTEGER, "reflected_times"
    ));
}

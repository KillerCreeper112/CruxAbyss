package killercreepr.cruxabyss.persistence;

import killercreepr.crux.persistence.PersistTag;
import org.bukkit.persistence.PersistentDataType;

public class AbyssPersist {
    public static final PersistTag<String> SPAWN_REASON = PersistTag.register(new PersistTag<>(PersistentDataType.STRING, "spawn_reason"));
}

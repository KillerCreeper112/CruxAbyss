package killercreepr.cruxabyss.core.structure.safezone;

import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.core.structure.active.SimpleActiveStructure;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssSafezone extends SimpleActiveStructure {
    public ActiveAbyssSafezone(@NotNull StoredStructure stored, @NotNull Chunk chunk) {
        super(stored, chunk);
    }
}

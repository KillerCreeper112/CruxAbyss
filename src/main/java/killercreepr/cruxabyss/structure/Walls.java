package killercreepr.cruxabyss.structure;

import net.kyori.adventure.key.Key;
import org.bukkit.block.BlockFace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Walls {
    protected final Map<BlockFace, Collection<Key>> structures = new HashMap<>();

    public Map<BlockFace, Collection<Key>> getStructures() {
        return structures;
    }
}

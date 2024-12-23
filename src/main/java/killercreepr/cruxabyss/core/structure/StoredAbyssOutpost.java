package killercreepr.cruxabyss.core.structure;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.Structure;
import killercreepr.cruxstructures.api.structure.TickedStoredStructure;
import killercreepr.cruxstructures.core.structure.stored.SimpleStoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class StoredAbyssOutpost extends SimpleStoredStructure implements TickedStoredStructure {
    public UUID owner;
    public StoredAbyssOutpost(@NotNull Structure structure, @NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation) {
        super(structure, chunk, center, rotation);
    }

    public StoredAbyssOutpost(@NotNull Key structureKey, @NotNull StoredChunk chunk, @NotNull CruxPosition center, @NotNull BoundingBox boundingBox, double rotation) {
        super(structureKey, chunk, center, boundingBox, rotation);
    }

    @Override
    public @Nullable ActiveStructure buildActive(@NotNull Chunk chunk) {
        return new ActiveAbyssOutpost(this, chunk);
    }

    protected int tick = 0;
    @Override
    public void tick() {
        if(owner == null) return;
        tick++;
        if(tick < 200) return;
        tick = 0;
        Player p = Crux.getServer().getPlayer(owner);
        if(p == null) return;
        Crux.scheduler().runTask(() ->{
            ValuesProvider cfg = CruxAbyss.inst().values();
            cfg.ABYSS_OUTPOST_TAKE_OVER_EFFECTS().valueOr(Set.of()).forEach(pot ->{
                p.addPotionEffect(pot);
            });
        });
    }
}

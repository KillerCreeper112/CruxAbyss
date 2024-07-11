package killercreepr.cruxabyss.structure;

import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssOutpost extends SimpleActiveStructure {
    protected final @NotNull AbyssOutpost stored;
    public ActiveAbyssOutpost(@NotNull AbyssOutpost stored, @NotNull Chunk chunk) {
        super(stored, chunk);
        this.stored = stored;
    }

    @Override
    public void tick() {
        super.tick();
        stored.setLifeSpan(stored.getLifeSpan() - 1);
        Bukkit.broadcastMessage(stored.getLifeSpan() + "");
    }

    @Override
    public boolean shouldStop() {
        return stored.getLifeSpan() < 1;
    }
}

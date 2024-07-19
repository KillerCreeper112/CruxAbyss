package killercreepr.cruxabyss.structure;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssOutpost extends ActiveTestStructure {
    protected final @NotNull StoredAbyssOutpost data;
    public ActiveAbyssOutpost(@NotNull StoredAbyssOutpost data, @NotNull Chunk chunk) {
        super(data, chunk);
        this.data = data;
    }

    @Override
    public void started() {
        super.started();
        Bukkit.broadcastMessage("ABYSS OUTPOST STARTED");
    }

    @Override
    public void stopped() {
        super.stopped();
        Bukkit.broadcastMessage("ABYSS OUTPOST STOPPED");
    }

    @Override
    @NotNull
    public StoredAbyssOutpost getData() {
        return data;
    }
}

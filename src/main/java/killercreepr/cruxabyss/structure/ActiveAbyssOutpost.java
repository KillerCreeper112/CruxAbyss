package killercreepr.cruxabyss.structure;

import killercreepr.cruxstructures.structure.active.SimpleActiveStructure;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssOutpost extends SimpleActiveStructure {
    protected final @NotNull StoredAbyssOutpost data;
    public ActiveAbyssOutpost(@NotNull StoredAbyssOutpost data, @NotNull Chunk chunk) {
        super(data, chunk);
        this.data = data;
    }

    @Override
    public @NotNull AbyssOutpost getStructure() {
        return (AbyssOutpost) this.getData().getParent();
    }

    public void resetOwner(){
        getData().owner = null;
    }

    public void capture(Player p){
        getData().owner = p.getUniqueId();
    }

    @Override
    public void started() {
        super.started();
    }

    @Override
    public void stopped() {
        super.stopped();
    }

    @Override
    @NotNull
    public StoredAbyssOutpost getData() {
        return data;
    }
}

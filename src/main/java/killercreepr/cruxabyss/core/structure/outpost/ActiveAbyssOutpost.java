package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.api.structure.StoredLootHolderStructure;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.active.SimpleActiveStructure;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ActiveAbyssOutpost extends SimpleActiveStructure {
    protected final @NotNull StoredAbyssOutpost data;
    public final Map<CruxPosition, StoredStructure> lootHolders = new HashMap<>();
    public ActiveAbyssOutpost(@NotNull StoredAbyssOutpost data, @NotNull Chunk chunk) {
        super(data, chunk);
        this.data = data;
        updateLootHolders();
    }

    public void updateLootHolders(){
        lootHolders.clear();
        CruxWorld crux = CruxCore.core().worldManager().getWorld(center.getWorld().getUID());
        if(crux == null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;
        BoundingBox box = getData().getBoundingBox();
        module.getStored(StoredLootHolderStructure.class, stored -> box.overlaps(stored.getBoundingBox()))
            .forEach(stored -> lootHolders.put(stored.getPosition(), stored));
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

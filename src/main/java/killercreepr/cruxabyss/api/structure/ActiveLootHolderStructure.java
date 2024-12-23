package killercreepr.cruxabyss.api.structure;

import killercreepr.cruxstructures.api.structure.ActiveStructure;
import org.jetbrains.annotations.NotNull;

public interface ActiveLootHolderStructure extends ActiveStructure {
    @Override
    @NotNull StoredLootHolderStructure getData();
}

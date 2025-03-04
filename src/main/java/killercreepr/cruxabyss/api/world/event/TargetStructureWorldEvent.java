package killercreepr.cruxabyss.api.world.event;

import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.jetbrains.annotations.NotNull;

public interface TargetStructureWorldEvent extends WorldEvent {
    @NotNull StoredStructure getTargetStructure();
}

package killercreepr.cruxabyss.api.entity.mob.goal;

import killercreepr.cruxstructures.api.structure.StoredStructure;

public interface OutpostTargeterGoal {
    StoredStructure getOutpostTarget();
    void setOutpostTarget(StoredStructure structure);
}

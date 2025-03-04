package killercreepr.cruxabyss.api.structure.outpost;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface OutpostData {
    Map<OutpostUpgrade, Integer> getUpgrades();
    boolean hasUpgrade(OutpostUpgrade upgrade);
    int getUpgradeLevel(OutpostUpgrade upgrade);
    void setUpgradeLevel(OutpostUpgrade upgrade, int level);
    void removeUpgrade(OutpostUpgrade upgrade);

    @Nullable TickedOutpostUpgrade getTickedOutpostUpgrade(OutpostUpgrade upgrade);
}

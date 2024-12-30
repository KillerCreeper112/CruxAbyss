package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;

public class AbyssOutpostUpgrades {
    public static void register(){}
    public static final OutpostUpgrade REGENERATION = register(new RegenerationUpgrade(Crux.key("regeneration")));
    private static OutpostUpgrade register(OutpostUpgrade upgrade){
        return AbyssRegistries.OUTPOST_UPGRADE.register(upgrade);
    }
}

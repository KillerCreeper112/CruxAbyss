package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;

public class AbyssOutpostUpgrades {
    public static void register(){}
    public static final OutpostUpgrade REGENERATION = register(new RegenerationUpgrade(Crux.key("regeneration")));
    public static final OutpostUpgrade ABYSSAL_RELAY = register(new AbyssalRelayUpgrade(Crux.key("abyssal_relay")));
    public static final OutpostUpgrade ABYSSAL_FORGE = register(new AbyssalForgeUpgrade(Crux.key("abyssal_forge")));
    private static OutpostUpgrade register(OutpostUpgrade upgrade){
        return AbyssRegistries.OUTPOST_UPGRADE.register(upgrade);
    }
}

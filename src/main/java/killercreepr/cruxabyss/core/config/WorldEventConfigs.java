package killercreepr.cruxabyss.core.config;

import killercreepr.cruxabyss.api.values.AbyssOutpostInvasionCfg;
import killercreepr.cruxconfig.config.common.data.GenericCfgContainer;
import killercreepr.cruxconfig.config.common.file.ICfg;
import org.bukkit.plugin.Plugin;

public class WorldEventConfigs extends GenericCfgContainer<ICfg<?, ?>> {
    public final AbyssOutpostInvasionCfg ABYSS_OUTPOST_INVASION;
    public WorldEventConfigs(Plugin plugin) {
        super();
        ABYSS_OUTPOST_INVASION = new AbyssOutpostInvasionEventConfig(plugin, "events/abyss_outpost_invasion");
        addUnchecked(
            ABYSS_OUTPOST_INVASION
        );
    }
}

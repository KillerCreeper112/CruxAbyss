package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.EntityUpgradeAbyssOutpostEvent;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostUpgradeObjective extends GenericEventObjective<EntityUpgradeAbyssOutpostEvent> {
    public AbyssOutpostUpgradeObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(EntityUpgradeAbyssOutpostEvent event) {
        var player = event.getEntity();
        return LootContext.builder()
            .looted(event.getOutpost())
            .looter(player)
            .location(player.getLocation())
            .info(
                DataExchange.builder()
                    .putAll(event.getOldlevel(), "old_level")
                    .putAll(event.getNewLevel(), "new_level")
                    .putAll(event.getUpgrade(), "upgrade")
                    .build()
            )
            .build();
    }
}

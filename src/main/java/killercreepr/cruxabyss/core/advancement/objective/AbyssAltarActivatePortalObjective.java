package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.AbyssAltarActivatePortalEvent;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarActivatePortalObjective extends GenericEventObjective<AbyssAltarActivatePortalEvent> {
    public AbyssAltarActivatePortalObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(AbyssAltarActivatePortalEvent event) {
        var altar = event.getAltar();
        var player = event.getPlayer();
        return LootContext.builder()
            .looted(event.getAltar())
            .looter(player)
            .location(event.getAltar().center().getLocation())
            .info(
                DataExchange.builder()
                    .putAll(event.getItem(), "item")
                    .putAll(altar, "altar")
                    .build()
            )
            .build();
    }
}

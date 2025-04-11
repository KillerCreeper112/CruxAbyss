package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.EntityTravelThroughAbyssPortalGatewayEvent;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class TravelThroughAbyssPortalGatewayObjective extends GenericEventObjective<EntityTravelThroughAbyssPortalGatewayEvent> {
    public TravelThroughAbyssPortalGatewayObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(EntityTravelThroughAbyssPortalGatewayEvent event) {
        var to = event.getTo();
        var player = event.getEntity();
        return LootContext.builder()
            .looted(to)
            .looter(player)
            .location(to)
            .build();
    }
}

package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.PlayerAbyssAltarBuildEvent;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarBuildObjective extends GenericEventObjective<PlayerAbyssAltarBuildEvent> {
    public AbyssAltarBuildObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(PlayerAbyssAltarBuildEvent event) {
        var altar = event.getAltar();
        var player = event.getPlayer();
        return LootContext.builder()
            .looted(event.getBlock())
            .looter(player)
            .location(event.getBlock().getLocation())
            .info(
                DataExchange.builder()
                    .putAll(event.getBlock(), "block")
                    .putAll(altar, "altar")
                    .build()
            )
            .build();
    }
}

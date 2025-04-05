package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.AbyssOutpostDeactivateEvent;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostDeactivateObjective extends GenericEventObjective<AbyssOutpostDeactivateEvent> {
    public AbyssOutpostDeactivateObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(AbyssOutpostDeactivateEvent event) {
        var outpost = event.getOutpost();
        var player = event.getPlayer();
        return LootContext.builder()
            .looted(outpost)
            .looter(player)
            //.add(AbyssComponents.LOOT_CAPTURED_ABYSS_OUTPOST, event)
            .build();
    }
}

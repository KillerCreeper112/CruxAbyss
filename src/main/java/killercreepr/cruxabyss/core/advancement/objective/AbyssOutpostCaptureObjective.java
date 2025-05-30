package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostCaptureObjective extends GenericEventObjective<AbyssOutpostCaptureEvent> {
    public AbyssOutpostCaptureObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(AbyssOutpostCaptureEvent event) {
        var outpost = event.getOutpost();
        var player = event.getEntity();
        return LootContext.builder()
            .looted(outpost)
            .looter(player)
            .add(AbyssComponents.LOOT_CAPTURED_ABYSS_OUTPOST, event)
            .build();
    }
}

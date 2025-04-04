package killercreepr.cruxabyss.core.advancement.objective;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxabyss.api.event.PlayerSurvive1MinuteInAbyssEvent;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import org.jetbrains.annotations.NotNull;

public class Survive1MinuteAbyssObjective extends GenericEventObjective<PlayerSurvive1MinuteInAbyssEvent> {
    public Survive1MinuteAbyssObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(PlayerSurvive1MinuteInAbyssEvent event) {
        return LootContext.builder()
            .looter(event.getPlayer())
            .build();
    }
}

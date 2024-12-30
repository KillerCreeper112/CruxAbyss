package killercreepr.cruxabyss.core.loot.condition;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.loot.conditions.BaseCondition;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import org.jetbrains.annotations.NotNull;

//todo
public class AbyssOutpostCaptureCondition extends BaseCondition {
    public AbyssOutpostCaptureCondition(@NotNull String target) {
        super(target);
    }

    @Override
    public boolean test(@NotNull LootContext ctx) {
        if(!ctx.has(AbyssComponents.LOOT_CAPTURED_ABYSS_OUTPOST)) return false;
        return true;
    }
}

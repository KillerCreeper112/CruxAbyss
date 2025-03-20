package killercreepr.cruxabyss.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssOutpostUpgrades;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveOutpostLockerUpgrade;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostLockerOpenAction extends SimpleMenuAction {
    public AbyssOutpostLockerOpenAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        DataExchange info = ctx.getAllMergedInfo();
        AbyssOutpostData outpost = info.get(AbyssOutpostData.class);
        if(outpost == null) outpost = info.getOrThrow(ActiveAbyssOutpost.class).getData();
        if(!(outpost.getTickedOutpostUpgrade(AbyssOutpostUpgrades.OUTPOST_LOCKER) instanceof ActiveOutpostLockerUpgrade upgrade)) return true;
        upgrade.openStorage(ctx.getPlayer());
        return true;
    }
}

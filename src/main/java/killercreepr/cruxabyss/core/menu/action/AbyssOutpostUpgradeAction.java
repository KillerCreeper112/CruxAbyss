package killercreepr.cruxabyss.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class AbyssOutpostUpgradeAction extends SimpleMenuAction {
    public AbyssOutpostUpgradeAction(@NotNull Key key) {
        super(key);
    }

    //[abyss_outpost_upgrade] set <upgrade> <level>
    //[abyss_outpost_upgrade] clear
    //[abyss_outpost_upgrade] remove <upgrade>
    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        DataExchange info = ctx.getAllMergedInfo();
        AbyssOutpostData outpost = info.get(AbyssOutpostData.class);
        if(outpost == null) outpost = info.getOrThrow(ActiveAbyssOutpost.class).getData();
        switch (args[0].toLowerCase()){
            case "set" ->{
                Key upgradeKey = Crux.key(args[1]);
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(upgradeKey);
                int level = (int) CruxMath.evaluate(args[2]);
                outpost.setUpgradeLevel(upgrade, level);
            }
            case "clear" ->{
                new HashSet<>(outpost.getUpgrades().keySet()).forEach(outpost::removeUpgrade);
            }
            case "remove" ->{
                Key upgradeKey = Crux.key(args[1]);
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(upgradeKey);
                outpost.removeUpgrade(upgrade);
            }
        }
        return true;
    }
}

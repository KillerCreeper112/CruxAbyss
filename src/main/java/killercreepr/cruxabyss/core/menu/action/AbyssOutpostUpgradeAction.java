package killercreepr.cruxabyss.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
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
    public boolean execute(@NotNull Player p, @NotNull ActionContext ctx, @NotNull String[] args) {
        DataExchange info = ctx.getAllMergedInfo();
        ActiveAbyssOutpost outpost = info.getOrThrow(ActiveAbyssOutpost.class);
        switch (args[0].toLowerCase()){
            case "set" ->{
                Key upgradeKey = Crux.key(args[1]);
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(upgradeKey);
                int level = (int) CruxMath.evaluate(args[2]);
                outpost.getData().setUpgradeLevel(upgrade, level);
            }
            case "clear" ->{
                new HashSet<>(outpost.getData().getUpgrades().keySet()).forEach(upgrade ->{
                    outpost.getData().removeUpgrade(upgrade);
                });
            }
            case "remove" ->{
                Key upgradeKey = Crux.key(args[1]);
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(upgradeKey);
                outpost.getData().removeUpgrade(upgrade);
            }
        }
        return true;
    }
}

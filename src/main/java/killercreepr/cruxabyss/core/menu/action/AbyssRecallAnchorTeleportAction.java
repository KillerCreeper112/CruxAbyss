package killercreepr.cruxabyss.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssRecallAnchor;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbyssRecallAnchorTeleportAction extends SimpleMenuAction {
    public AbyssRecallAnchorTeleportAction(@NotNull Key key) {
        super(key);
    }

    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        DataExchange info = ctx.getAllMergedInfo();
        AbyssRecallAnchor anchor = info.getOrThrow(AbyssRecallAnchor.class);
        Entity p = info.getOrThrow(Player.class);
        boolean x = anchor.attemptTeleport(p);
        if(!x){
            Lang.ABYSS_OUTPOST_UPGRADE_RECALL_CANNOT_TELEPORT.use(p);
        }
        return true;
    }
}

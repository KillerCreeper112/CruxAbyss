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
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public class AbyssOutpostMemberAction extends SimpleMenuAction {
    public AbyssOutpostMemberAction(@NotNull Key key) {
        super(key);
    }

    //[abyss_outpost_member] add <uuid>
    //[abyss_outpost_member] remove <uuid>
    //[abyss_outpost_member] clear
    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        DataExchange info = ctx.getAllMergedInfo();
        ActiveAbyssOutpost outpost = info.getOrThrow(ActiveAbyssOutpost.class);
        switch (args[0].toLowerCase()){
            case "add" ->{
                UUID uuid = UUID.fromString(args[1]);
                outpost.getData().members.add(uuid);
            }
            case "clear" ->{
                outpost.getData().members.clear();
            }
            case "remove" ->{
                UUID uuid = UUID.fromString(args[1]);
                outpost.getData().members.remove(uuid);
            }
        }
        return true;
    }
}

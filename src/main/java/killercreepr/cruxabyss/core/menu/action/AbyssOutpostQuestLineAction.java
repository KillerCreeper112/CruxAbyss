package killercreepr.cruxabyss.core.menu.action;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.questline.AbyssOutpostQuestLineHolder;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxmenus.api.menu.contex.ActionContext;
import killercreepr.cruxmenus.core.menu.action.SimpleMenuAction;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostQuestLineAction extends SimpleMenuAction {
    public AbyssOutpostQuestLineAction(@NotNull Key key) {
        super(key);
    }

    //[abyss/outpost/quest_line] open <menu_key>
    @Override
    public boolean execute(@NotNull ActionContext ctx, @NotNull String[] args) {
        DataExchange info = ctx.getAllMergedInfo();
        AbyssOutpostData outpost = info.get(AbyssOutpostData.class);
        if(outpost == null) outpost = info.getOrThrow(ActiveAbyssOutpost.class).getData();
        switch (args[0].toLowerCase()){
            case "open" ->{
                Key menuKey = Crux.key(args[1]);
                CruxCore.core().cruxMenus().menuRegistry().menuHolders().get(menuKey)
                    .open(ctx.getPlayer(), DataExchange.builder()
                        .put("abyss_outpost", outpost)
                        .put("quest_progress_key", AbyssOutpostQuestLineHolder.KEY)
                        .put(outpost.questLineHolder)
                        .build());
            }
        }
        return true;
    }
}

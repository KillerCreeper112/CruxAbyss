package killercreepr.cruxabyss.core.structure.outpost.questline;

import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxquestline.core.config.handler.FileSimpleQuestLineProgress;
import killercreepr.cruxquestline.core.config.handler.QuestLineCfgHandlers;

public class AbyssQuestLineLoader {
    public static void load(){
        QuestLineCfgHandlers.PROGRESS_TYPE_HANDLERS.put(AbyssOutpostQuestLineHolder.KEY,
            (FileSimpleQuestLineProgress)
                info -> AbyssOutpostQuestLineHolder.createNewProgress(info.getOrThrow(
                    AbyssOutpostQuestLineHolder.KEY.value(), AbyssOutpostData.class)));
    }
}

package killercreepr.cruxabyss.core.structure.outpost.questline;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxquestline.api.quest.QuestLineHolder;
import killercreepr.cruxquestline.api.quest.QuestLineProgress;
import killercreepr.cruxquestline.core.quest.SimpleQuestGenerator;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbyssOutpostQuestLineHolder implements QuestLineHolder {
    public static final Key KEY = Crux.key("abyss_outpost");
    public static QuestLineProgress createNewProgress(AbyssOutpostData outpost){
        return SimpleQuestGenerator.createGenericProgress(KEY, () -> outpost.questLineHolder);
    }

    public AbyssOutpostQuestLineHolder(AbyssOutpostData data) {
        setQuestLineProgress(createNewProgress(data));
    }

    protected final Map<Key, QuestLineProgress> progress = new HashMap<>();
    @Nullable
    @Override
    public QuestLineProgress getQuestLineProgress(@NotNull Key key) {
        return progress.get(key);
    }

    @Override
    public void setQuestLineProgress(@NotNull QuestLineProgress questLineProgress) {
        progress.put(questLineProgress.key(), questLineProgress);
    }

    @Nullable
    @Override
    public QuestLineProgress removeQuestLineProgress(@NotNull Key key) {
        return progress.remove(key);
    }

    @NotNull
    @Override
    public Map<Key, QuestLineProgress> getQuestLineProgressMap() {
        return progress;
    }

    @Override
    public void onStreakUpdated(@NotNull QuestLineProgress questLineProgress) {

    }

    @Override
    public int getHighestStreak(@NotNull QuestLineProgress questLineProgress) {
        return 0;
    }

    @Override
    public void onQuestCompleted(QuestLineProgress questLineProgress) {

    }
}

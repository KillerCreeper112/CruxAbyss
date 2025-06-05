package killercreepr.cruxabyss.core.structure.outpost.questline;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxquestline.api.quest.QuestCategoryData;
import killercreepr.cruxquestline.api.quest.QuestLineHolder;
import killercreepr.cruxquestline.api.quest.QuestLineProgress;
import killercreepr.cruxquestline.core.lang.Lang;
import killercreepr.cruxquestline.core.quest.SimpleQuestGenerator;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbyssOutpostQuestLineHolder implements QuestLineHolder {
    public static final Key KEY = Crux.key("abyss_outpost");
    public static QuestLineProgress createNewProgress(AbyssOutpostData outpost){
        return SimpleQuestGenerator.createSimpleProgress(KEY, () -> outpost.questLineHolder);
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
        return questLineProgress.getStreak();
    }

    @Override
    public void onQuestCompleted(QuestLineProgress progress) {
        QuestCategoryData data = progress.getQuestGenerator().getCategoryData(progress.getQuestCategory());
        progress.getAllParticipants().forEach((uuid, par) ->{
            Player p = Crux.getServer().getPlayer(uuid);
            if(p==null) return;

            Lang.QUEST_COMPLETE.use(p, TagContainer.merged().hook(p));
            if(data != null){
                data.reward(p, progress);
            }
            new ParticleBuilder(Particle.TOTEM_OF_UNDYING)
                .location(p.getLocation().add(0, p.getHeight()/2, 0))
                .offset(.5, .5, .5)
                .count(CruxMath.random(7, 10))
                .extra(.1)
                .spawn()
            ;
        });
    }
}

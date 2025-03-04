package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import com.google.gson.reflect.TypeToken;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveAbyssalRecallUpgrade;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class AbyssalRecallUpgrade extends AbstractOutpostUpgrade{
    public AbyssalRecallUpgrade(Key key) {
        super(key);
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade createStored(@NotNull AbyssOutpostData data, int level) {
        return new ActiveAbyssalRecallUpgrade(level, data);
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade createActive(@NotNull ActiveAbyssOutpost outpost, int level) {
        return null;
    }

    @Nullable
    @Override
    public TickedOutpostUpgrade deserialize(@NotNull AbyssOutpostData data, int level, @NotNull FileContext<?> ctx, @Nullable FileElement e) {
        ActiveAbyssalRecallUpgrade upgrade = new ActiveAbyssalRecallUpgrade(level, data);
        if(!(e instanceof FileObject o)) return upgrade;
        upgrade.setRespawnAnchors(ctx.getRegistry().deserializeFromFile(
            new TypeToken<Set<CruxPosition>>(){}.getType(), o.get("respawn_anchors")
        ));
        return upgrade;
    }
}

package killercreepr.cruxabyss.core.config.handler;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.core.math.BlockPos;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.handler.FileObjectHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class FileAbyssWorldPlayerData /*implements FileObjectHandler<AbyssWorld.PlayerData>*/ {
    /*@Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull AbyssWorld.PlayerData data) {
        FileRegistry reg = ctx.getRegistry();
        return new FileObject()
            .add("claimed_outposts", reg.serializeToFile(data.getClaimedOutposts()))
            ;
    }

    @Nullable
    @Override
    public AbyssWorld.PlayerData deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileRegistry reg = ctx.getRegistry();
        Collection<BlockPos> claimedOutposts = reg.deserializeFromFile(
            new TypeToken<Set<BlockPos>>(){}.getType(),
            o.get("claimed_outposts")
        );
        if(claimedOutposts == null || claimedOutposts.isEmpty()) return null;
        var data = new AbyssWorld.PlayerData();
        data.setClaimedOutposts(claimedOutposts);
        return data;
    }*/
}

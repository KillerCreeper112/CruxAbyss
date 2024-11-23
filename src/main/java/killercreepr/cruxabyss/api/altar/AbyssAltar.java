package killercreepr.cruxabyss.api.altar;

import killercreepr.cruxabyss.core.altar.SimpleAbyssAltar;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface AbyssAltar {
    static @Nullable AbyssAltar getFromCenter(@NotNull Block block){
        return SimpleAbyssAltar.getFromCenter(block);
    }

    static @Nullable AbyssAltar findAltar(@NotNull Block block){
        return SimpleAbyssAltar.findAltar(block);
    }

    @NotNull
    Block center();
    boolean isValid();
    @NotNull
    BlockFace getDirection();
    @NotNull
    Collection<AltarEntity> selectedEntities();
}

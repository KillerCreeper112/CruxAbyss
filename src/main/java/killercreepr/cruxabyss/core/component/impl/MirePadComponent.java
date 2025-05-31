package killercreepr.cruxabyss.core.component.impl;

import killercreepr.cruxabyss.core.block.active.ActiveMirePad;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MirePadComponent implements CruxBlockComponent {

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveMirePad(block, crux);
    }
}

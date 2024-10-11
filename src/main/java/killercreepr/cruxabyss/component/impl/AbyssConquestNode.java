package killercreepr.cruxabyss.component.impl;

import killercreepr.cruxabyss.block.active.ActiveAbyssConquestNode;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.component.CruxBlockComponent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssConquestNode implements CruxBlockComponent {
    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveAbyssConquestNode(block, crux);
    }
}

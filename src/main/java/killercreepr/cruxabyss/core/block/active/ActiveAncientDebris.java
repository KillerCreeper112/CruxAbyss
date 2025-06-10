package killercreepr.cruxabyss.core.block.active;

import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveAncientDebris extends SimpleActiveCruxBlock {
    public ActiveAncientDebris(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    @Override
    public @NotNull Key buildLootTableKey() {
        Key key = this.cruxBlock.getGroup() == null ? this.cruxBlock.key() : this.cruxBlock.getGroup().key();
        return Key.key("crux", "block/" + key.value());
    }
}

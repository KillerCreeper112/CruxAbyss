package killercreepr.cruxabyss.core.block.active;

import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.flag.BlockBreakFlags;
import killercreepr.cruxblocks.core.block.active.standard.ActiveEntitySpawner;
import killercreepr.cruxblocks.core.block.component.standard.EntitySpawnerComponent;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawner;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssEntitySpawner extends ActiveEntitySpawner {
    public ActiveAbyssEntitySpawner(@NotNull Block block, @NotNull CruxBlock cruxBlock,
                                    @NotNull EntitySpawnerComponent data,
                                    @NotNull NaturalEntitySpawner spawner) {
        super(block, cruxBlock, data, spawner);
    }

    @Override
    public void navigateSpawner() {
        super.navigateSpawner();
        if(delay >= 0) return;
        breakBlock(null, BlockBreakFlags.empty());
    }
}

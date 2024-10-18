package killercreepr.cruxabyss.block.active;

import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.standard.active.ActiveEntitySpawner;
import killercreepr.cruxblocks.block.standard.component.EntitySpawnerComponent;
import killercreepr.cruxblocks.user.Miner;
import killercreepr.cruxworlds.world.entity.NaturalEntitySpawner;
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
        breakBlock((Miner) null);
    }
}

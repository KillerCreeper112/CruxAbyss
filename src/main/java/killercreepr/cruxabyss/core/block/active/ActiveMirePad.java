package killercreepr.cruxabyss.core.block.active;

import killercreepr.crux.core.util.CruxBlockUtil;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxRedstonePowerable;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ActiveMirePad extends SimpleActiveCruxBlock implements ActiveCruxRedstonePowerable {
    public ActiveMirePad(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    public CruxBlock getUnpoweredBlock(){
        return cruxBlock.getGroup().getBlock(
            cruxBlock.getGroup().key()
        );
    }

    public CruxBlock getPoweredBlock(){
        return cruxBlock.getGroup().getBlock(
            Key.key(
                cruxBlock.getGroup().key().namespace(), cruxBlock.getGroup().key().value() + "_powered"
            )
        );
    }

    @Override
    public void redstonePowerChanged(Block from, int newPower) {
        super.redstonePowerChanged(from, newPower);
        update();
    }

    @Override
    public void update() {
        super.update();
        boolean powered = CruxBlockUtil.findPowerSource(block) != null;
        CruxBlock state = powered ? getPoweredBlock() : getUnpoweredBlock();
        if(state.getTextureData().compareTexture(block)) return;
        state.setBlock(BlockContext.context(block, null), true, false);
    }
}

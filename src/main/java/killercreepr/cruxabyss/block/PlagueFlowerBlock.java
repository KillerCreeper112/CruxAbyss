package killercreepr.cruxabyss.block;

import killercreepr.cruxblocks.block.GenericBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.context.BlockContext;
import killercreepr.cruxblocks.block.texture.TextureData;
import killercreepr.cruxcore.CruxCore;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class PlagueFlowerBlock extends GenericBlock {
    public PlagueFlowerBlock(@NotNull Key key, @NotNull TextureData textureData) {
        super(key, textureData);
    }

    @Override
    public boolean canPlace(@NotNull BlockContext ctx) {
        Block b = ctx.getBlock();
        Block ground = b.getRelative(BlockFace.DOWN);
        ActiveCruxBlock active = CruxCore.inst().cruxBlocks().getActiveBlock(ground);
        if(active == null) return false;
        return active.getCruxBlock().key().value().contains("plague_flower");
    }
}

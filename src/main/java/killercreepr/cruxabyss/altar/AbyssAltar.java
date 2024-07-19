package killercreepr.cruxabyss.altar;

import killercreepr.crux.util.CruxBlockFace;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Candle;
import org.jetbrains.annotations.NotNull;

public class AbyssAltar {
    protected final @NotNull Block center;
    public AbyssAltar(@NotNull Block center) {
        this.center = center;
    }

    public @NotNull BlockFace getDirection(){
        if(check(BlockFace.EAST)) return BlockFace.EAST;
        return BlockFace.NORTH;
    }

    public boolean isValid(){
        if(center.getType() != Material.ENCHANTING_TABLE) return false;
        if(check(BlockFace.NORTH) || check(BlockFace.EAST)) return true;
        return false;
    }

    public boolean check(@NotNull BlockFace dir){
        return checkBlock(center.getRelative(CruxBlockFace.rotateLeft(dir))) &&
            checkBlock(center.getRelative(CruxBlockFace.rotateRight(dir)));
    }

    public boolean checkBlock(@NotNull Block b){
        if(b.getType() != Material.OBSIDIAN) return false;
        if(!(b.getRelative(BlockFace.UP).getBlockData() instanceof Candle candle)) return false;
        return candle.isLit();
    }
}

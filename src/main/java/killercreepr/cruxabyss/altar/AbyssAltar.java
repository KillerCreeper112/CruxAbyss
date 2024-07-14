package killercreepr.cruxabyss.altar;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class AbyssAltar {
    protected final @NotNull Block center;
    public AbyssAltar(@NotNull Block center) {
        this.center = center;
    }

    public @NotNull BlockFace getDirection(){
        if(center.getRelative(BlockFace.EAST).getType() == Material.OBSIDIAN && checkEastWest()){
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }

    public boolean isValid(){
        if(center.getType() != Material.ENCHANTING_TABLE) return false;
        if(center.getRelative(BlockFace.NORTH).getType() == Material.OBSIDIAN) return checkNorthSouth();
        if(center.getRelative(BlockFace.EAST).getType() == Material.OBSIDIAN) return checkEastWest();
        return false;
    }

    public boolean checkNorthSouth(){
        Block b = center.getRelative(BlockFace.SOUTH);
        if(b.getType() != Material.OBSIDIAN) return false;
        return !checkEastWest();
    }

    public boolean checkEastWest(){
        Block b = center.getRelative(BlockFace.WEST);
        if(b.getType() != Material.OBSIDIAN) return false;
        return !checkNorthSouth();
    }
}

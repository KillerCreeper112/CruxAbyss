package killercreepr.cruxabyss.altar;

import killercreepr.crux.util.CruxBlockFace;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssAltar {
    protected final @NotNull Block center;
    public AbyssAltar(@NotNull Block center) {
        this.center = center;
    }

    @NotNull
    public Block getCenter() {
        return center;
    }

    public @Nullable Entity getSelectedEntity(){
        BoundingBox box = BoundingBox.of(center.getLocation().toCenterLocation(), 1, 1, 1).shift(0, 1, 0);
        for(Entity e : center.getWorld().getNearbyEntities(box, e -> CruxMob.is(e, AbyssMob.ABYSS_CRYSTAL) || CruxMob.is(e, AbyssMob.ALTAR_PORTAL) || CruxMob.is(e, AbyssMob.RETURN_PORTAL))){
            return e;
        }
        return null;
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

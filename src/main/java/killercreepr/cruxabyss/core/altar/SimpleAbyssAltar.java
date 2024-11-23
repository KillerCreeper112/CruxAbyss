package killercreepr.cruxabyss.core.altar;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.altar.AltarEntity;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SimpleAbyssAltar implements AbyssAltar {
    public static final Predicate<Block> OBSIDIAN_PREDICATE = b -> b.getType() == Material.OBSIDIAN;
    public static final Predicate<Block> CANDLE_PREDICATE = b -> b.getBlockData() instanceof Candle c && c.isLit();
    public static final Predicate<Block> ENCHANTING_TABLE_PREDICATE = b -> b.getType() == Material.ENCHANTING_TABLE;

    public static final Map<CruxPosition, Predicate<Block>> STRUCTURE = new MapBuilder<CruxPosition, Predicate<Block>>(new LinkedHashMap<>())
        .put(CruxPosition.block(0, 0, 0), ENCHANTING_TABLE_PREDICATE) // Check enchanting table first.
        .put(CruxPosition.block(1, 0, 0), OBSIDIAN_PREDICATE)
        .put(CruxPosition.block(1, 1, 0), CANDLE_PREDICATE)
        .put(CruxPosition.block(-1, 0, 0), OBSIDIAN_PREDICATE)
        .put(CruxPosition.block(-1, 1, 0), CANDLE_PREDICATE)
        .buildUnmodifiable();

    public static final float[] validRotations = new float[]{0, 90};

    public static AbyssAltar getFromCenter(@NotNull Block block){
        if(!ENCHANTING_TABLE_PREDICATE.test(block)) return null;
        AbyssAltar altar = new SimpleAbyssAltar(block);
        if(altar.isValid()) return altar;
        return null;
    }

    public static AbyssAltar findAltar(@NotNull Block block){
        CruxPosition blockPos = CruxPosition.block(block);
        World world = block.getWorld();
        for (Map.Entry<CruxPosition, Predicate<Block>> entry : STRUCTURE.entrySet()) {
            CruxPosition pos = entry.getKey();
            Predicate<Block> filter = entry.getValue();
            if(!filter.test(block)) continue;

            CruxPosition centerPos = blockPos.subtract(pos);
            AbyssAltar altar = getFromCenter(centerPos.getBlock(world));
            if(altar != null) return altar;
        }
        return null;
    }

    protected final @NotNull Block center;
    public SimpleAbyssAltar(@NotNull Block center) {
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

    @NotNull
    @Override
    public Block center() {
        return center;
    }

    @Override
    public boolean isValid(){
        return checkFromCenter(center);
    }

    @NotNull
    @Override
    public BlockFace getDirection() {
        if(checkFromCenter(center, 0)) return BlockFace.NORTH;
        return BlockFace.EAST;
    }

    @NotNull
    @Override
    public Collection<AltarEntity> selectedEntities() {
        Collection<AltarEntity> list = new HashSet<>();
        BoundingBox box = BoundingBox.of(center.getLocation().toCenterLocation(), 1, 1, 1).shift(0, 1, 0);
        for(Entity e : center.getWorld().getNearbyEntities(box, e -> CruxMob.is(e, AbyssMob.ABYSS_CRYSTAL) ||
            CruxMob.is(e, AbyssMob.ALTAR_PORTAL) || CruxMob.is(e, AbyssMob.RETURN_PORTAL))){
            //todo make it get
        }
        return list;
    }

    public boolean checkFromCenter(@NotNull Block center){
        for(float rot : validRotations){
            if(checkFromCenter(center, rot)) return true;
        }
        return false;
    }

    public boolean checkFromCenter(@NotNull Block center, double rotation){
        int checked = 0;
        World world = center.getWorld();
        CruxPosition centerPos = CruxPosition.block(0, 0, 0);
        for (Map.Entry<CruxPosition, Predicate<Block>> entry : STRUCTURE.entrySet()) {
            CruxPosition pos = entry.getKey();
            Predicate<Block> filter = entry.getValue();

            CruxPosition rotated = pos.rotateAroundY(centerPos, rotation);
            Block block = rotated.getBlock(world);
            if(filter.test(block)) checked++;
        }
        return checked == STRUCTURE.size();
    }
}

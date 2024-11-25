package killercreepr.cruxabyss.core.altar;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.altar.AltarEntity;
import killercreepr.cruxabyss.api.altar.AltarEntityType;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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

    public static final short[] validRotations = new short[]{0, 90};

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

    @NotNull
    @Override
    public Block center() {
        return center;
    }

    @Override
    public boolean isValid(){
        return checkFromCenter(center);
    }

    protected final Map<Block, Predicate<Block>> cache = new HashMap<>();
    protected short cacheRotation;
    @Override
    public boolean isValidCache() {
        if(cache.isEmpty()) return isValid();
        for (Map.Entry<Block, Predicate<Block>> entry : cache.entrySet()) {
            Block b = entry.getKey();
            Predicate<Block> filter = entry.getValue();
            if(!filter.test(b)) return false;
        }
        return true;
    }

    @NotNull
    @Override
    public BlockFace getDirection() {
        if(cache.isEmpty()) isValid();
        //if(!isValidCache()) throw new IllegalStateException("SimpleAbyssAltar is no longer valid! Cannot get direction.");
        if(cacheRotation == 0) return BlockFace.NORTH;
        return BlockFace.EAST;
    }

    @NotNull
    @Override
    public Collection<AltarEntity> selectedEntities() {
        Collection<AltarEntity> list = new HashSet<>();
        BoundingBox box = BoundingBox.of(center.getLocation().add(0, 1, 0).toCenterLocation(), 1, 1, 1);
        for(Entity e : center.getWorld().getNearbyEntities(box)){
            CruxMob cruxMob = CruxMob.get(e);
            if(!(cruxMob instanceof AltarEntityType type)) continue;
            list.add(type.createAltarEntity(this, e));
        }
        return list;
    }

    public boolean checkFromCenter(@NotNull Block center){
        for(short rot : validRotations){
            if(checkFromCenter(center, rot, true)){
                return true;
            }
        }
        return false;
    }

    public boolean checkFromCenter(@NotNull Block center, double rotation, boolean updateCache){
        int checked = 0;
        CruxPosition centerPos = CruxPosition.block(0, 0, 0);
        Map<Block, Predicate<Block>> cache = new HashMap<>();
        for (Map.Entry<CruxPosition, Predicate<Block>> entry : STRUCTURE.entrySet()) {
            CruxPosition pos = entry.getKey();
            Predicate<Block> filter = entry.getValue();

            CruxPosition rotated = pos.rotateAroundY(centerPos, rotation);
            Block block = center.getRelative(rotated.blockX(), rotated.blockY(), rotated.blockZ());
            if(filter.test(block)){
                checked++;
                cache.put(block, filter);
            }
        }
        if(checked == STRUCTURE.size()){
            if(!updateCache) return true;
            this.cache.clear();
            this.cache.putAll(cache);
            this.cacheRotation = (short) rotation;
            return true;
        }
        return false;
    }
}

package killercreepr.cruxabyss.world.entity;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.loot.SimpleWeighted;
import killercreepr.crux.loot.WeightedObject;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class NaturalMobSettings extends SimpleWeighted {
    private static final Set<NaturalMobSettings> REGISTRY = new HashSet<>();
    public static <T extends NaturalMobSettings> T register(@NotNull T e){
        REGISTRY.add(e);
        return e;
    }
    public static NaturalMobSettings EMPTY;
    public static NaturalMobSettings CRIMSON_EYE;
    public static NaturalMobSettings MOOSE;
    public static NaturalMobSettings GROUND_DWELLER;
    public static NaturalMobSettings CHARRED_BONES;

    private final static Set<Material> CRIMSON_EYE_SPAWNABLE_ON_NOT = new HashSet<>();

    public static void register(){
        CRIMSON_EYE_SPAWNABLE_ON_NOT.addAll(MaterialSetTag.LEAVES.getValues());
        CRIMSON_EYE_SPAWNABLE_ON_NOT.addAll(MaterialSetTag.LOGS.getValues());
        CRIMSON_EYE_SPAWNABLE_ON_NOT.addAll(MaterialSetTag.WART_BLOCKS.getValues());
        CRIMSON_EYE_SPAWNABLE_ON_NOT.addAll(MaterialSetTag.WARPED_STEMS.getValues());
        CRIMSON_EYE_SPAWNABLE_ON_NOT.addAll(MaterialSetTag.CRIMSON_STEMS.getValues());

        EMPTY = register(
                register(new NaturalMobSettings(null, 30, 0f) {
                    @Override
                    public @Nullable Entity spawn(@NotNull SpawnInfo info) {
                        return null;
                    }

                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        return true;
                    }
                }));
        CRIMSON_EYE = register(new NaturalMobSettings(AbyssMob.CRIMSON_EYE, 4, 0f) {
            @Override
            public @NotNull Entity spawn(@NotNull SpawnInfo info) {
                return spawn.spawn(info.getGame(), info.getBlock()
                        .getLocation().toCenterLocation().subtract(0, .5, 0));
            }

            @Override
            public boolean canSpawn(@NotNull SpawnInfo info) {
                Block b = info.getBlock();
                if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                    Block down = b.getRelative(BlockFace.DOWN);
                    if(!down.isSolid() || CRIMSON_EYE_SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                    for(BlockFace f : BlockFace.values()){
                        if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                        if(!down.getRelative(f).isSolid()) return false;
                    }
                    return b.getWorld().getNearbyEntities(b.getLocation(), 5D, 5D, 5D,
                            x -> CruxMob.is(x, spawn)).isEmpty();
                }
                return false;
            }

            @Override
            public int getGroupSize(@NotNull SpawnInfo info) {
                return CruxMath.random(1, 5);
            }
        });

        MOOSE = register(new NaturalMobSettings(AbyssMob.MOOSE, 7, 0f) {
            @Override
            public @NotNull Entity spawn(@NotNull SpawnInfo info) {
                return spawn.spawn(info.getGame(), info.getBlock()
                        .getLocation().toCenterLocation().subtract(0, .5, 0));
            }

            @Override
            public boolean canSpawn(@NotNull SpawnInfo info) {
                Block b = info.getBlock();
                if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                    Block down = b.getRelative(BlockFace.DOWN);
                    if(!down.isSolid() || CRIMSON_EYE_SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                    for(BlockFace f : BlockFace.values()){
                        if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                        if(!down.getRelative(f).isSolid()) return false;
                    }
                    return true;
                }
                return false;
            }
        });

        GROUND_DWELLER = register(new NaturalMobSettings(AbyssMob.GROUND_DWELLER, 8, 0f) {
            @Override
            public @NotNull Entity spawn(@NotNull SpawnInfo info) {
                return spawn.spawn(info.getGame(), info.getBlock()
                        .getLocation().toCenterLocation().subtract(0, .5, 0));
            }

            @Override
            public boolean canSpawn(@NotNull SpawnInfo info) {
                Block b = info.getBlock();
                if(b.isSolid()){
                    for(BlockFace f : BlockFace.values()){
                        if(!f.isCartesian()) continue;
                        if(!b.getRelative(f).isSolid()) return false;
                    }
                    int amount = 0;
                    for(Block pass : CruxLoc.getNearbyBlocks(b, 3)){
                        if(pass.isPassable() || pass.isEmpty()) amount++;
                    }
                    return amount > 8 && b.getWorld().getNearbyEntities(b.getLocation(), 5D, 5D, 5D,
                            x -> CruxMob.is(x, spawn)).isEmpty();
                }
                return false;
            }
        });

        CHARRED_BONES = register(new NaturalMobSettings(AbyssMob.CHARRED_BONES, 4, 0f) {
            @Override
            public @NotNull Entity spawn(@NotNull SpawnInfo info) {
                return spawn.spawn(info.getGame(), info.getBlock()
                        .getLocation().toCenterLocation().subtract(0, .5, 0));
            }

            @Override
            public boolean canSpawn(@NotNull SpawnInfo info) {
                Block b = info.getBlock();

                if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                    Block down = b.getRelative(BlockFace.DOWN);
                    if(!down.isSolid() || CRIMSON_EYE_SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                    for(BlockFace f : BlockFace.values()){
                        if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                        if(!down.getRelative(f).isSolid()) return false;
                    }
                    return b.getWorld().getNearbyEntities(b.getLocation(), 5D, 5D, 5D,
                            x -> CruxMob.is(x, spawn)).isEmpty();
                }
                return false;
            }

            @Override
            public int getGroupSize(@NotNull SpawnInfo info) {
                return CruxMath.random(1, 2);
            }
        });
    }

    protected boolean isPassableAndNotLiquid(@NotNull Block b){
        return b.isPassable() && !b.isLiquid();
    }

    protected final AbyssMob spawn;
    public NaturalMobSettings(AbyssMob spawn, int weight, float quality) {
        super(weight, quality);
        this.spawn = spawn;
    }

    public static @NotNull Collection<NaturalMobSettings> get(@NotNull SpawnInfo info, int rollsPer){
        Set<NaturalMobContainer> allThatCouldSpawn = new HashSet<>();
        for(NaturalMobContainer c : NaturalMobContainer.getRegistry()){
            if(c.canSpawn(info)) allThatCouldSpawn.add(c);
        }
        Collection<NaturalMobSettings> random = new HashSet<>();
        for(NaturalMobContainer c : allThatCouldSpawn){
            random.addAll(c.random(rollsPer, info));
        }
        return random;
    }

    public abstract @Nullable Entity spawn(@NotNull SpawnInfo info);

    public int getGroupSize(@NotNull SpawnInfo info){ return 1; }
    public int getGroupRadius(@NotNull SpawnInfo info){ return CruxMath.random(5, 10); }
    public abstract boolean canSpawn(@NotNull SpawnInfo info);
}

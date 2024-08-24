package killercreepr.cruxabyss.world.abyss.entity;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.data.util.CollectionBuilder;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.world.entity.AbyssNaturalEntitySpawn;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxworlds.world.entity.entity.NaturalEntitySpawn;
import killercreepr.cruxworlds.world.entity.entity.SpawnContext;
import killercreepr.cruxworlds.world.entity.entity.impl.SimpleNaturalEntitySpawn;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class StandardAbyssSpawns {
    public static final Collection<Material> SPAWNABLE_ON_NOT = new CollectionBuilder<Material>()
        .apply(builder ->{
            builder.addAll(MaterialSetTag.LEAVES.getValues());
            builder.addAll(MaterialSetTag.LOGS.getValues());
            builder.addAll(MaterialSetTag.WART_BLOCKS.getValues());
            builder.addAll(MaterialSetTag.WARPED_STEMS.getValues());
            builder.addAll(MaterialSetTag.CRIMSON_STEMS.getValues());
        })
        .build();

    public static final NaturalEntitySpawn EMPTY = new SimpleNaturalEntitySpawn(30, 0f) {
        @Nullable
        @Override
        public Entity spawn(@NotNull SpawnContext ctx) {
            return null;
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            return true;
        }
    };
    public static final NaturalEntitySpawn CRIMSON_EYE = new AbyssNaturalEntitySpawn(4, 0, AbyssMob.CRIMSON_EYE) {

        @Override
        public @NotNull Entity spawn(@NotNull SpawnContext ctx) {
            return mob.spawn(null, ctx.getBlock()
                .getLocation().toCenterLocation().subtract(0, .5, 0));
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return b.getWorld().getNearbyEntities(b.getLocation(), 5D, 5D, 5D,
                    x -> CruxMob.is(x, mob)).isEmpty();
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 5);
        }
    };
    public static final NaturalEntitySpawn MOOSE = new AbyssNaturalEntitySpawn(7, 0f, AbyssMob.MOOSE) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return true;
            }
            return false;
        }
    };

    public static final NaturalEntitySpawn GROUND_DWELLER = new AbyssNaturalEntitySpawn(8, 0f, AbyssMob.GROUND_DWELLER) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
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
                    x -> CruxMob.is(x, mob)).isEmpty();
            }
            return false;
        }
    };

    public static final NaturalEntitySpawn CHARRED_BONES = new AbyssNaturalEntitySpawn(4, 0f, AbyssMob.CHARRED_BONES) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();

            if(isPassableAndNotLiquid(b) && isPassableAndNotLiquid(b.getRelative(BlockFace.UP))){
                Block down = b.getRelative(BlockFace.DOWN);
                if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
                for(BlockFace f : BlockFace.values()){
                    if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                    if(!down.getRelative(f).isSolid()) return false;
                }
                return b.getWorld().getNearbyEntities(b.getLocation(), 5D, 5D, 5D,
                    x -> CruxMob.is(x, mob)).isEmpty();
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 2);
        }
    };
}

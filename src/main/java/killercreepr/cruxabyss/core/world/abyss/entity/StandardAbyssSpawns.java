package killercreepr.cruxabyss.core.world.abyss.entity;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.core.data.util.CollectionBuilder;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.world.entity.NaturalCruxMobSpawn;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawn;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawn;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

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
        public Entity spawn(@NotNull SpawnContext ctx, @Nullable Consumer<Entity> consumer) {
            return null;
        }

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            return true;
        }
    };
    public static final NaturalEntitySpawn ABYSSAL_EYE_VINE = new NaturalCruxMobSpawn(5, 0, AbyssMob.ABYSSAL_EYE_VINE) {

        @Override
        public @NotNull Entity spawn(@NotNull SpawnContext ctx, Consumer<Entity> consumer) {
            return mob.spawn(ctx.getBlock()
                .getLocation().toCenterLocation().subtract(0, .5, 0), consumer);
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
                return b.getWorld().getNearbyEntities(b.getLocation(), 10D, 10D, 10D,
                    x -> CruxMob.is(x, mob)).isEmpty() &&
                    CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 5, e -> CruxMob.is(e, mob)) < 6;
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 5);
        }
    };
    public static final NaturalEntitySpawn MOOSE = new NaturalCruxMobSpawn(7, 0f, AbyssMob.MOOSE) {
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

    public static final NaturalEntitySpawn GROUND_DWELLER = new NaturalCruxMobSpawn(8, 0f, AbyssMob.GROUND_DWELLER) {
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
                return amount > 8 && b.getWorld().getNearbyEntities(b.getLocation(), 10D, 10D, 10D,
                    x -> CruxMob.is(x, mob)).isEmpty() &&
                    CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
            }
            return false;
        }
    };

    public static final NaturalEntitySpawn CHARRED_BONES = new NaturalCruxMobSpawn(4, 0f, AbyssMob.CHARRED_BONES) {
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
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 8;
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 2);
        }
    };

    public static final NaturalEntitySpawn PLAGUE_STALKER = new NaturalCruxMobSpawn(4, 0f, AbyssMob.PLAGUE_STALKER) {
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
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, AbyssMob.PLAGUE_STALKER)) < 2;
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 2);
        }
    };

    public static final NaturalEntitySpawn PLAGUEWING = new NaturalCruxMobSpawn(4, 0f, AbyssMob.PLAGUEWING) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 7) return false;
            Block b = ctx.getBlock();
            if(!b.isEmpty()) return false;

            for(BlockFace f : BlockFace.values()){
                if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                if(!b.getRelative(f).isEmpty()) return false;
            }
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 9, e -> CruxMob.is(e, AbyssMob.PLAGUEWING)) < 2;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };

    public static final NaturalEntitySpawn EMBER_LEAPER = new NaturalCruxMobSpawn(3, 0f, AbyssMob.EMBER_LEAPER) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(!isPassableAndNotLiquid(b)) return false;
            int yCheck = 9;
            for(int y = 1; y <= yCheck; y++){
                Block check = b.getRelative(0, y, 0);
                if(!isPassableAndNotLiquid(check)) return false;
            }


            Block down = b.getRelative(BlockFace.DOWN);
            if(!down.isSolid() || SPAWNABLE_ON_NOT.contains(down.getType())) return false;
            for(BlockFace f : BlockFace.values()){
                if(!f.isCartesian() || f == BlockFace.UP || f == BlockFace.DOWN) continue;
                if(!down.getRelative(f).isSolid()) return false;
            }

            return b.getWorld().getNearbyEntities(b.getLocation(), 12D, 12D, 12D,
                x -> CruxMob.is(x, mob)).isEmpty() &&
                CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };

    protected static final Collection<Material> WATER = Set.of(
        Material.WATER,
        Material.KELP,
        Material.KELP_PLANT,
        Material.SEAGRASS,
        Material.TALL_SEAGRASS
    );
    private static boolean isWater(Block b){
        return WATER.contains(b.getType());
    }

    public static final NaturalEntitySpawn TOXINTRAWL = new NaturalCruxMobSpawn(3, 0f, AbyssMob.TOXINTRAWL) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(!isWater(b)) return false;

            for(Block check : CruxLoc.getNearbyBlocks(b, 3)){
                if(!isWater(check)) return false;
            }
            return b.getWorld().getNearbyEntities(b.getLocation(), 28D, 28D, 28D,
                x -> CruxMob.is(x, mob)).isEmpty();
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };

    public static final NaturalEntitySpawn ABYSSAL_HUSK = new NaturalCruxMobSpawn(3, 0f, AbyssMob.ABYSSAL_HUSK) {
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
                return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 6;
            }
            return false;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return CruxMath.random(1, 2);
        }
    };

    public static final NaturalEntitySpawn VOID_DWELLER = new NaturalCruxMobSpawn(3, 0f, AbyssMob.VOID_DWELLER) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            Block b = ctx.getBlock();
            if(!b.isEmpty()) return false;
            for(int i = 1; i < 8; i++){
                var check = b.getRelative(0, i, 0);
                if(!check.isEmpty()) return false;
            }
            return CruxEntityUtil.getEntityAmountNearChunk(b.getChunk(), 6, e -> CruxMob.is(e, mob)) < 2;
        }

        @Override
        public int getGroupSize(@NotNull SpawnContext ctx) {
            return 1;
        }
    };
}

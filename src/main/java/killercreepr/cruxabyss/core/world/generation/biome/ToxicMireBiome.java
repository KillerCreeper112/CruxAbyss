package killercreepr.cruxabyss.core.world.generation.biome;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.world.biome.BiomeManager;
import killercreepr.cruxabyss.core.world.generation.decoration.ToxicMireTreePopulator;
import killercreepr.cruxabyss.core.world.generation.populator.AbyssPopulator;
import killercreepr.cruxabyss.core.world.generation.populator.GrimPopulator;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.component.BushGroup;
import killercreepr.cruxblocks.api.block.component.BushType;
import killercreepr.cruxblocks.api.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import killercreepr.cruxblocks.core.registries.CruxBlocksRegistries;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Predicate;

public class ToxicMireBiome extends GrimBiome {
    public static final Predicate<BlockData> TOXIC_MIRE_DO_NOT_REPLACE = data ->{
        if(AbyssPopulator.GENERAL_DO_NOT_REPLACE.test(data)) return true;
        Material type = data.getMaterial();
        if(type == Material.AMETHYST_BLOCK || type == Material.BUDDING_AMETHYST ||
            type == Material.AMETHYST_CLUSTER ||
            type == Material.LARGE_AMETHYST_BUD||
            type == Material.SMALL_AMETHYST_BUD||
            type == Material.MEDIUM_AMETHYST_BUD){
            return true;
        }
        if(type == Material.SMOOTH_BASALT || type == Material.CALCITE){
            return true;
        }
        if(type == Material.SCULK || type == Material.SCULK_CATALYST ||
            type == Material.SCULK_SHRIEKER ||
            type == Material.SCULK_SENSOR ||
            type == Material.SCULK_VEIN){
            return true;
        }
        //if(type == Material.DEEPSLATE) return true;
        return false;
    };

    private final CruxNoise noise = CruxNoise.fast()
        .frequency(0.005f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(5)
        ;

    private final CruxNoise grasslandsNoise = CruxNoise.fast()
        .frequency(0.01f)  // Low frequency for large-scale features
        .noiseType(CruxNoise.NoiseType.Perlin)  // Use Perlin noise
        .fractalType(CruxNoise.FractalType.FBm)  // FBM for smooth transitions
        .fractalOctaves(6)  // Number of octaves for detail
        .fractalLacunarity(2.0f)  // Adjust roughness
        .fractalGain(0.5f);  // Strength of features
    private final float grasslandsThreshold = 0.35f;  // High threshold for rarity
    public final ToxicGrasslandsBiome grasslandsBiome;

    public final ToxicMireTreePopulator treePopulator = new ToxicMireTreePopulator();

    public ToxicMireBiome(@NotNull GrimPopulator master) {
        super(master);
        grasslandsBiome = new ToxicGrasslandsBiome(master);
    }

    public void acceptBiomeSet(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setBiome(BiomeManager.TOXIC_MIRE, limitedRegion, x,y,z);
    }

    public boolean isValidGrasslands(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        for(int i = 1; i <= 6; i++){
            if(!limitedRegion.isInRegion(x, y+i, z)) return false;
            if(!limitedRegion.getType(x, y+i, z).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Material m = limitedRegion.getType(x,y,z);
        acceptBiomeSet(worldInfo, random, limitedRegion, x, y, z);
        if(m == Material.BEDROCK) return;
        if(MaterialSetTag.LOGS.isTagged(m)){
            limitedRegion.setType(x, y, z, Material.AIR);
            /*CruxBlock block = AbyssBlocks.PLAGUE_STEM.getBlock(orientable.getAxis());
            if(block==null) return;
            block.setBlock(limitedRegion, x, y, z);*/
            if(limitedRegion.isInRegion(x, y-1, z)){
                BlockState state = limitedRegion.getBlockState(x, y-1, z);
                if(state.isCollidable() && random.nextBoolean()){
                    treePopulator.place(
                        worldInfo, random, limitedRegion,
                        new Location(null, x, y, z)
                    );
                }
            }
            return;
        }else if(MaterialSetTag.LEAVES.isTagged(m)){
            limitedRegion.setType(x, y, z, Material.AIR);
            //AbyssBlocks.PLAGUE_WART.getBaseBlock().setBlock(limitedRegion, x, y, z);
            //limitedRegion.setType(x,y,z, Material.NETHER_WART_BLOCK);
            if(limitedRegion.isInRegion(x,y+1,z) && limitedRegion.getType(x,y+1,z) == Material.SNOW){
                limitedRegion.setType(x,y+1,z, Material.AIR);
            }
            return;
        }else if(MaterialSetTag.FLOWERS.isTagged(m)){
            limitedRegion.setType(x,y,z,Material.AIR);
        }else if(MaterialSetTag.PLANKS.isTagged(m)){
            AbyssBlocks.PLAGUE_PLANKS.setBlock(limitedRegion, x, y, z);
            return;
        }else if(m == Material.BAMBOO){
            AbyssBlocks.PLAGUE_STEM.setBlock(limitedRegion, x, y, z);
            return;
        }
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(!b.isSolid()){
            decorationLogic(worldInfo, random, chunkX, chunkZ, limitedRegion, x, y, z);
            return;
        }

        if(TOXIC_MIRE_DO_NOT_REPLACE.test(limitedRegion.getBlockData(x,y,z))) return;

        //float n = noise.GetNoise(x,y,z);
        switch (m){
            default ->{
                if(CruxBlocksRegistries.BLOCK.getByBlockData(limitedRegion.getBlockData(x, y, z)) != null) return;

                if(isOre(b)){
                    if(!MaterialSetTag.DIAMOND_ORES.isTagged(b.getType())) return;
                }

                Block above = limitedRegion.isInRegion(x,y+1,z) ? limitedRegion.getBlockState(x,y+1,z).getBlock() : null;
                if(y >= 62 && above != null && (above.isEmpty() || above.isPassable())){
                    if(diamondOre(limitedRegion, x, y, z, AbyssBlocks.FUNGIRE_ORE)) return;
                    AbyssBlocks.PLAGUE_MOSS.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                }else if(y >= 58){
                    if(diamondOre(limitedRegion, x, y, z, AbyssBlocks.FUNGIRE_ORE)) return;
                    AbyssBlocks.PLAGUE_DIRT.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                } else{
                    if(diamondOre(limitedRegion, x, y, z, AbyssBlocks.PLAGUE_STONE_FUNGIRE_ORE)) return;
                    AbyssBlocks.PLAGUE_STONE.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                }
            }
        }
    }

    public boolean diamondOre(LimitedRegion limitedRegion, int x, int y, int z, CruxBlockGroup group){
        Material type = limitedRegion.getType(x,y,z);
        if(MaterialSetTag.DIAMOND_ORES.isTagged(type)){
            group.setBlock(limitedRegion, x, y, z);
            return true;
        }
        return false;
    }

    private void decorationLogic(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(grasslandsNoise.noise(x, y, z) > grasslandsThreshold){
            grasslandsBiome.accept(worldInfo, random, chunkX, chunkZ, limitedRegion, x, y, z);
            return;
        }

        if(CruxMath.testChance(1)){
            flowerLogic(limitedRegion, x, y, z);
            /*if(CruxMath.testChance(1)){
                rootsPatch(worldInfo, random, limitedRegion, x, y, z, CruxMath.random(3, 6));
            }else flowerLogic(limitedRegion, x, y, z);*/
        }else if(CruxMath.testChance(.64)){
            fungiLogic(limitedRegion, x, y, z);
        }
    }

    private void rootsPatch(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z, int size){
        if(!flowerLogic(limitedRegion, x, y, z)) return;

        for(int xAddon = -size; xAddon <= size; xAddon++){
            for(int zAddon = -size; zAddon <= size; zAddon++){
                if(CruxMath.testChance(35)) continue;
                CruxPosition pos = CruxPosition.block(x + xAddon, y, z + zAddon);
                master.function(worldInfo, random, limitedRegion, pos,
                    new GrimPopulator.ChunkFunction() {
                        @Override
                        public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                            CruxPosition validPos = findValidRootPosition(limitedRegion, pos.blockX(), pos.blockY(), pos.blockZ());
                            if(validPos == null) return;
                            flowerLogic(limitedRegion, validPos.blockX(), validPos.blockY(), validPos.blockZ());
                        }
                    });
            }
        }
    }

    private CruxPosition findValidRootPosition(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.getType(x, y, z).isEmpty()){
            if(!limitedRegion.isInRegion(x, y+1, z)) return null;
            if(!limitedRegion.getType(x, y, z).isSolid()) return null;
            if(!limitedRegion.getType(x, y+1, z).isEmpty()) return null;
            return CruxPosition.block(x, y+1, z);
        }
        if(limitedRegion.isInRegion(x, y-1, z)){
            if(limitedRegion.getType(x, y-1, z).isSolid()){
                return CruxPosition.block(x, y, z);
            }
        }
        return null;
    }

    public boolean isLiquid(Material m){
        return m == Material.WATER || m == Material.LAVA;
    }

    private boolean fungiLogic(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.isInRegion(x, y-1, z)) return false;
        var ground = limitedRegion.getType(x, y-1, z);
        if(!ground.isSolid() || isLiquid(ground)) return false;
        if(!isReplaceable(limitedRegion, x, y, z)) return false;
        CruxBlockGroup block;
        int size;
        if(CruxMath.testChance(25)){
            size = 2;
            block = AbyssBlocks.TALL_PLAGUE_SHROOM;
        }else if(CruxMath.testChance(10)){
            AbyssBlocks.MIREHORN.setBlock(limitedRegion, x, y, z);
            return true;
        }else if(CruxMath.testChance(3)){
            AbyssBlocks.TOXSPORE.setBlock(limitedRegion, x, y, z);
            return true;
        }else{
            AbyssBlocks.PLAGUE_SHROOM.setBlock(limitedRegion, x, y, z);
            return true;
        }
        return attemptFlowerPlace(limitedRegion, x, y, z, size, block);
    }

    public boolean isReplaceable(LimitedRegion region, int x, int y, int z){
        if(!region.isInRegion(x, y, z)) return false;
        Block b = region.getBlockState(x, y, z).getBlock();
        if(b.isLiquid()) return false;
        return b.isEmpty() || b.isReplaceable();
    }

    private boolean flowerLogic(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.isInRegion(x, y-1, z)) return false;
        var ground = limitedRegion.getType(x, y-1, z);
        if(!ground.isSolid() || isLiquid(ground)) return false;
        if(!isReplaceable(limitedRegion, x, y, z)) return false;

        CruxBlockGroup block;
        int size;

        size = CruxMath.random(2,4);
        block = AbyssBlocks.PLAGUE_ROOTS;
        return attemptFlowerPlace(limitedRegion, x, y, z, size, block);
    }

    private boolean attemptFlowerPlace(@NotNull LimitedRegion limitedRegion, int x, int y, int z,
                                       int size, @NotNull CruxBlockGroup block){
        for(int i = 0; i < size; i++){
            if(!isReplaceable(limitedRegion,x, y + i, z)) return false;
        }

        return setFlower(limitedRegion, x, y, z, size, block);
    }

    private void setDefaultFlower(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setFlower(limitedRegion, x, y, z, CruxMath.random(2, 4), AbyssBlocks.PLAGUE_ROOTS);
    }

    private boolean setFlower(@NotNull LimitedRegion region, int x, int y, int z, int length, @NotNull CruxBlockGroup bushBlock){
        //make sure it's all in region first
        /*for(int i = 0; i < length; i++){
            if(!region.isInRegion(x, y + i, z)) return false;
        }*/
        BushGroup bush = bushBlock.getComponents().get(CruxBlockComponents.BUSH_GROUP);
        for(int i = 0; i < length; i++){
            CruxBlock block;
            if(i == 0) block = bush.getBlock(BushType.BOTTOM);
            else if((i+1) == length) block = bush.getBlock(BushType.TOP);
            else block = bush.getBlock(BushType.MIDDLE);
            block.setBlock(region, x, y + i, z);
        }
        return true;
    }
}

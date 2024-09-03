package killercreepr.cruxabyss.world.generation.biome;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.generation.decoration.ToxicMireTreePopulator;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.standard.BushType;
import killercreepr.cruxblocks.block.standard.group.BushBlockGroup;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ToxicMireBiome extends GrimBiome {
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
    private final float grasslandsThreshold = 0.2f;  // High threshold for rarity
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
        }else if(MaterialSetTag.SMALL_FLOWERS.isTagged(m)){
            limitedRegion.setType(x,y,z,Material.AIR);
        }else if(MaterialSetTag.TALL_FLOWERS.isTagged(m)){
            limitedRegion.setType(x,y,z,Material.AIR);
        }
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        if(!b.isSolid()){
            decorationLogic(worldInfo, random, chunkX, chunkZ, limitedRegion, x, y, z);
            return;
        }

        //float n = noise.GetNoise(x,y,z);
        switch (m){
            default ->{
                if(CruxBlocksRegistries.BLOCKS.getByBlockData(limitedRegion.getBlockData(x, y, z)) != null) return;
                if(y >= 62 && limitedRegion.isInRegion(x,y+1,z) && limitedRegion.getType(x,y+1,z) == Material.AIR){
                    AbyssBlocks.PLAGUE_MOSS.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                }else if(y >= 58){
                    AbyssBlocks.PLAGUE_MOSS_DIRT.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                } else{
                    AbyssBlocks.PLAGUE_STONE.getBaseBlock().setBlock(
                        limitedRegion, x, y, z
                    );
                }
            }
        }
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
        }
    }

    private void rootsPatch(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z, int size){
        if(!flowerLogic(limitedRegion, x, y, z)) return;
        Bukkit.broadcastMessage("roots patch " + x + " " + y + " " + z);

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

    private boolean flowerLogic(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        if(!limitedRegion.isInRegion(x, y-1, z)) return false;
        if(!limitedRegion.getType(x, y-1, z).isSolid()) return false;
        int size = CruxMath.random(2, 4);
        for(int i = 0; i < size; i++){
            if(!limitedRegion.isInRegion(x, y + i, z)) return false;
            Material m = limitedRegion.getType(x, y + i, z);
            if(!m.isEmpty()) return false;
        }

        setFlower(limitedRegion, x, y, z, size, AbyssBlocks.PLAGUE_ROOTS);
        return true;
    }

    private void setDefaultFlower(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setFlower(limitedRegion, x, y, z, CruxMath.random(2, 4), AbyssBlocks.PLAGUE_ROOTS);
    }

    private boolean setFlower(@NotNull LimitedRegion region, int x, int y, int z, int length, @NotNull BushBlockGroup bush){
        //make sure it's all in region first
        for(int i = 0; i < length; i++){
            if(!region.isInRegion(x, y + i, z)) return false;
        }
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

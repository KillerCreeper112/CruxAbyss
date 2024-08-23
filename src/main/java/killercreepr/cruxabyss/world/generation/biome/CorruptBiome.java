package killercreepr.cruxabyss.world.generation.biome;

import com.destroystokyo.paper.MaterialSetTag;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxabyss.world.generation.BlockGenerator;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import killercreepr.cruxgeneration.util.FastNoiseLite;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CorruptBiome extends GrimBiome {

    private final FastNoiseLite spikeNoise = new FastNoiseLite();

    private final Map<Material, BlockGenerator> replace = new HashMap<>();
    public CorruptBiome(@NotNull GrimPopulator master) {
        super(master);
        spikeNoise.SetFrequency(.09f);
        spikeNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        spikeNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        spikeNoise.SetFractalOctaves(3);

        BlockGenerator gen = new BlockGenerator() {
            @Override
            public void set(@NotNull LimitedRegion region, int x, int y, int z) {
                region.setType(x,y,z,Material.MYCELIUM);
            }
        };
        for(Material m : MaterialSetTag.SAND.getValues()){
            replace.put(m, gen);
        }
        replace.put(Material.GRASS_BLOCK, gen);
    }

    public void acceptBiomeSet(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, int x, int y, int z){
        setBiome(BiomeManager.CORRUPT, limitedRegion, x,y,z);
    }

    @Override
    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion, int x, int y, int z) {
        Block b = limitedRegion.getBlockState(x,y,z).getBlock();
        acceptBiomeSet(worldInfo, random, limitedRegion, x, y, z);
        if(!b.isSolid() || b.getType() == Material.BEDROCK) return;
        BlockGenerator gen = replace.getOrDefault(limitedRegion.getType(x,y,z), null);
        if(gen == null) return;
        gen.set(limitedRegion,x,y,z);
        if(limitedRegion.getType(x,y,z) == Material.MYCELIUM && CruxMath.testChance(6.5) && limitedRegion.getType(x,y+1,z).isEmpty() &&
                limitedRegion.getType(x,y+2,z).isEmpty()){
            generateSpike(worldInfo, random, limitedRegion, x,y,z);
        }
    }


    private void generateSpike(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion region, int x, int y, int z){
        final Material block = CruxMath.random(1, 100) <= 50 ? Material.RED_MUSHROOM_BLOCK :  Material.BROWN_MUSHROOM_BLOCK;
        float size = CruxMath.random(1f, 3f);
        float minus = CruxMath.random(.1f, .5f);
        Location l = new Location(region.getWorld(), x,y,z);
        l.setPitch(CruxMath.random(-90f, -35f));
        l.setYaw(CruxMath.random(-180f, 180f));
        for(; size > 0; size -= minus){
            l = CruxLoc.shift(l, 1D, 0D, 0D);
            for (float xx = -size; xx <= size; xx += minus) {
                for (float yy = -size; yy <= size; yy += minus) {
                    for (float zz = -size; zz <= size; zz += minus) {
                        double equationResult = Math.pow(xx, 2) / Math.pow(size, 2)
                                + Math.pow(yy, 2) / Math.pow(size, 2)
                                + Math.pow(zz, 2) / Math.pow(size, 2);
                        final CruxPosition place = CruxPosition.block(l.clone().add(xx, yy, zz));
                        if (equationResult <= 1 + .7 * spikeNoise.GetNoise((float) place.x(), (float) place.y(), (float) place.z())) {
                            master.function(worldInfo, random, region, place, new GrimPopulator.ChunkFunction() {
                                @Override
                                public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                                    limitedRegion.setType(place.blockX(),
                                            place.blockY(), place.blockZ(), block);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

}

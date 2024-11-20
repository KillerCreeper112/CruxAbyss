package killercreepr.cruxabyss.world.generation.decoration;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.world.generation.BlockGenerator;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import killercreepr.cruxgeneration.util.CruxNoise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RockPopulator extends GrimPopulator {
    private final BlockGenerator ore;
    private final CruxNoise rockNoise = CruxNoise.fast()
        .frequency(.09f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(3)
        ;
    private final CruxNoise rockSpawnNoise = CruxNoise.fast()
        .frequency(.01f)
        .noiseType(CruxNoise.NoiseType.OpenSimplex2)
        .fractalType(CruxNoise.FractalType.FBm)
        .fractalOctaves(2)
        ;
    private final Set<Biome> biomes = Set.of(Biome.values());
    private final Set<Material> passMaterials = new HashSet<>();

    public RockPopulator(@NotNull BlockGenerator ore) {
        this.ore = ore;

        passMaterials.addAll(Tag.LEAVES.getValues());
        passMaterials.addAll(Tag.LOGS.getValues());
    }

    @Override
    public void populateBeforeFunctions(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if(CruxMath.random(1, 100) <= 85) return;
        int spawned = 0;
        int max = CruxMath.random(1, 6);
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int xx = x + (chunkX * 16);
                int zz = z + (chunkZ * 16);
                if(spawned > 0){
                    if(CruxMath.random(1, 100) <= 70) continue;
                    float noise = rockSpawnNoise.noise(xx, zz);
                    if(noise < .6) continue;
                }
                for(int y = worldInfo.getMaxHeight()-10; y > 65; y--){
                    if(canSpawnRock(limitedRegion, xx, y, zz)){
                        generateRock(worldInfo, random, limitedRegion, new Location(limitedRegion.getWorld(), xx, y+CruxMath.random(0,1), zz));
                        spawned++;
                        if(spawned >= max) return;
                        break;
                    }
                }
            }
        }
    }

    private boolean canSpawnRock(@NotNull LimitedRegion limitedRegion, int x, int y, int z){
        Block s = limitedRegion.getBlockState(x, y, z).getBlock();
        return biomes.contains(s.getBiome()) && !pass(s) && s.isSolid() && pass(limitedRegion.getBlockState(x, y+1, z).getBlock()) &&
                pass(limitedRegion.getBlockState(x, y+2, z).getBlock()) &&
                //check for solid ground
                limitedRegion.getBlockState(x+1, y, z).getBlock().isSolid() &&
                limitedRegion.getBlockState(x, y, z+1).getBlock().isSolid() &&
                limitedRegion.getBlockState(x-1, y, z).getBlock().isSolid() &&
                limitedRegion.getBlockState(x, y, z-1).getBlock().isSolid();
    }

    private boolean pass(@NotNull Block b){
        return b.isEmpty() || b.isPassable() || b.isReplaceable() || passMaterials.contains(b.getType());
    }

    private void generateRock(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, @NotNull Location center){
        int width = CruxMath.random(2, 4);
        int length = CruxMath.random(2, 4);
        int height = CruxMath.random(2, 4);

        for (int x = -width; x <= width; x++) {
            for (int y = -height; y <= height; y++) {
                for (int z = -length; z <= length; z++) {
                    Location rel = center.clone().add(x, y, z);
                    double equationResult = Math.pow(x, 2) / Math.pow(width, 2)
                            + Math.pow(y, 2) / Math.pow(height, 2)
                            + Math.pow(z, 2) / Math.pow(length, 2);
                    if (equationResult <= 1 + 0.7 * rockNoise.noise((float) rel.getX(), (float) rel.getY(), (float) rel.getZ())) {
                        ChunkFunction function = new ChunkFunction() {
                            @Override
                            public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                                if(CruxMath.random(1, 100) <= 35) ore.set(limitedRegion, rel.getBlockX(), rel.getBlockY(), rel.getBlockZ());
                                else limitedRegion.setType(rel.getBlockX(), rel.getBlockY(), rel.getBlockZ(), Material.STONE);
                            }
                        };
                        function(worldInfo, random, limitedRegion, CruxPosition.block(rel), function);
                    }
                }
            }
        }
    }
}

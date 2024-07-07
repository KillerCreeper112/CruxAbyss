package killercreepr.cruxabyss.world;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GrimGameChunkGenerator extends ChunkGenerator {
    private final FastNoiseLite terrainNoise = new FastNoiseLite();
    private final FastNoiseLite detailNoise = new FastNoiseLite();

    private final FastNoiseLite erosion = new FastNoiseLite();
    private final FastNoiseLite continentalness = new FastNoiseLite();
    private final FastNoiseLite peaks = new FastNoiseLite();

    private final HashMap<Integer, List<Material>> layers = new HashMap<>() {{
        put(0, List.of(Material.GRASS_BLOCK));
        put(1, List.of(Material.DIRT, Material.COARSE_DIRT, Material.SAND, Material.GRAVEL));
        put(2, List.of(Material.COAL_ORE, Material.IRON_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE));
        put(3, List.of(Material.BEDROCK));
    }};


    public GrimGameChunkGenerator() {
        // Set frequencies
        /*erosion.SetFrequency(.002f);
        peaks.SetFrequency(.0005f);
        continentalness.SetFrequency(.0001f);
        terrainNoise.SetFrequency(0.005f);
        detailNoise.SetFrequency(0.03f);*/
        erosion.SetFrequency(.002f);
        peaks.SetFrequency(.0005f);
        continentalness.SetFrequency(.0001f);
        terrainNoise.SetFrequency(0.006f);
        detailNoise.SetFrequency(0.03f);
        detailNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);

        // Add fractals
        terrainNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        terrainNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        terrainNoise.SetFractalOctaves(5);

        erosion.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        erosion.SetFractalType(FastNoiseLite.FractalType.Ridged);
        erosion.SetFractalOctaves(3);

        continentalness.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        continentalness.SetFractalType(FastNoiseLite.FractalType.FBm);
        continentalness.SetFractalOctaves(6);

        peaks.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        peaks.SetFractalType(FastNoiseLite.FractalType.FBm);
        peaks.SetFractalOctaves(4);
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                for(int y = chunkData.getMaxHeight(); y > chunkData.getMinHeight(); y--){
                    if(chunkData.getBlockData(x, y, z).getMaterial().isSolid()){
                        chunkData.setBlock(x, y+1, z, layers.get(0).get(0));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                chunkData.setBlock(x, chunkData.getMinHeight(), z, layers.get(3).get(random.nextInt(layers.get(3).size())));
            }
        }
    }



    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        for(int y = chunkData.getMinHeight(); y < chunkData.getMaxHeight(); y++) {
            for(int x = 0; x < 16; x++) {
                for(int z = 0; z < 16; z++) {
                    float noise2 = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 2) +
                            (detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) / 10);
                    float noise3 = detailNoise.GetNoise(x + (chunkX * 16), y, z + (chunkZ * 16)) +
                            (erosion.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) / 20);

                    float pe = peaks.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 5;
                    float co = continentalness.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 2;
                    float er = erosion.GetNoise(x + (chunkX * 16), z + (chunkZ * 16));

                    float currentY = (62 + ((noise2 * 10) + ((pe + co + er) * 20)));
                    if(y < currentY) {
                        float distanceToSurface = Math.abs(y - currentY); // The absolute y distance to the world surface.
                        double function = .1 * Math.pow(distanceToSurface, 2) - 1; // A second grade polynomial offset to the noise max and min (1, -1).
                        if(noise3 > Math.min(function, -.3)){
                            if(distanceToSurface < 5) {
                                chunkData.setBlock(x, y, z, layers.get(1).get(random.nextInt(layers.get(1).size())));
                            }

                            // Not close to the surface at all.
                            else {
                                Material neighbour = Material.STONE;
                                List<Material> neighbourBlocks = new ArrayList<>(List.of(chunkData.getType(Math.max(x - 1, 0), y, z), chunkData.getType(x, Math.max(y - 1, 0), z), chunkData.getType(x, y, Math.max(z - 1, 0)))); // A list of all neighbour blocks.

                                // Randomly place vein anchors.
                                if(random.nextFloat() < 0.002) {
                                    neighbour = layers.get(2).get(Math.min(random.nextInt(layers.get(2).size()), random.nextInt(layers.get(2).size()))); // A basic way to shift probability to lower values.
                                }

                                // If the current block has an ore block as neighbour, try the current block.
                                if((!Collections.disjoint(neighbourBlocks, layers.get(2)))) {
                                    for (Material neighbourBlock : neighbourBlocks) {
                                        if (layers.get(2).contains(neighbourBlock) && random.nextFloat() < -0.01 * layers.get(2).indexOf(neighbourBlock) + 0.4) {
                                            neighbour = neighbourBlock;
                                        }
                                    }
                                }

                                chunkData.setBlock(x, y, z, neighbour);
                            }
                        }
                    } else if(y < 62) chunkData.setBlock(x, y, z, Material.WATER);
                }
            }
        }
    }
}

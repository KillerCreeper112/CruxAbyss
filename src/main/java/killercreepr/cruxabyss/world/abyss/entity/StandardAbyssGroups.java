package killercreepr.cruxabyss.world.abyss.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.nms.biome.BiomeUtils;
import killercreepr.crux.registry.Registry;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxworlds.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.world.entity.SpawnContext;
import killercreepr.cruxworlds.world.entity.impl.NaturalSpawnPartGroup;
import killercreepr.cruxworlds.world.entity.impl.SimpleNaturalEntitySpawnGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class StandardAbyssGroups {
    public static final NaturalEntitySpawnGroup EMPTY = new SimpleNaturalEntitySpawnGroup(50, 0f) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            return true;
        }
    };
    public static final NaturalEntitySpawnGroup ABYSSAL_EYE_VINE = new NaturalSpawnPartGroup(10, 0f,
        StandardAbyssSpawns.ABYSSAL_EYE_VINE){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 12) return false;
            Block b = ctx.getBlock();
            NamespacedKey k = BiomeUtils.getBiome(b);
            return k.equals(Crux.key("toxic_mire")) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
        }
    };
    public static final NaturalEntitySpawnGroup MOOSE = new NaturalSpawnPartGroup(6, 0f,
        StandardAbyssSpawns.MOOSE) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 8) return false;
            Block b = ctx.getBlock();
            Biome biome = b.getBiome();
            Set<Biome> biomes = Set.of(Biome.PLAINS, Biome.BIRCH_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
                Biome.SUNFLOWER_PLAINS, Biome.FLOWER_FOREST);
            return biomes.contains(biome) && getEntityAmountNearChunk(b.getChunk(), 6) == 0;
        }
    };

    public static final NaturalEntitySpawnGroup GROUND_DWELLER = new NaturalSpawnPartGroup(12, 0f,
        StandardAbyssSpawns.GROUND_DWELLER) {
        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 25) return false;
            boolean biome = false;
            Block b = ctx.getBlock();
            switch (b.getBiome()){
                case BEACH, SNOWY_BEACH, SAVANNA, SAVANNA_PLATEAU, WINDSWEPT_SAVANNA, BADLANDS,
                     ERODED_BADLANDS, WOODED_BADLANDS, DESERT -> biome = true;
                case CUSTOM -> {
                    NamespacedKey k = BiomeUtils.getBiome(b);
                    biome = k.equals(Crux.key("corruption"));
                }
            }
            return biome && getEntityAmountNearChunk(b.getChunk(), 8) < 4;
        }
    };

    public static void register(@NotNull Registry<NaturalEntitySpawnGroup> registry){
        registry.register(EMPTY);
        registry.register(ABYSSAL_EYE_VINE);
        registry.register(MOOSE);
        registry.register(GROUND_DWELLER);
    }
}

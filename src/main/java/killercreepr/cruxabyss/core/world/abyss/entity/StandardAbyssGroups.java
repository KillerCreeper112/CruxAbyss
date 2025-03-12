package killercreepr.cruxabyss.core.world.abyss.entity;

import killercreepr.crux.api.registry.Registry;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.paper.nms.biome.BiomeUtils;
import killercreepr.cruxabyss.core.world.biome.BiomeManager;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.NaturalSpawnPartGroup;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawnGroup;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
            return k.equals(BiomeManager.TOXIC_MIRE.key()) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
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
            Biome bb = b.getBiome();
            if(isBiome(bb, Biome.BEACH, Biome.SNOWY_BEACH, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.WINDSWEPT_SAVANNA,
                Biome.BADLANDS, Biome.ERODED_BADLANDS, Biome.WOODED_BADLANDS, Biome.DESERT)){
                biome = true;
            }else if(bb.key().equals(BiomeManager.FUNGAL_GROVE.key())){
                biome = true;
            }
            return biome && getEntityAmountNearChunk(b.getChunk(), 8) < 4;
        }
    };

    public static boolean isBiome(Biome biome, Biome... biomes){
        return Arrays.stream(biomes).anyMatch(b -> b.key().equals(biome.key()));
    }

    public static final NaturalEntitySpawnGroup CHARRED_BONES = new NaturalSpawnPartGroup(10, 0f,
        StandardAbyssSpawns.CHARRED_BONES){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 12) return false;
            Block b = ctx.getBlock();
            NamespacedKey k = BiomeUtils.getBiome(b);
            return k.equals(BiomeManager.CHARRED_WASTES.key()) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
        }
    };

    public static final NaturalEntitySpawnGroup PLAGUE_STALKER = new NaturalSpawnPartGroup(10, 0f,
        StandardAbyssSpawns.PLAGUE_STALKER){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 8) return false;
            Block b = ctx.getBlock();
            NamespacedKey k = BiomeUtils.getBiome(b);
            return k.equals(BiomeManager.TOXIC_GRASSLANDS.key()) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
        }
    };

    public static final NaturalEntitySpawnGroup PLAGUEWING = new NaturalSpawnPartGroup(3, 0f,
        StandardAbyssSpawns.PLAGUEWING){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 2) return false;
            Block b = ctx.getBlock();
            if(b.getY() < 150) return false;
            if(!b.isEmpty()) return false;
            NamespacedKey k = BiomeUtils.getBiome(b);
            return BiomeManager.isToxicMireType(k) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
        }
    };

    public static final NaturalEntitySpawnGroup EMBER_LEAPER = new NaturalSpawnPartGroup(8, 0f,
        StandardAbyssSpawns.EMBER_LEAPER){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 9) return false;
            Block b = ctx.getBlock();
            NamespacedKey k = BiomeUtils.getBiome(b);
            return k.equals(BiomeManager.CHARRED_WASTES.key()) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
        }
    };

    public static final NaturalEntitySpawnGroup TOXINTRAWL = new NaturalSpawnPartGroup(8, 0f,
        StandardAbyssSpawns.TOXINTRAWL){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 9) return false;
            Block b = ctx.getBlock();
            NamespacedKey k = BiomeUtils.getBiome(b);
            return BiomeManager.isToxicMireType(k) && getEntityAmountNearChunk(b.getChunk(), 4) < 16;
        }
    };

    public static final NaturalEntitySpawnGroup ABYSSAL_HUSK = new NaturalSpawnPartGroup(10, 0f,
        StandardAbyssSpawns.ABYSSAL_HUSK){

        @Override
        public boolean canSpawn(@NotNull SpawnContext ctx) {
            if(CruxMath.random(1, 100) <= 6) return false;
            World world = ctx.getWorld();
            //if it's not night, return
            if(!(world.getTime() >= 13_000 && world.getTime() <= 23_000)) return false;
            Block b = ctx.getBlock();
            NamespacedKey k = BiomeUtils.getBiome(b);
            return BiomeManager.isToxicMireType(k) && getEntityAmountNearChunk(b.getChunk(), 6) < 12;
        }
    };

    public static void register(@NotNull Registry<NaturalEntitySpawnGroup> registry){
        registry.register(EMPTY);
        registry.register(ABYSSAL_EYE_VINE);
        registry.register(MOOSE);
        registry.register(GROUND_DWELLER);
        registry.register(CHARRED_BONES);
        registry.register(PLAGUE_STALKER);
        registry.register(PLAGUEWING);
        registry.register(EMBER_LEAPER);
        registry.register(TOXINTRAWL);
        registry.register(ABYSSAL_HUSK);
    }
}

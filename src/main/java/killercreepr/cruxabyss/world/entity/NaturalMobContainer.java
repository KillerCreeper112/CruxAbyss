package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.loot.SimpleWeighted;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.world.biome.BiomeManager;
import killercreepr.cruxentities.entity.CruxMob;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public abstract class NaturalMobContainer extends SimpleWeighted implements Listener {
    private static final Set<NaturalMobContainer> REGISTRY = new HashSet<>();
    public static @NotNull Set<NaturalMobContainer> getRegistry(){ return REGISTRY; }

    public static <T extends NaturalMobContainer> T register(@NotNull T e){
        REGISTRY.add(e);
        return e;
    }

    public static void register(){
        //empty
        register(
                new NaturalMobContainer(50, 0f) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        return true;
                    }
                }
        );

        register(
                new NaturalMobContainer(10, 0, NaturalMobSettings.CRIMSON_EYE) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 12) return false;
                        NamespacedKey k = BiomeManager.getBiome(info.getBlock());
                        return k.equals(Crux.key("crimson")) && getNearbyChunkEntitySpawns(info.getBlock().getChunk(), 4).size() < 16;
                    }
                }
        );


        register(
                new NaturalMobContainer(6, 0, NaturalMobSettings.MOOSE) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 8) return false;
                        Biome biome = info.getBlock().getBiome();
                        Set<Biome> biomes = Set.of(Biome.PLAINS, Biome.BIRCH_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
                                Biome.SUNFLOWER_PLAINS, Biome.FLOWER_FOREST);
                        return biomes.contains(biome) && getNearbyChunkEntitySpawns(info.getBlock().getChunk(), 6).isEmpty();
                    }
                }
        );

        register(
                new NaturalMobContainer(12, 0, NaturalMobSettings.GROUND_DWELLER) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 25) return false;
                        boolean biome = false;
                        switch (info.getBlock().getBiome()){
                            case BEACH, SNOWY_BEACH, SAVANNA, SAVANNA_PLATEAU, WINDSWEPT_SAVANNA, BADLANDS,
                                    ERODED_BADLANDS, WOODED_BADLANDS, DESERT -> biome = true;
                            case CUSTOM -> {
                                NamespacedKey k = BiomeManager.getBiome(info.getBlock());
                                biome = k.equals(Crux.key("corruption"));
                            }
                        }
                        return biome && getNearbyChunkEntitySpawns(info.getBlock().getChunk(), 8).size() < 4;
                    }
                }
        );

        register(
                new NaturalMobContainer(5, 0, NaturalMobSettings.CHARRED_BONES) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 60) return false;
                        NamespacedKey k = BiomeManager.getBiome(info.getBlock());
                        return k.equals(Crux.key("charred_wastes")) && getNearbyChunkEntitySpawns(info.getBlock().getChunk(), 8).size() < 6;
                    }
                }
        );
    }

    protected @NotNull Collection<Entity> getNearbyChunkEntitySpawns(@NotNull Chunk chunk, int radius){
        return getNearbyChunkEntitySpawns(chunk, radius, null);
    }

    protected @NotNull Collection<Entity> getNearbyChunkEntitySpawns(@NotNull Chunk chunk, int radius, @Nullable Predicate<Entity> predicate){
        return getNearbyChunkEntities(chunk, radius, e ->{
            if(predicate != null && !predicate.test(e)) return false;
            for(NaturalMobSettings s : spawns){
                if(CruxMob.is(e, s.spawn)) return true;
            }
            return false;
        });
    }

    protected @NotNull Collection<Entity> getNearbyChunkEntities(@NotNull Chunk chunk, int radius, @Nullable Predicate<Entity> predicate){
        Collection<Entity> list = new HashSet<>();
        for(int x = -radius; x < radius; x++){
            for(int z = -radius; z < radius; z++){
                if(!chunk.getWorld().isChunkLoaded(chunk.getX()+x, chunk.getZ()+z)) continue;
                Chunk c = chunk.getWorld().getChunkAt(chunk.getX()+x, chunk.getZ()+z);
                if(predicate == null) list.addAll(List.of(c.getEntities()));
                else{
                    for(Entity e : c.getEntities()){
                        if(predicate.test(e)) list.add(e);
                    }
                }
            }
        }
        return list;
    }

    private final Collection<NaturalMobSettings> spawns = new HashSet<>();

    public NaturalMobContainer(int weight, float quality, @NotNull NaturalMobSettings... spawns) {
        super(weight, quality);
        this.spawns.addAll(List.of(spawns));
    }

    public @NotNull Collection<NaturalMobSettings> getSpawns() {
        return spawns;
    }

    public static @NotNull List<Entity> spawn(@NotNull Collection<NaturalMobSettings> poll, @NotNull SpawnInfo info){
        List<Entity> list = new ArrayList<>();
        for(NaturalMobSettings s : poll){
            int spawned = 0;
            int maxGroup = s.getGroupSize(info);
            int groupRadius = s.getGroupRadius(info);
            for(int i = 0; i < maxGroup; i++){
                Entity e;
                if(spawned < 1){
                    if(s.canSpawn(info)) e = s.spawn(info);
                    else e = null;
                }else{
                    e = null;
                    boolean breakCompletely = false;
                    for(int x = groupRadius; x >= -groupRadius; --x) {
                        if(breakCompletely) break;
                        for(int y = groupRadius; y >= -groupRadius; --y) {
                            if(breakCompletely) break;
                            for(int z = groupRadius; z >= -groupRadius; --z) {
                                Block b = info.getBlock().getRelative(x,y,z);
                                if(b.equals(info.getBlock())) continue;
                                SpawnInfo groupInfo = new SpawnInfo(b, info.getGame());
                                if(s.canSpawn(groupInfo)){
                                    e = s.spawn(groupInfo);
                                    breakCompletely=true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(e == null && spawned == 0) break;
                if(e != null){
                    spawned++;
                    list.add(e);
                }
            }
        }
        return list;
    }

    public abstract boolean canSpawn(@NotNull SpawnInfo info);

    public static @NotNull List<NaturalMobContainer> randomContainer(int rolls, @NotNull SpawnInfo info){
        return randomContainer(REGISTRY, rolls, info);
    }

    public static @NotNull List<NaturalMobContainer> randomContainer(@NotNull Collection<NaturalMobContainer> poll, int rolls, @NotNull SpawnInfo info){
        List<NaturalMobContainer> list = new ArrayList<>();
        Map<NaturalMobContainer, Integer> data = new HashMap<>();
        for(NaturalMobContainer p : poll){
            data.put(p, p.getWeight());
        }
        for(int i = 0; i < rolls; i++){
            int totalWeight = 0;
            int weight;
            for(NaturalMobContainer item : new HashSet<>(data.keySet())){
                if(!item.canSpawn(info)){
                    data.remove(item);
                    continue;
                }
                weight = item.getWeight();
                if(weight < 1){
                    data.remove(item);
                    continue;
                }
                totalWeight += weight;
                data.put(item, weight);
            }
            int chance = CruxMath.random(0, totalWeight);
            for(Map.Entry<NaturalMobContainer, Integer> entry : new HashSet<>(data.entrySet())){
                if(chance <= entry.getValue()){
                    data.remove(entry.getKey());
                    list.add(entry.getKey());
                    break;
                }
                chance -= entry.getValue();
            }
        }
        return list;
    }

    public @NotNull List<NaturalMobSettings> random(int rolls, @NotNull SpawnInfo info){
        return random(spawns, rolls, info);
    }

    public static @NotNull List<NaturalMobSettings> random(@NotNull Collection<NaturalMobSettings> poll, int rolls, @NotNull SpawnInfo info){
        List<NaturalMobSettings> list = new ArrayList<>();
        Map<NaturalMobSettings, Integer> data = new HashMap<>();
        for(NaturalMobSettings p : poll){
            data.put(p, p.getWeight());
        }
        for(int i = 0; i < rolls; i++){
            int totalWeight = 0;
            int weight;
            for(NaturalMobSettings item : new HashSet<>(data.keySet())){
                if(!item.canSpawn(info)){
                    data.remove(item);
                    continue;
                }
                weight = item.getWeight();
                if(weight < 1){
                    data.remove(item);
                    continue;
                }
                totalWeight += weight;
                data.put(item, weight);
            }
            int chance = CruxMath.random(0, totalWeight);
            for(Map.Entry<NaturalMobSettings, Integer> entry : new HashSet<>(data.entrySet())){
                if(chance <= entry.getValue()){
                    data.remove(entry.getKey());
                    list.add(entry.getKey());
                    break;
                }
                chance -= entry.getValue();
            }
        }
        return list;
    }
}

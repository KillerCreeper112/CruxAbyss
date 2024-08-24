package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.loot.SimpleWeighted;
import killercreepr.crux.nms.biome.BiomeUtils;
import killercreepr.crux.util.CruxMath;
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

public abstract class AbyssNaturalMobContainer extends SimpleWeighted implements Listener {
    private static final Set<AbyssNaturalMobContainer> REGISTRY = new HashSet<>();
    public static @NotNull Set<AbyssNaturalMobContainer> getRegistry(){ return REGISTRY; }

    public static <T extends AbyssNaturalMobContainer> T register(@NotNull T e){
        REGISTRY.add(e);
        return e;
    }

    public static void register(){
        //empty
        register(
                new AbyssNaturalMobContainer(50, 0f) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        return true;
                    }
                }
        );

        register(
                new AbyssNaturalMobContainer(10, 0, AbyssNaturalMobSettings.CRIMSON_EYE) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 12) return false;
                        NamespacedKey k = BiomeUtils.getBiome(info.getBlock());
                        return k.equals(Crux.key("toxic_mire")) && getNearbyChunkEntitySpawns(info.getBlock().getChunk(), 4).size() < 16;
                    }
                }
        );


        register(
                new AbyssNaturalMobContainer(6, 0, AbyssNaturalMobSettings.MOOSE) {
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
                new AbyssNaturalMobContainer(12, 0, AbyssNaturalMobSettings.GROUND_DWELLER) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 25) return false;
                        boolean biome = false;
                        switch (info.getBlock().getBiome()){
                            case BEACH, SNOWY_BEACH, SAVANNA, SAVANNA_PLATEAU, WINDSWEPT_SAVANNA, BADLANDS,
                                    ERODED_BADLANDS, WOODED_BADLANDS, DESERT -> biome = true;
                            case CUSTOM -> {
                                NamespacedKey k = BiomeUtils.getBiome(info.getBlock());
                                biome = k.equals(Crux.key("corruption"));
                            }
                        }
                        return biome && getNearbyChunkEntitySpawns(info.getBlock().getChunk(), 8).size() < 4;
                    }
                }
        );

        register(
                new AbyssNaturalMobContainer(5, 0, AbyssNaturalMobSettings.CHARRED_BONES) {
                    @Override
                    public boolean canSpawn(@NotNull SpawnInfo info) {
                        if(CruxMath.random(1, 100) <= 60) return false;
                        NamespacedKey k = BiomeUtils.getBiome(info.getBlock());
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
            for(AbyssNaturalMobSettings s : spawns){
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

    private final Collection<AbyssNaturalMobSettings> spawns = new HashSet<>();
    public AbyssNaturalMobContainer(int weight, float quality, @NotNull AbyssNaturalMobSettings... spawns) {
        super(weight, quality);
        this.spawns.addAll(List.of(spawns));
    }

    public @NotNull Collection<AbyssNaturalMobSettings> getSpawns() {
        return spawns;
    }

    public static @NotNull List<Entity> spawn(@NotNull Collection<AbyssNaturalMobSettings> poll, @NotNull SpawnInfo info){
        List<Entity> list = new ArrayList<>();
        for(AbyssNaturalMobSettings s : poll){
            int spawned = 0;
            int maxGroup = s.getGroupSize(info);
            int groupRadius = s.getGroupRadius(info);
            for(int i = 0; i < maxGroup; i++){
                Entity e;
                if(spawned < 1){
                    if(s.canSpawn(info)) e = s.spawn(info);
                    else break;
                }else{
                    e = spawnGroup(groupRadius, info, s);
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

    private static Entity spawnGroup(int groupRadius, @NotNull SpawnInfo info, @NotNull AbyssNaturalMobSettings s){
        for(int x = groupRadius; x >= -groupRadius; --x) {
            for(int y = groupRadius; y >= -groupRadius; --y) {
                for(int z = groupRadius; z >= -groupRadius; --z) {
                    Block b = info.getBlock().getRelative(x,y,z);
                    if(b.equals(info.getBlock())) continue;
                    SpawnInfo groupInfo = new SpawnInfo(b, info.getGame());
                    if(s.canSpawn(groupInfo)){
                        return s.spawn(groupInfo);
                    }
                }
            }
        }
        return null;
    }

    public abstract boolean canSpawn(@NotNull SpawnInfo info);

    public static @NotNull List<AbyssNaturalMobContainer> randomContainer(int rolls, @NotNull SpawnInfo info){
        return randomContainer(REGISTRY, rolls, info);
    }

    public static @NotNull List<AbyssNaturalMobContainer> randomContainer(@NotNull Collection<AbyssNaturalMobContainer> poll, int rolls, @NotNull SpawnInfo info){
        List<AbyssNaturalMobContainer> list = new ArrayList<>();
        Map<AbyssNaturalMobContainer, Integer> data = new HashMap<>();
        for(AbyssNaturalMobContainer p : poll){
            data.put(p, p.getWeight());
        }
        for(int i = 0; i < rolls; i++){
            int totalWeight = 0;
            int weight;
            for(AbyssNaturalMobContainer item : new HashSet<>(data.keySet())){
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
            for(Map.Entry<AbyssNaturalMobContainer, Integer> entry : new HashSet<>(data.entrySet())){
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

    public @NotNull List<AbyssNaturalMobSettings> random(int rolls, @NotNull SpawnInfo info){
        return random(spawns, rolls, info);
    }

    public static @NotNull List<AbyssNaturalMobSettings> random(@NotNull Collection<AbyssNaturalMobSettings> poll, int rolls, @NotNull SpawnInfo info){
        List<AbyssNaturalMobSettings> list = new ArrayList<>();
        Map<AbyssNaturalMobSettings, Integer> data = new HashMap<>();
        for(AbyssNaturalMobSettings p : poll){
            data.put(p, p.getWeight());
        }
        for(int i = 0; i < rolls; i++){
            int totalWeight = 0;
            int weight;
            for(AbyssNaturalMobSettings item : new HashSet<>(data.keySet())){
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
            for(Map.Entry<AbyssNaturalMobSettings, Integer> entry : new HashSet<>(data.entrySet())){
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

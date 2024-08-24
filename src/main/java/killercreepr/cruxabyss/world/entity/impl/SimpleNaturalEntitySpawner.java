package killercreepr.cruxabyss.world.entity.impl;

import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.registry.Registry;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxWeightedSupplier;
import killercreepr.cruxabyss.persistence.AbyssPersist;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxabyss.world.entity.NaturalEntitySpawner;
import killercreepr.cruxabyss.world.entity.SpawnContext;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SimpleNaturalEntitySpawner implements NaturalEntitySpawner {
    private static final short GLOBAL_MOB_CAP = 3000;//todo was 300
    protected final @NotNull Plugin plugin;
    protected final @NotNull Random random;
    protected final @NotNull Registry<NaturalEntitySpawnGroup> registry;

    public SimpleNaturalEntitySpawner(@NotNull Plugin plugin, @NotNull Random random, @NotNull Registry<NaturalEntitySpawnGroup> registry) {
        this.plugin = plugin;
        this.random = random;
        this.registry = registry;
    }

    private final int radius = 34;
    private final int innerRadius = 10;

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public int getInnerRadius() {
        return innerRadius;
    }

    @Override
    public int getGlobalMobLimit() {
        return GLOBAL_MOB_CAP;
    }

    @Override
    public boolean isBelowGlobalMobLimit(int amount) {
        return amount < GLOBAL_MOB_CAP;
    }

    public boolean belowGlobalCap(int amount){
        return amount < GLOBAL_MOB_CAP;
    }

    public CompletableFuture<Boolean> belowGlobalCapMainThread(@NotNull World world){
        return CompletableFuture.supplyAsync(() ->{
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            plugin.getServer().getScheduler().runTask(plugin, task ->{
                future.complete(belowGlobalCap(world));
            });
            return future.join();
        });
    }

    public boolean belowGlobalCap(@NotNull World world){
        int x = 0;
        for(Entity e : world.getEntitiesByClass(Mob.class)){
            if("natural".equalsIgnoreCase(AbyssPersist.SPAWN_REASON.get(e, null))){
                x++;
                if(x >= GLOBAL_MOB_CAP) return false;
            }
        }
        return true;
    }

    public int getNaturallySpawnedMobs(@NotNull World world){
        int x = 0;
        for(Entity e : world.getEntitiesByClass(Mob.class)){
            if("natural".equalsIgnoreCase(AbyssPersist.SPAWN_REASON.get(e, null))){
                x++;
            }
        }
        return x;
    }

    @Override
    public boolean canNavigate(@NotNull World world) {
        return belowGlobalCap(world);
    }

    private final Collection<NaturalEntitySpawnGroup> CACHE = new HashSet<>();
    @Override
    public void navigate(@NotNull World world, @NotNull CruxPosition center,
                         @Nullable Predicate<NaturalEntitySpawner> canContinue,
                         @Nullable Consumer<NaturalEntitySpawner> onFinish){
        if(!center.getBlock(world).getChunk().isLoaded()){
            if(onFinish != null) onFinish.accept(this);
            return;
        }
        new BukkitRunnable(){
            private Set<Block> blocks;
            @Override
            public void run() {
                if(blocks == null) blocks = random(center.getBlock(world), radius, innerRadius, CruxMath.random(7500, 10000));
                Block last = null;
                int amount = CruxMath.random(50, 100);
                for(Block b : new HashSet<>(blocks)){
                    blocks.remove(b);
                    SpawnContext ctx = SpawnContext.simple(b, random);
                    Collection<NaturalEntitySpawnGroup> list;
                    if(last != null && b.getLocation().distanceSquared(last.getLocation()) < (48*48)){
                        list = CACHE;
                    }else{
                        list = CruxWeightedSupplier.builder(registry.values())
                            .rolls(CruxMath.random(1, 5))
                            .filter(check -> check.canSpawn(ctx))
                            .build().rollList();
                    }
                    last = b;

                    if(list.isEmpty()){
                        //amount--;
                        continue;
                    }

                    plugin.getServer().getScheduler().runTask(plugin, task ->{
                        for(NaturalEntitySpawnGroup m : list){
                            NaturalEntitySpawnGroup.spawn(
                                m.selectRandom(CruxMath.random(1, 5), ctx), ctx
                            );
                        }
                    });
                    amount--;
                    if(amount < 1) break;
                }
                if(blocks.isEmpty() || (canContinue != null && !canContinue.test(SimpleNaturalEntitySpawner.this))){
                    cancel();
                    if(onFinish != null) onFinish.accept(SimpleNaturalEntitySpawner.this);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    public @NotNull Set<Block> random(@NotNull Block center, int radius, int innerRadius, int rolls){
        Set<Block> list = new CopyOnWriteArraySet<>();
        for(int i = 0; i < rolls; i++){
            int x = CruxMath.random(-radius, radius);
            int y = CruxMath.random(-radius, radius);
            int z = CruxMath.random(-radius, radius);
            while((-innerRadius <= x && x <= innerRadius) &&
                (-innerRadius <= y && y <= innerRadius) &&
                (-innerRadius <= z && z <= innerRadius)){
                x = CruxMath.random(-radius, radius);
                y = CruxMath.random(-radius, radius);
                z = CruxMath.random(-radius, radius);
            }
            Block b = center.getRelative(x,y,z);
            if(b.getChunk().isLoaded()) list.add(b);
        }
        return list;
    }
}

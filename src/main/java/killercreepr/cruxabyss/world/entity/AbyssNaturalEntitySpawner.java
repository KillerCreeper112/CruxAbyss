package killercreepr.cruxabyss.world.entity;

import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxabyss.persistence.AbyssPersist;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class AbyssNaturalEntitySpawner {
    private final GameManager game;
    private static final short GLOBAL_MOB_CAP = 3000;//todo was 300
    //innerRadius+radius
    //radius = 24
    private final int radius = 34;
    private final int innerRadius = 10;

    public int getRadius() {
        return radius;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public AbyssNaturalEntitySpawner(@NotNull GameManager game) {
        this.game = game;
    }

    public boolean belowGlobalCap(int amount){
        return amount < GLOBAL_MOB_CAP;
    }

    public CompletableFuture<Boolean> belowGlobalCapMainThread(){
        return CompletableFuture.supplyAsync(() ->{
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            game.getPlugin().getServer().getScheduler().runTask(game.getPlugin(), task ->{
                future.complete(belowGlobalCap());
            });
            return future.join();
        });
    }

    public boolean belowGlobalCap(){
        int x = 0;
        for(Entity e : game.getWorld().getEntitiesByClass(Mob.class)){
            if("natural".equalsIgnoreCase(AbyssPersist.SPAWN_REASON.get(e, null))){
                x++;
                if(x >= GLOBAL_MOB_CAP) return false;
            }
        }
        return true;
    }

    public int getNaturalSpawnedMobs(){
        int x = 0;
        for(Entity e : game.getWorld().getEntitiesByClass(Mob.class)){
            if("natural".equalsIgnoreCase(AbyssPersist.SPAWN_REASON.get(e, null))){
                x++;
            }
        }
        return x;
    }

    private final Collection<AbyssNaturalMobContainer> CACHE = new HashSet<>();
    public void navigate(@NotNull Player p){
        if(!p.getLocation().getBlock().getChunk().isLoaded()){
            game.naturalSpawnerChecked(p);
            return;
        }
        new BukkitRunnable(){
            private Set<Block> blocks;
            @Override
            public void run() {
                if(blocks == null) blocks = random(p.getLocation().getBlock(), radius, innerRadius, CruxMath.random(7500, 10000));
                Block last = null;
                int amount = CruxMath.random(50, 100);
                for(Block b : new HashSet<>(blocks)){
                    blocks.remove(b);
                    SpawnInfo info = new SpawnInfo(b, game);
                    Collection<AbyssNaturalMobContainer> list;
                    if(last != null && b.getLocation().distanceSquared(last.getLocation()) < (48*48)){
                        list = CACHE;
                    }else list = AbyssNaturalMobContainer.randomContainer(CruxMath.random(1, 5), info);
                    last = b;

                    if(list.isEmpty()){
                        //amount--;
                        continue;
                    }

                    game.getPlugin().getServer().getScheduler().runTask(game.getPlugin(), task ->{
                        for(AbyssNaturalMobContainer m : list){
                            AbyssNaturalMobContainer.spawn(m.random(CruxMath.random(1, 5), info), info);
                        }
                    });
                    amount--;
                    if(amount < 1) break;
                }
                if(blocks.isEmpty() || !p.isOnline() || !game.inGame(p)){
                    cancel();
                    game.naturalSpawnerChecked(p);
                }
            }
        }.runTaskTimerAsynchronously(game.getPlugin(), 0L, 1L);
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

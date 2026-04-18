package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.cruxabyss.api.event.PlayerSurvive1MinuteInAbyssEvent;
import killercreepr.cruxabyss.core.statistic.AbyssStatistic;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstatistics.api.bukkit.BukkitStatisticHolder;
import killercreepr.cruxstatistics.api.statistic.CruxStatisticHolder;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class AbyssWorldDwellerHolder extends PlayerTickedDataHolder {
    public static AbyssWorldDwellerHolder abyssWorldDweller(Player p){
        if(!isAbyss(p.getWorld())) return null;
        return EntityMemory.getOrCreateDataHolder(p, AbyssWorldDwellerHolder.class, mem ->{
            if(!(mem instanceof PlayerMemory d)) return null;
            return new AbyssWorldDwellerHolder(d);
        });
    }

    public static int getAbyssDwellTicks(Player p){
        var holder = BukkitStatisticHolder.statisticHolder(p);
        if(holder == null) return 0;
        return holder.getStatistic(AbyssStatistic.CURRENT_ABYSS_SURVIVE_SECONDS) * 20;
    }

    public static boolean isAbyss(World world){
        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) return false;
        return AbyssWorldTypes.ABYSS.compare(crux.get(CruxWorldsComponents.WORLD_TYPE));
    }

    public static final Key KEY = Crux.key("abyss_world_dweller");
    public AbyssWorldDwellerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        //load();
    }
    public AbyssWorldDwellerHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return super.shouldRemoveFromMemory(e) || e == null || !e.isValid() || !isAbyss(e.getWorld()) ||
        e.getIdleDuration().compareTo(Duration.ofMinutes(1)) >= 0;
    }

    @Override
    protected void removingFromMemory(@Nullable Player e) {
        super.removingFromMemory(e);
        if(cacheHolder != null){
            cacheHolder.setStatistic(AbyssStatistic.CURRENT_ABYSS_SURVIVE_SECONDS, 0);
        }
    }

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        //save();
    }

    protected CruxStatisticHolder cacheHolder;
    protected byte tick = 0;
    @Override
    protected void onTick(@NotNull Player e) {
        super.onTick(e);
        tick++;
        //tick every 1 second
        if(tick % 20 != 0) return;
      if (e.getGameMode() == GameMode.SPECTATOR) {
        return;
      }

        if(cacheHolder == null){
            cacheHolder = BukkitStatisticHolder.statisticHolder(e);
            if(cacheHolder == null) return;
        }

        cacheHolder.incrementStatistic(AbyssStatistic.ABYSS_SURVIVE_SECONDS, 1);
        cacheHolder.incrementStatistic(AbyssStatistic.CURRENT_ABYSS_SURVIVE_SECONDS, 1);

        var tick = cacheHolder.getStatistic(AbyssStatistic.CURRENT_ABYSS_SURVIVE_SECONDS);

        if(tick % 60 == 0){
            PlayerSurvive1MinuteInAbyssEvent event = new PlayerSurvive1MinuteInAbyssEvent(e);
            event.callEvent();
        }
    }
}

package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.core.statistic.AbyssStatistic;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxstatistics.api.bukkit.BukkitStatisticHolder;
import killercreepr.cruxstatistics.api.statistic.CruxStatisticHolder;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RewardsListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssOutpostCapture(AbyssOutpostCaptureEvent event) {
        if(!(event.getEntity() instanceof HumanEntity p)) return;

        ActiveAbyssOutpost outpost = event.getOutpost();
        AbyssOutpostData data = outpost.getData();
        if(data.hasCapturedThisBefore(p.getUniqueId())) return;

        CruxStatisticHolder holder = BukkitStatisticHolder.statisticHolder(p);
        if(holder != null) holder.incrementStatistic(AbyssStatistic.ABYSS_OUTPOSTS_CAPTURED, 1);

        Location spawn = event.getConquestNode().getLocation().toCenterLocation().add(0, 0.55, 0);

        LootTable<ItemStack> loot = CruxRegistries.ITEM_LOOT_TABLE.get(Crux.key("structure/abyss/outpost/capture"));
        if(loot == null) return;
        LootContext ctx = LootContext.builder()
            .looter(p)
            .looted(event.getOutpost())
            .location(spawn)
            .build();

        var list = loot.populateLoot(ctx);
        if(list.isEmpty()) return;
        Crux.scheduler().runTaskMain(() ->{
            list.forEach(item ->{
                if(CruxItem.isEmpty(item)) return;
                Vector dir = generateRandomVector().multiply(
                    CruxMath.random(0.2f, 0.3f)
                );
                spawn.getWorld().dropItem(spawn, item, drop ->{
                    drop.setVelocity(dir);
                });
            });
        });
    }

    public Vector generateRandomVector(){
        return new Vector(
            CruxMath.random(-1f, 1f),
            CruxMath.random(0.5f, .9f),
            CruxMath.random(-1f, 1f)
        );
    }

}

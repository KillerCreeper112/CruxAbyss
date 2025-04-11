package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.data.Holder;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxabyss.api.event.*;
import killercreepr.cruxabyss.core.advancement.objective.*;
import killercreepr.cruxabyss.core.entity.memory.DepthsOfMadnessHolder;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.ObjectiveAdvancement;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.core.data.TrackedAdvancement;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class CustomObjectivesListener implements Listener {
    protected final Holder<CruxAdvancementManager> MANAGER = () -> AdvancementRegistries.ADVANCEMENT_MANAGERS
        .get(USurvivePlugin.inst().key("abyss"));
    protected final Holder<CruxAdvancement> DEPTHS_OF_MADNESS = () -> MANAGER.value().getAdvancement(USurvivePlugin.inst().key("abyss/depths_of_madness"));

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTravelThroughAbyssPortalGateway(EntityTravelThroughAbyssPortalGatewayEvent event) {
        Entity p = event.getEntity();
        AdvancementHolder holder = EntityMemory.getDataHolder(p, AdvancementHolder.class);
        if(holder==null) return;

        holder.getAdvancementTracker().apply(TravelThroughAbyssPortalGatewayObjective.class, (manager,
                                                                                              advancement,
                                                                                              objective) -> {
            objective.trigger(p.getUniqueId(), manager, advancement, event);
        });
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedBlock()) return;
        Player p = event.getPlayer();
        if(DEPTHS_OF_MADNESS.value() == null) return;

        CruxWorld world = CruxCore.inst().worldManager().getWorld(p.getWorld().key());
        if(world == null || !AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))) return;
        if(!DepthsOfMadnessHolder.isInDepths(p)) return;
        if(EntityMemory.getDataHolder(p, DepthsOfMadnessHolder.class) != null || DEPTHS_OF_MADNESS.value().isGranted(p)) return;
        EntityMemory.getOrCreateDataHolder(p, DepthsOfMadnessHolder.class, mem -> new DepthsOfMadnessHolder(mem, e ->{
            //todo fun little thing when complete
            MANAGER.value().grantAdvancement(p, DEPTHS_OF_MADNESS.value());
        }));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityUpgradeAbyssOutpost(EntityUpgradeAbyssOutpostEvent event) {
        if(!(event.getEntity() instanceof Player p)) return;
        AdvancementHolder holder = EntityMemory.getOrCreateDataHolder(p, AdvancementHolder.class);
        if(holder==null) return;

        holder.getAdvancementTracker().apply(AbyssOutpostUpgradeObjective.class, (manager,
                                                                                  advancement,
                                                                                  objective) -> {
            objective.trigger(p.getUniqueId(), manager, advancement, event);
        });
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSurvive1MinuteInAbyss(PlayerSurvive1MinuteInAbyssEvent event) {
        Player p = event.getPlayer();
        AdvancementHolder holder = EntityMemory.getOrCreateDataHolder(p, AdvancementHolder.class);
        if(holder==null) return;

        Map<TrackedAdvancement, Survive1MinuteAbyssObjective> foundObjectives = null;
        for(TrackedAdvancement a : holder.getAdvancementTracker().getAllTracked()) {
            var found = a.getAdvancementOrThrow(ObjectiveAdvancement.class).getObjectives(Survive1MinuteAbyssObjective.class);
            if(found.isEmpty()) continue;
            if(foundObjectives == null) foundObjectives = new HashMap<>();
            for (Survive1MinuteAbyssObjective value : found.values()) {
                foundObjectives.put(a, value);
            }
        }
        if(foundObjectives == null) return;

        Map<TrackedAdvancement, Survive1MinuteAbyssObjective> finalFoundObjectives = foundObjectives;
        Crux.scheduler().runTask(() ->{
            if(!CruxEntityUtil.isValid(p)) return;

            finalFoundObjectives.forEach((key, value) ->{
                CruxAdvancementManager<?> manager = key.getManager();
                ObjectiveAdvancement advancement = key.getObjective();
                value.trigger(p.getUniqueId(), manager, advancement, event);
            });
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAbyssAltarBuild(PlayerAbyssAltarBuildEvent event) {
        Player p = event.getPlayer();
        AdvancementHolder holder = EntityMemory.getOrCreateDataHolder(p, AdvancementHolder.class);
        if(holder==null) return;

        holder.getAdvancementTracker().apply(AbyssAltarBuildObjective.class, (manager,
                                                                              advancement,
                                                                              objective) -> {
            objective.trigger(p.getUniqueId(), manager, advancement, event);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbyssAltarActivatePortal(AbyssAltarActivatePortalEvent event) {
        Player p = event.getPlayer();
        AdvancementHolder holder = EntityMemory.getOrCreateDataHolder(p, AdvancementHolder.class);
        if(holder==null) return;

        holder.getAdvancementTracker().apply(AbyssAltarActivatePortalObjective.class, (manager,
                                                                                       advancement,
                                                                                       objective) -> {
            objective.trigger(p.getUniqueId(), manager, advancement, event);
        });
    }

}

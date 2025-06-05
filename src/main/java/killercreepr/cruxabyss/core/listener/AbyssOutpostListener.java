package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.entity.mob.goal.PlagueTyrantGoal;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssOutpostUpgrades;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveAbyssalRecallUpgrade;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class AbyssOutpostListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return;
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if(b==null) return;
        if(!(b.getBlockData() instanceof RespawnAnchor anchor)) return;
        if(anchor.getCharges() < 1) return;

        ItemStack item = event.getItem();
        if(item != null && item.getType() == Material.GLOWSTONE){
            if(anchor.getCharges() < anchor.getMaximumCharges()){
                return;
            }
        }

        CruxWorld crux = CruxCore.core().worldManager().getWorld(b.getWorld().key());
        if(crux==null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module==null) return;
        //Vector vec = b.getLocation().toVector();
        StoredStructure stored = CruxCollection.getFirst(module.getStored(
            StoredStructure.class, check ->{
                if(!check.has(AbyssComponents.ABYSS_OUTPOST_DATA)) return false;
                AbyssOutpostData data = check.get(AbyssComponents.ABYSS_OUTPOST_DATA);
                if(!(data.getTickedOutpostUpgrade(AbyssOutpostUpgrades.ABYSSAL_RECALL) instanceof ActiveAbyssalRecallUpgrade upgrade)) return false;
                return upgrade.isWithinRadius(b);
                /*BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
                return box.contains(vec);*/
            }
        ));
        if(stored==null) return;
        AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(!(data.getTickedOutpostUpgrade(AbyssOutpostUpgrades.ABYSSAL_RECALL) instanceof ActiveAbyssalRecallUpgrade upgrade)) return;
        if(!data.isMemberOrOwner(p.getUniqueId())) return;

        event.setCancelled(true);
        CruxPosition pos = CruxPosition.block(b);
        if(upgrade.hasRespawnAnchor(pos)){
            upgrade.removeRespawnAnchor(pos);
            Lang.ABYSS_OUTPOST_UPGRADE_RECALL_REMOVED.use(p);
            return;
        }
        if(upgrade.getRespawnAnchors(b.getWorld()).size() >= upgrade.getMaxRespawnAnchors()){
            Lang.ABYSS_OUTPOST_UPGRADE_RECALL_REACHED_MAX.use(p);
            return;
        }
        upgrade.addRespawnAnchor(upgrade.wrapAnchor(pos));
        Lang.ABYSS_OUTPOST_UPGRADE_RECALL_ADDED.use(p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Mob mob)) return;
        var goal = CruxGoalUtil.getGoal(mob, PlagueTyrantGoal.class);
        if(goal == null) return;
        var pos = goal.getHomePosition();
        if(pos == null) return;

        var worldKey = goal.getHomeWorld();
        if(worldKey == null) return;
        var world = CruxCore.core().worldManager().getWorld(worldKey);
        if(world == null) return;

        var module = world.getModule(StructureWorldModule.class);
        if(module == null) return;

        var structure = module.getFirstStoredAt(StoredStructure.class, pos);
        if(structure == null) return;
        var data = structure.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(data == null) return;
        data.setDefeatedPlagueTyrant(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        Entity e = event.getEntity();
        if(!CruxMob.isInCategory(e, MobCategory.ENEMY)) return;

        World world = e.getWorld();

        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux==null) return;
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module==null) return;

        Vector vec = e.getLocation().toVector();

        StoredStructure stored = CruxCollection.getFirst(module.getStored(
            StoredStructure.class, check ->{
                if(!check.has(AbyssComponents.ABYSS_OUTPOST_DATA)) return false;
                BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
                return box.contains(vec);
            }
        ));
    }

}

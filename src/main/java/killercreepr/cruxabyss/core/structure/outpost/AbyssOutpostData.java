package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.outpost.OutpostData;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.StoredOutpostUpgrade;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbyssOutpostData implements StoredStructureComponent, ManagedTicked, OutpostData {
    public UUID owner;
    protected final Map<OutpostUpgrade, Integer> upgrades = new HashMap<>();
    protected final Map<OutpostUpgrade, StoredOutpostUpgrade> storedUpgrades = new ConcurrentHashMap<>();
    @Override
    public void onFileSave(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = ctx.getRegistry();
        if(owner != null){
            o.add("owner", reg.serializeToFile(owner));
        }
        if(!upgrades.isEmpty()){
            FileArray a = new FileArray(upgrades.size());
            upgrades.forEach((upgrade, level) ->{
                FileObject obj = new FileObject();
                obj.add("key", reg.serializeToFile(upgrade.key()));
                obj.addProperty("level", level);
                a.add(obj);
            });
            o.add("upgrades", a);
        }
    }

    @Override
    public void onActiveCreated(@NotNull ActiveStructure structure) {
        structure.set(AbyssComponents.ACTIVE_ABYSS_OUTPOST, new ActiveAbyssOutpost(structure));
    }

    @Override
    public void started() {
        ManagedTicked.super.started();
        initiateUpgrades();
    }

    @Override
    public void stopped() {
        ManagedTicked.super.stopped();
        storedUpgrades.values().forEach(ManagedTicked::stopped);
    }

    public void initiateUpgrades(){
        storedUpgrades.clear();
        upgrades.forEach((upgrade, level) ->{
            StoredOutpostUpgrade stored = upgrade.createStored(this, level);
            if(stored == null) return;
            storedUpgrades.put(upgrade, stored);
        });
        storedUpgrades.values().forEach(ManagedTicked::started);
    }

    protected int tick = 0;
    @Override
    public void tick() {
        if(owner == null) return;
        tick++;
        if(tick < 200) return;
        tick = 0;

        storedUpgrades.values().removeIf(upgrade ->{
            if(upgrade.shouldStop()){
                upgrade.stopped();
                return true;
            }
            upgrade.tick();
            return false;
        });

        Player p = Crux.getServer().getPlayer(owner);
        if(p == null) return;
        Crux.scheduler().runTask(() ->{
            ValuesProvider cfg = CruxAbyss.inst().values();
            cfg.ABYSS_OUTPOST_TAKE_OVER_EFFECTS().valueOr(Set.of()).forEach(pot ->{
                p.addPotionEffect(pot);
            });
        });
    }

    @Override
    public Map<OutpostUpgrade, Integer> getUpgrades() {
        return upgrades;
    }

    @Override
    public boolean hasUpgrade(OutpostUpgrade upgrade) {
        return upgrades.containsKey(upgrade);
    }

    @Override
    public int getUpgradeLevel(OutpostUpgrade upgrade) {
        return upgrades.getOrDefault(upgrade, 0);
    }

    @Override
    public void setUpgradeLevel(OutpostUpgrade upgrade, int level) {
        int previousLevel = getUpgradeLevel(upgrade);
        if(previousLevel == level) return;

        upgrades.put(upgrade, level);

        StoredOutpostUpgrade stored = storedUpgrades.get(upgrade);
        if(stored == null){
            stored = upgrade.createStored(this, level);
            if(stored != null){
                storedUpgrades.put(upgrade, stored);
                stored.started();
            }
            return;
        }
        stored.setLevel(level);
    }

    @Override
    public void removeUpgrade(OutpostUpgrade upgrade) {
        upgrades.remove(upgrade);
        StoredOutpostUpgrade stored = storedUpgrades.remove(upgrade);
        if(stored != null) stored.stopped();
    }
}

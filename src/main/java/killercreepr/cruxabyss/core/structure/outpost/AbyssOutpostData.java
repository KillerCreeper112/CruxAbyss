package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.outpost.OutpostData;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbyssOutpostData implements StoredStructureComponent, ManagedTicked, OutpostData {
    public UUID owner;
    public Long timeCaptured;
    public Collection<UUID> members = new HashSet<>();
    protected final Map<OutpostUpgrade, Integer> upgrades = new HashMap<>();
    protected final Map<OutpostUpgrade, TickedOutpostUpgrade> storedUpgrades = new ConcurrentHashMap<>();
    protected static final int tickRate = 1;
    protected final StoredStructure stored;

    public AbyssOutpostData(StoredStructure stored) {
        this.stored = stored;
    }

    @Override
    public void onFileSave(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = ctx.getRegistry();
        if(owner != null){
            o.add("owner", reg.serializeToFile(owner));
        }
        if(!members.isEmpty()){
            o.add("members", reg.serializeToFile(members));
        }
        if(timeCaptured != null){
            o.addProperty("time_captured", timeCaptured);
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
        storedUpgrades.values().forEach(t -> t.stopped(tick, tickRate));
    }

    public void initiateUpgrades(){
        storedUpgrades.clear();
        upgrades.forEach((upgrade, level) ->{
            TickedOutpostUpgrade stored = upgrade.createStored(this, level);
            if(stored == null) return;
            storedUpgrades.put(upgrade, stored);
        });
        storedUpgrades.values().forEach(t -> t.started(tick, tickRate));
    }

    protected int tick = 0;
    @Override
    public void tick() {
        if(owner == null) return;
        tick++;
        if(tick < 200) return;
        tick = 0;

        storedUpgrades.values().removeIf(upgrade ->{
            if(upgrade.shouldStop(tick, tickRate)){
                upgrade.stopped(tick, tickRate);
                return true;
            }
            upgrade.tick(tick, tickRate);
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

        ActiveStructure active = CruxCore.core().worldManager().getWorld(stored.getChunk().worldUUID())
                .getModule(StructureWorldModule.class)
                    .getActiveStructures().get(stored.getChunk().getChunkKey(), stored.getPosition());
        if(active != null && active.has(AbyssComponents.ACTIVE_ABYSS_OUTPOST)){
            ActiveAbyssOutpost abyss = active.get(AbyssComponents.ACTIVE_ABYSS_OUTPOST);
            TickedOutpostUpgrade stored = abyss.activeUpgrades.get(upgrade);
            if(stored == null){
                stored = upgrade.createActive(abyss, level);
                if(stored != null){
                    abyss.activeUpgrades.put(upgrade, stored);
                    stored.started(tick, tickRate);
                }
            }else stored.setLevel(level);
        }

        upgrades.put(upgrade, level);

        TickedOutpostUpgrade stored = storedUpgrades.get(upgrade);
        if(stored == null){
            stored = upgrade.createStored(this, level);
            if(stored != null){
                storedUpgrades.put(upgrade, stored);
                stored.started(tick, tickRate);
            }
            return;
        }
        stored.setLevel(level);
    }

    @Override
    public void removeUpgrade(OutpostUpgrade upgrade) {
        upgrades.remove(upgrade);
        TickedOutpostUpgrade stored = storedUpgrades.remove(upgrade);
        if(stored != null) stored.stopped(tick, tickRate);
    }
}

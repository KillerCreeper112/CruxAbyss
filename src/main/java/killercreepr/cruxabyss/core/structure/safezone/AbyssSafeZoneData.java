package killercreepr.cruxabyss.core.structure.safezone;

import killercreepr.crux.api.data.tick.ManagedTicked;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.structure.outpost.OutpostData;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.api.structure.safezone.SafeZoneData;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AbyssSafeZoneData implements StoredStructureComponent, ManagedTicked, SafeZoneData {
    public UUID owner;
    public Long timeCaptured;
    protected final Map<OutpostUpgrade, Integer> upgrades = new HashMap<>();
    protected final Map<OutpostUpgrade, TickedOutpostUpgrade> storedUpgrades = new ConcurrentHashMap<>();
    protected static final int tickRate = 1;
    protected final StoredStructure stored;

    public AbyssSafeZoneData(StoredStructure stored) {
        this.stored = stored;
    }

    @Override
    public void onFileSave(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = ctx.getRegistry();
        if(owner != null){
            o.add("owner", reg.serializeToFile(owner));
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
    }

    @Override
    public void stopped() {
        ManagedTicked.super.stopped();
        storedUpgrades.values().forEach(t -> t.stopped(tick, tickRate));
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
}

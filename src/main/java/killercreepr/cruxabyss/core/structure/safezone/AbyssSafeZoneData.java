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
    protected static final int tickRate = 1;
    protected final StoredStructure stored;

    public AbyssSafeZoneData(StoredStructure stored) {
        this.stored = stored;
    }

    @Override
    public void onFileSave(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = ctx.getRegistry();
    }

    @Override
    public void onActiveCreated(@NotNull ActiveStructure structure) {
        //structure.set(AbyssComponents.ACTIVE_ABYSS_OUTPOST, new ActiveAbyssOutpost(structure));
    }

    @Override
    public void started() {
        ManagedTicked.super.started();
    }

    @Override
    public void stopped() {
        ManagedTicked.super.stopped();
        //storedUpgrades.values().forEach(t -> t.stopped(tick, tickRate));
    }

    protected int tick = 0;
    @Override
    public void tick() {
    }
}

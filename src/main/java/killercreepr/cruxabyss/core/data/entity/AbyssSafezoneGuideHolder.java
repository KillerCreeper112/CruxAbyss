package killercreepr.cruxabyss.core.data.entity;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.crux.core.util.GetNear;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.data.ParticleGuide;
import killercreepr.cruxabyss.core.structure.safezone.StoredAbyssSafeZone;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.util.GetStructureNear;
import killercreepr.cruxworlds.api.world.CruxWorld;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AbyssSafezoneGuideHolder extends PlayerTickedDataHolder {
    public static final Key KEY = Crux.key("abyss_safezone_guide");
    public AbyssSafezoneGuideHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }

    public AbyssSafezoneGuideHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return super.shouldRemoveFromMemory(e) || target == null;
    }

    protected int tick = 0;
    @Override
    protected void onTick(@NotNull Player e) {
        super.onTick(e);
        if(target == null) return;
        tick++;
        ValuesProvider cfg = CruxAbyss.inst().values();
        int every = cfg.ABYSS_SAFEZONE_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        if(tick % every != 0) return;
        tick = 0;
        guide(e);
    }

    public void guide(Player p){
        ValuesProvider cfg = CruxAbyss.inst().values();
        new ParticleGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), target, cfg.ABYSS_SAFEZONE_GUIDE_PARTICLE_AMOUNT().value().intValue(), cfg).setStarted();
    }

    protected UUID world;
    protected StoredStructure targetStructure;
    protected Location target;

    public void onWorldChanged(Player p, World from){
        if(p.getWorld().getUID().equals(world)) return;
        target = null;
        targetStructure = null;
    }

    public StoredStructure findSafezone(Player p){
        CruxWorld crux = CruxCore.core().worldManager().getWorld(p.getWorld().key());
        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        GetNear<StoredStructure> near = new GetStructureNear(module.getStoredStructures())
            .center(p.getLocation())
            .filter(stored -> stored instanceof StoredAbyssSafeZone)
            .operation(GetNear.Operation.NEAREST)
            ;
        return near.findFirst();
    }

    public void update(World world, Player p){
        this.world = world.getUID();
        targetStructure = findSafezone(p);
        if(targetStructure == null) return;
        target = targetStructure.getPosition().toLocation(world);
    }

    public UUID getWorld() {
        return world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public StoredStructure getTargetStructure() {
        return targetStructure;
    }

    public void setTargetStructure(StoredStructure targetStructure) {
        this.targetStructure = targetStructure;
    }

    public Location getTarget() {
        return target;
    }

    public void setTarget(Location target) {
        this.target = target;
    }
}

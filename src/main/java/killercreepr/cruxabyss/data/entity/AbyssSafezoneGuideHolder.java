package killercreepr.cruxabyss.data.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.crux.data.entity.PlayerTickedDataHolder;
import killercreepr.crux.util.GetNear;
import killercreepr.cruxabyss.data.ParticleGuide;
import killercreepr.cruxabyss.structure.StoredAbyssSafezone;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.manager.StructureManager;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import killercreepr.cruxstructures.util.GetStructureNear;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.values.ValuesProvider;
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
        ValuesProvider cfg = USurvivePlugin.inst().values();
        int every = cfg.DEATH_GUIDE_TICK_EVERY().value().intValue();
        if(every==0) return;
        guide(e);
    }

    public void guide(Player p){
        //todo eventually do not use death guide values lol
        ValuesProvider cfg = USurvivePlugin.inst().values();
        new ParticleGuide(p, p.getLocation().add(0, p.getHeight()/2, 0), target, cfg.DEATH_GUIDE_PARTICLE_AMOUNT().value().intValue(), cfg).setStarted();
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
        StructureManager structureManager = CruxCore.inst().structureManager();
        GetNear<StoredStructure> near = new GetStructureNear(structureManager.getStored())
            .center(p.getLocation())
            .filter(stored -> stored instanceof StoredAbyssSafezone)
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

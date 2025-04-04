package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.api.event.PlayerSurvive1MinuteInAbyssEvent;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssWorldDwellerHolder extends PlayerTickedDataHolder implements Loadable {
    public static AbyssWorldDwellerHolder abyssWorldDweller(Player p){
        if(!isAbyss(p.getWorld())) return null;
        return EntityMemory.getOrCreateDataHolder(p, AbyssWorldDwellerHolder.class, mem ->{
            if(!(mem instanceof PlayerMemory d)) return null;
            return new AbyssWorldDwellerHolder(d);
        });
    }

    public static int getAbyssDwellTicks(Player p){
        AbyssWorldDwellerHolder holder = abyssWorldDweller(p);
        if(holder == null) return 0;
        return holder.getTick();
    }

    public static boolean isAbyss(World world){
        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) return false;
        return AbyssWorldTypes.ABYSS.compare(crux.get(CruxWorldsComponents.WORLD_TYPE));
    }

    public static final Key KEY = Crux.key("abyss_world_dweller");
    public AbyssWorldDwellerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        load();
    }
    public AbyssWorldDwellerHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return super.shouldRemoveFromMemory(e) || e == null || !e.isValid() || !isAbyss(e.getWorld());
    }

    public int getTick() {
        return tick;
    }

    @Override
    public void onMemoryUnload(@NotNull Entity e) {
        super.onMemoryUnload(e);
        save();
    }

    protected int tick = 0;

    @Override
    protected void onTick(@NotNull Player e) {
        super.onTick(e);
        tick++;

        if(tick % 1200 == 0){
            PlayerSurvive1MinuteInAbyssEvent event = new PlayerSurvive1MinuteInAbyssEvent(e);
            event.callEvent();
        }
    }

    @Override
    public void save() {
        CruxTag.set(parent.value(), "abyss_world_dwell_ticks", PersistentDataType.INTEGER, tick);
    }

    @Override
    public void load() {
        tick = CruxTag.get(parent.value(), "abyss_world_dwell_ticks", PersistentDataType.INTEGER, tick);
    }
}

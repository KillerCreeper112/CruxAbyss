package killercreepr.cruxabyss.core.world.module;

import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class WorldEventsModule extends SimpleWorldModule implements Ticked {
    protected final @NotNull Collection<WorldEvent> events = new HashSet<>();
    public WorldEventsModule(@NotNull CruxWorld parent) {
        super(parent);
    }

    public boolean hasWorldEventOfType(Class<? extends WorldEvent> clazz){
        return events.stream().anyMatch(event -> clazz.isAssignableFrom(event.getClass()));
    }

    public void addWorldEvent(@NotNull WorldEvent event){
        events.add(event);
    }

    public void removeWorldEvent(@NotNull WorldEvent event){
        events.remove(event);
    }

    @Override
    public void tick() {
        events.removeIf(event ->{
            if(event.shouldStop()) return true;
            event.tick();
            return false;
        });
    }
}

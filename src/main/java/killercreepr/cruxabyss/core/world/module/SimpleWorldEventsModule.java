package killercreepr.cruxabyss.core.world.module;

import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.world.module.SimpleWorldModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class SimpleWorldEventsModule extends SimpleWorldModule implements WorldEventsModule {
    protected final @NotNull Collection<WorldEvent> events = new HashSet<>();
    public SimpleWorldEventsModule(@NotNull CruxWorld parent) {
        super(parent);
    }

    public boolean hasWorldEventOfType(Class<? extends WorldEvent> clazz){
        return events.stream().anyMatch(event -> clazz.isAssignableFrom(event.getClass()));
    }

    public void addWorldEvent(@NotNull WorldEvent event){
        events.add(event);
        event.started();
    }

    public void removeWorldEvent(@NotNull WorldEvent event){
        events.remove(event);
    }

    @Override
    public Collection<WorldEvent> getWorldEvents() {
        return events;
    }

    @Override
    public void tick() {
        events.removeIf(event ->{
            if(event.shouldStop()){
                event.stopped();
                return true;
            }
            event.tick();
            return false;
        });
    }
}

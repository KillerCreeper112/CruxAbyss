package killercreepr.cruxabyss.api.world.module;

import killercreepr.crux.api.data.tick.Ticked;
import killercreepr.cruxabyss.api.world.event.WorldEvent;
import killercreepr.cruxworlds.api.world.module.WorldModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface WorldEventsModule extends WorldModule, Ticked {
    boolean hasWorldEventOfType(Class<? extends WorldEvent> clazz);
    void addWorldEvent(@NotNull WorldEvent event);
    void removeWorldEvent(@NotNull WorldEvent event);
    Collection<WorldEvent> getWorldEvents();

    default <T extends WorldEvent> Collection<T> getApplicableWorldEvents(Class<T> clazz, Predicate<T> filter){
        Collection<T> list = new HashSet<>();
        forEach(clazz, event -> {
            if (filter.test(event)) {
                list.add(event);
            }
        });
        return list;
    }

    default <T extends WorldEvent> Collection<T> getWorldEventsOfType(Class<T> clazz){
        Collection<T> list = new HashSet<>();
        forEach(clazz, list::add);
        return list;
    }

    default <T extends WorldEvent> void forEach(Class<T> type, Consumer<T> consumer) {
        getWorldEvents().forEach(event -> {
            if (type.isAssignableFrom(event.getClass())) {
                consumer.accept(type.cast(event));
            }
        });
    }
}

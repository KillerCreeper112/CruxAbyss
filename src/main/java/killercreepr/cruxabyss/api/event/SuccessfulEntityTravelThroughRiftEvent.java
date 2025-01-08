package killercreepr.cruxabyss.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class SuccessfulEntityTravelThroughRiftEvent extends EntityEvent{
    private static final HandlerList HANDLER_LIST = new HandlerList();
    //protected final @NotNull RandomWorldTP to;
    protected final @NotNull Entity returnPortal;
    public SuccessfulEntityTravelThroughRiftEvent(@NotNull Entity what, @NotNull Entity returnPortal) {
        super(what);
        //this.to = to;
        this.returnPortal = returnPortal;
    }

    @NotNull
    public Entity getReturnPortal() {
        return returnPortal;
    }

    /*@NotNull
    public RandomWorldTP getTo() {
        return to;
    }*/

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}

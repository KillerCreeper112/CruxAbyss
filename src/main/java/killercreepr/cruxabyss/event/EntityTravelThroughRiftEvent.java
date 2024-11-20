package killercreepr.cruxabyss.event;

import killercreepr.cruxabyss.entity.goal.AbyssAltarPortalGoal;
import killercreepr.cruxteleport.api.teleport.world.RandomWorldTP;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityTravelThroughRiftEvent extends EntityEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final @NotNull AbyssAltarPortalGoal rift;
    protected @NotNull RandomWorldTP to;
    protected boolean removePortal = true;
    protected boolean cancelTeleport = false;
    public EntityTravelThroughRiftEvent(@NotNull Entity what, @NotNull AbyssAltarPortalGoal rift, @NotNull RandomWorldTP to) {
        super(what);
        this.rift = rift;
        this.to = to;
    }

    @NotNull
    public AbyssAltarPortalGoal getRift() {
        return rift;
    }

    public boolean isCancelTeleport() {
        return cancelTeleport;
    }

    public void setCancelTeleport(boolean cancelTeleport) {
        this.cancelTeleport = cancelTeleport;
    }

    public boolean isRemovePortal() {
        return removePortal;
    }

    public void setRemovePortal(boolean removePortal) {
        this.removePortal = removePortal;
    }

    @NotNull
    public RandomWorldTP getTo() {
        return to;
    }

    public void setTo(@NotNull RandomWorldTP to) {
        this.to = to;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelTeleport && !removePortal;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        setCancelTeleport(true);
        setRemovePortal(false);
    }
}

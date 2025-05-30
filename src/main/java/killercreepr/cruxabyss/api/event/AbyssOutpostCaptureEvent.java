package killercreepr.cruxabyss.api.event;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AbyssOutpostCaptureEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected boolean cancel = false;
    protected final ActiveAbyssOutpost outpost;
    protected final Entity entity;
    protected final Block conquestNode;

    public ActiveAbyssOutpost getOutpost() {
        return outpost;
    }

    public Entity getEntity() {
        return entity;
    }

    public AbyssOutpostCaptureEvent(ActiveAbyssOutpost outpost, Entity entity, Block conquestNode) {
        super(!Crux.isPrimaryThread());
        this.outpost = outpost;
        this.entity = entity;
        this.conquestNode = conquestNode;
    }

    public Block getConquestNode() {
        return conquestNode;
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
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}

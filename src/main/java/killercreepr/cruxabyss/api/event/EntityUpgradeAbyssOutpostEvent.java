package killercreepr.cruxabyss.api.event;

import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class EntityUpgradeAbyssOutpostEvent extends EntityEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final OutpostUpgrade upgrade;
    protected final int oldlevel;
    protected int newLevel;
    protected final AbyssOutpostData outpost;
    protected boolean cancel = false;
    public EntityUpgradeAbyssOutpostEvent(@NotNull Entity who, OutpostUpgrade upgrade, int oldlevel, int newLevel, AbyssOutpostData outpost) {
        super(who);
        this.upgrade = upgrade;
        this.oldlevel = oldlevel;
        this.newLevel = newLevel;
        this.outpost = outpost;
    }

    public OutpostUpgrade getUpgrade() {
        return upgrade;
    }

    public int getOldlevel() {
        return oldlevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public AbyssOutpostData getOutpost() {
        return outpost;
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

package killercreepr.cruxabyss.api.event;

import killercreepr.cruxabyss.api.altar.AbyssAltar;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarActivatePortalEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected boolean cancel = false;

    //@NotNull AbyssAltar altar, @NotNull Player user, @NotNull CruxItem cruxItem
    protected final Player player;
    protected final AbyssAltar altar;
    protected final ItemStack item;

    public AbyssAltarActivatePortalEvent(Player player, AbyssAltar altar, ItemStack item) {
        this.player = player;
        this.altar = altar;
        this.item = item;
    }

    public Player getPlayer() {
        return player;
    }

    public AbyssAltar getAltar() {
        return altar;
    }

    public ItemStack getItem() {
        return item;
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

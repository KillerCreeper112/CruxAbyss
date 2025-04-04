package killercreepr.cruxabyss.api.event;

import killercreepr.crux.core.Crux;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerSurvive1MinuteInAbyssEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final Player player;

    public Player getPlayer() {
        return player;
    }

    public PlayerSurvive1MinuteInAbyssEvent(Player player) {
        super(!Crux.isPrimaryThread());
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}

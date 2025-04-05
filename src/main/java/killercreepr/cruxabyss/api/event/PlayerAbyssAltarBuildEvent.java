package killercreepr.cruxabyss.api.event;

import killercreepr.cruxabyss.api.altar.AbyssAltar;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAbyssAltarBuildEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    protected final Block block;
    protected final AbyssAltar altar;

    public PlayerAbyssAltarBuildEvent(Player player, Block block, AbyssAltar altar) {
        super(player);
        this.player = player;
        this.block = block;
        this.altar = altar;
    }

    public Block getBlock() {
        return block;
    }

    public AbyssAltar getAltar() {
        return altar;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

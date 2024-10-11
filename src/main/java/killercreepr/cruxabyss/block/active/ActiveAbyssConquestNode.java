package killercreepr.cruxabyss.block.active;

import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlockImpl;
import killercreepr.cruxblocks.block.active.ActiveCruxInteractable;
import killercreepr.cruxblocks.block.active.ActiveCruxTickedBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ActiveAbyssConquestNode extends ActiveCruxBlockImpl implements ActiveCruxTickedBlock, ActiveCruxInteractable {
    public ActiveAbyssConquestNode(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    @Override
    public void tick() {
        Bukkit.broadcastMessage("conquest ticked");
    }

    @NotNull
    @Override
    public Event.Result interact(@NotNull PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return Event.Result.DENY;
        Player p = event.getPlayer();
        return Event.Result.DEFAULT;
    }
}

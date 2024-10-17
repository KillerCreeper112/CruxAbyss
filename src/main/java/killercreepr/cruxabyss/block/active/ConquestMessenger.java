package killercreepr.cruxabyss.block.active;

import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.crux.tags.container.TagContainer;
import killercreepr.cruxabyss.lang.Lang;
import killercreepr.cruxabyss.structure.ActiveAbyssOutpost;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ConquestMessenger {
    protected final ActiveAbyssConquestNode node;
    protected final ActiveAbyssOutpost outpost;

    public ConquestMessenger(ActiveAbyssConquestNode node, ActiveAbyssOutpost outpost) {
        this.node = node;
        this.outpost = outpost;
    }

    public Collection<Player> getNearby(){
        return node.getBlock().getWorld().getNearbyEntitiesByType(Player.class, node.getBlock().getLocation().toCenterLocation(), 500D);//todo
    }

    public ActiveAbyssConquestNode getNode() {
        return node;
    }

    public ActiveAbyssOutpost getOutpost() {
        return outpost;
    }

    protected float lastProgress;
    protected boolean msged = false;
    private void takingOrDeactivatingTick(boolean takingOver, float progress){
        if(progress < lastProgress){
            lastProgress = progress;
            msged = false;
            return;
        }
        lastProgress = progress;
        if(msged) return;
        if(progress < .2) return;
        broadcastMsg(takingOver);
    }

    public void broadcastMsg(boolean takingOver){
        Player p = node.getUser() == null ? null : node.getUser().get();
        if(p == null) return;
        msged = true;
        MergedTagContainer tags = TagContainer.merged()
            .hook(p)
            .hook(node.getBlock())
            ;
        getNearby().forEach(near ->{
            if(takingOver){
                Lang.ABYSS_CONQUEST_NODE_PLAYER_CAPTURING.use(near, tags);
                return;
            }
            Lang.ABYSS_CONQUEST_NODE_PLAYER_DEACTIVATING.use(near, tags);
        });
    }

    public void deactivatingTick(float progress){
        takingOrDeactivatingTick(false, progress);
    }

    public void takingOverTick(float progress){
        takingOrDeactivatingTick(true, progress);
    }
}

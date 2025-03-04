package killercreepr.cruxabyss.core.structure.outpost.upgrade;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveAbyssalRecallUpgrade;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;

public class AbyssRecallAnchor {
    public static AbyssRecallAnchor abyssRecallAnchor(CruxPosition pos, AbyssOutpostData data){
        return new AbyssRecallAnchor(pos, data);
    }

    protected final CruxPosition position;
    protected final AbyssOutpostData data;

    public boolean attemptTeleport(Entity e){
        if(!(data.getTickedOutpostUpgrade(AbyssOutpostUpgrades.ABYSSAL_RECALL) instanceof ActiveAbyssalRecallUpgrade upgrade)) return false;
        Block block = block();
        if(block == null) return false;
        return upgrade.attemptTeleportToRespawnAnchor(e, block);
    }

    public AbyssRecallAnchor(CruxPosition position, AbyssOutpostData data) {
        this.position = position;
        this.data = data;
    }

    public World getWorld(){
        return data.getStored().getChunk().toBukkitWorld();
    }

    public Block getBlock(){
        World world = getWorld();
        if(world == null) return null;
        return position.getBlock(world);
    }

    protected Block blockCache;
    protected long lastCache;

    public Block block(){
        if(blockCache == null || !CruxMath.hasOccurredWithin(lastCache, 600) || getWorld() == null){
            blockCache = getBlock();
            lastCache = System.currentTimeMillis();
        }
        return blockCache;
    }

    public boolean canRespawnAt(){
        return getCharges() > 0;
    }

    public boolean isDestroyed(){
        Block b = block();
        if(b == null) return true;
        return !(b.getBlockData() instanceof RespawnAnchor);
    }

    public int getCharges(){
        Block b = block();
        if(b == null) return 0;
        if(!(b.getBlockData() instanceof RespawnAnchor anchor)) return 0;
        return anchor.getCharges();
    }

    public CruxPosition getPosition() {
        return position;
    }
}

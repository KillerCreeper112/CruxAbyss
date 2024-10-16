package killercreepr.cruxabyss.block.active;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.structure.ActiveAbyssOutpost;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlockImpl;
import killercreepr.cruxblocks.block.active.ActiveCruxInteractable;
import killercreepr.cruxblocks.block.active.ActiveCruxTickedBlock;
import killercreepr.cruxblocks.block.context.PlaceBlockContext;
import killercreepr.cruxcore.CruxCore;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ActiveAbyssConquestNode extends ActiveCruxBlockImpl implements ActiveCruxTickedBlock, ActiveCruxInteractable {
    public ActiveAbyssConquestNode(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    protected Reference<Player> user;
    protected long lastInteract;
    protected int progress = 0;
    protected final int maxProgress = 100;

    public CruxBlock getUnpoweredBlock(){
        return cruxBlock.getGroup().getBlock(
            cruxBlock.getGroup().key()
        );
    }

    public CruxBlock getPoweredBlock(){
        return cruxBlock.getGroup().getBlock(
            Key.key(
                cruxBlock.getGroup().key().namespace(), cruxBlock.getGroup().key().value() + "_powered"
            )
        );
    }

    @Override
    public void update() {
        super.update();
        if(!isValid()) return;
        if(outpost() == null) return;
        boolean powered = outpost().getData().owner != null;
        CruxBlock state = powered ? getPoweredBlock() : getUnpoweredBlock();
        if(state.getTextureData().compareTexture(block)) return;
        state.placeBlock(PlaceBlockContext.context(block, null, BlockFace.DOWN));
    }

    @Override
    public void tick() {
        visualTick();
        if(user == null) return;
        if(outpost() == null){
            reset();
            return;
        }
        if(!CruxMath.hasOccurredWithin(lastInteract, 11)){
            reset();
            return;
        }
        Player p = user.get();
        if(p == null){
            reset();
            return;
        }
        if(p.getUniqueId().equals(outpost().getData().owner) && !p.isSneaking()){
            return;
        }
        progress += 1;
        if(progress >= maxProgress){
            reset();
            if(p.getUniqueId().equals(outpost().getData().owner)){
                outpost().resetOwner();
            }else{
                outpost().capture(p);
            }
            p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
            Crux.getServer().getScheduler().runTask(Crux.getMainPlugin(), task ->{
                update();
            });
            return;
        }
        p.sendActionBar(Component.text(progress + ""));
    }

    protected int visualTick = 0;
    public void visualTick(){
        if(outpost() == null) return;
        visualTick++;
        if(outpost().getData().owner == null){
            return;
        }
        if(visualTick % 10 != 0) return;
        visualTick = 0;
        new ParticleBuilder(Particle.WITCH)
            .offset(.7, .7, .7)
            .extra(.1)
            .count(CruxMath.random(10, 20))
            .location(block.getLocation().toCenterLocation())
            .spawn()
        ;
    }

    public void reset(){
        user = null;
        lastInteract = 0L;
        progress = 0;
    }

    public boolean hasValidUser(){
        if(user == null) return false;
        return user.get() != null;
    }
    private ActiveAbyssOutpost outpost;
    protected long lastCheckedOutpost;
    public ActiveAbyssOutpost outpost(){
        if(outpost == null){
            if(CruxMath.hasOccurredWithin(lastCheckedOutpost, 20)) return outpost;
            outpost = CruxCore.inst().structureManager().getFirstActiveAt(ActiveAbyssOutpost.class, block);
            lastCheckedOutpost = System.currentTimeMillis();
        }
        return outpost;
    }
    @NotNull
    @Override
    public Event.Result interact(@NotNull PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return Event.Result.DENY;
        if(outpost() == null) return Event.Result.DENY;
        Player p = event.getPlayer();
        if(hasValidUser()){
            Player user = this.user.get();
            if(user != null){
                if(!p.equals(user) && !user.getUniqueId().equals(outpost().getData().owner)){
                    p.sendMessage("nono");
                    return Event.Result.DENY;
                }
            }
        }
        if(user == null){
            reset();

            outpost();
            if(outpost == null){
                p.sendMessage("Block must be placed in an abyss outpost.");
                return Event.Result.DENY;
            }

            user = new WeakReference<>(p);
        }
        lastInteract = System.currentTimeMillis();
        return Event.Result.DEFAULT;
    }
}

package killercreepr.cruxabyss.core.block.active;

import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.util.CruxBlockUtil;
import killercreepr.cruxabyss.core.component.impl.MirePadComponent;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxEntityMove;
import killercreepr.cruxblocks.api.block.active.ActiveCruxRedstonePowerable;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ActiveMirePad extends SimpleActiveCruxBlock implements ActiveCruxRedstonePowerable, ActiveCruxEntityMove {
    protected final MirePadComponent data;
    public ActiveMirePad(@NotNull Block block, @NotNull CruxBlock cruxBlock, MirePadComponent data) {
        super(block, cruxBlock);
        this.data = data;
        update();
    }

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
    public void redstonePowerChanged(Block from, int newPower) {
        super.redstonePowerChanged(from, newPower);
        update();
    }

    protected boolean powered = false;
    @Override
    public void update() {
        super.update();
        boolean powered = CruxBlockUtil.findPowerSource(block) != null;
        this.powered = powered;
        CruxBlock state = powered ? getPoweredBlock() : getUnpoweredBlock();
        if(state.getTextureData().compareTexture(block)) return;
        state.setBlock(BlockContext.context(block, null), true, false);
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public void onEntityMove(@NotNull Entity e) {
        if(!powered) return;

        if(data.launchSound != null) data.launchSound.playAt(e);
        if(data.launchForce != null){
            var ctx = InputContext.inputContext(TagContainer.string().hook(e));

            Vector look;
            if(data.useEntityRotation){
                look = e.getLocation().getDirection();
                if(data.useEntityPitch)
            }

            Vector dir = new Vector(
                data.launchForce.x().sample(ctx).doubleValue(),
                data.launchForce.y().sample(ctx).doubleValue(),
                data.launchForce.z().sample(ctx).doubleValue()
            );
            e.setVelocity(dir);
        }
        if(data.launchPotions != null){
            if(e instanceof LivingEntity living){
                living.addPotionEffects(data.launchPotions);
            }
        }
    }
}

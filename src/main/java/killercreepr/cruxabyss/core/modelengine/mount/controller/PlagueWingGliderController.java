package killercreepr.cruxabyss.core.modelengine.mount.controller;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.impl.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import killercreepr.cruxabyss.core.entity.memory.PlagueWingGliderHolder;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class PlagueWingGliderController extends AbstractMountController {
    protected final PlagueWingGliderHolder holder;
    protected final float moveSpeed;
    public PlagueWingGliderController(Entity entity, Mount mount, PlagueWingGliderHolder holder, float moveSpeed) {
        super(entity, mount);
        this.holder = holder;
        this.moveSpeed = moveSpeed;
    }

    public void onDismount(){
        holder.onDismount();
    }

    public void onLand(){
        holder.onLand();
    }

    public void updateDriverMovement(MoveController controller, ActiveModel model) {
        Optional maybeMount = model.getMountManager();
        if(maybeMount.isEmpty()) return;
        BehaviorManager<?> manager = (BehaviorManager)maybeMount.get();
        MountManager mount = (MountManager) manager;

        controller.nullifyFallDistance();

        if(controller.isOnGround()){
            onLand();
            return;
        }

        //Vector original = controller.getVelocity();
        //controller.setVelocity(original.getX(), 0D, original.getZ());
        if (this.input.isSneak()) {
            mount.dismountDriver();
            controller.move(0.0F, 0.0F, 0.0F, 0.0F);
            onDismount();
            return;
        }

        controller.move(this.input.getSide(), 0.0F, this.input.getFront(), moveSpeed);
    }

    public void updatePassengerMovement(MoveController controller, ActiveModel model) {
        Optional maybeMount = model.getMountManager();
        if(maybeMount.isEmpty()) return;
        BehaviorManager<?> manager = (BehaviorManager)maybeMount.get();
        MountManager mount = (MountManager) manager;
        if (this.input.isSneak()) {
            mount.dismountRider(this.entity);
        }
    }
}

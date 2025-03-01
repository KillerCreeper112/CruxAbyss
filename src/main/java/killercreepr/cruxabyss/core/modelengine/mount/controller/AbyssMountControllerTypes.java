package killercreepr.cruxabyss.core.modelengine.mount.controller;

import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import killercreepr.cruxabyss.core.entity.memory.PlagueWingGliderHolder;

public class AbyssMountControllerTypes {
    //public static final MountControllerSupplier PLAGUE_WING_GLIDER = (entity, mount) -> new PlagueWingGliderController(entity, mount);
    public static MountControllerSupplier plagueWingGlider(PlagueWingGliderHolder holder, float moveSpeed){
        return ((entity, mount) -> new PlagueWingGliderController(entity, mount, holder, moveSpeed));
    }
}

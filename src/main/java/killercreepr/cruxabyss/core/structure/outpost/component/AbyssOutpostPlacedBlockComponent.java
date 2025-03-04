package killercreepr.cruxabyss.core.structure.outpost.component;

import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxstructures.api.component.StructureBlockPlaceInsideComponent;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import org.bukkit.event.block.BlockPlaceEvent;

public class AbyssOutpostPlacedBlockComponent implements StructureBlockPlaceInsideComponent {
    @Override
    public void onBlockPlace(StoredStructure stored, BlockPlaceEvent event) {
        AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
        if(data==null) return;
        data.onBlockPlace(event);
    }
}

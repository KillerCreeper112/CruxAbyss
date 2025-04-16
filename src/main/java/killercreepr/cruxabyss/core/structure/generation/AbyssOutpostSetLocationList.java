package killercreepr.cruxabyss.core.structure.generation;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.generation.StructureGenerator;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.generation.InstantLocationSetListStructureGen;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class AbyssOutpostSetLocationList extends InstantLocationSetListStructureGen {
    public AbyssOutpostSetLocationList(@NotNull List<StructureGenerator> structurePool,
                                       @Nullable NumberProvider chunkRangeX,
                                       @Nullable NumberProvider chunkRangeZ, @Nullable NumberProvider minDistanceApart, @NotNull String id) {
        super(structurePool, chunkRangeX, chunkRangeZ, minDistanceApart, id);
    }

    @Override
    public void onComplete(World world,Chunk chunk) {
        super.onComplete(world, chunk);
        List<AbyssOutpostData> previousOwners = AbyssWorld.WORLD_TO_ABYSS_OUTPOST_OWNERS.remove(world.key());
        if(previousOwners == null || previousOwners.isEmpty()) return;

        CruxWorld crux = CruxCore.core().worldManager().getWorld(world.key());
        if(crux == null) return;

        StructureWorldModule module = crux.getModule(StructureWorldModule.class);
        if(module == null) return;

        List<AbyssOutpostData> dataList = new ArrayList<>();
        module.getStored(stored -> stored.has(AbyssComponents.ABYSS_OUTPOST_DATA)).forEach(stored ->{
            AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
            Objects.requireNonNull(data);
            dataList.add(data);
        });
        if(dataList.isEmpty()){
            Crux.log(Level.WARNING, world.getName() + " abyss world had previous outpost owners but no abyss outposts were generated!");
            return;
        }
        if(dataList.size() < previousOwners.size()){
            Crux.log(Level.WARNING, world.getName() + " abyss world had previous outpost owners but the generated abyss outposts that were generated is less than the previous owner amount!" +
                " PreviousOwners=" + previousOwners.size() + ", AbyssOutposts=" + dataList.size());
            Crux.log(Level.WARNING, world.getName() + " abyss world... Attempting to set previous owners anyway.");
        }

        Collections.shuffle(previousOwners);
        Collections.shuffle(dataList);

        int index = -1;
        for(var oldData : previousOwners){
            index++;
            if(index >= dataList.size()) break;
            AbyssOutpostData data = dataList.get(index);
            data.owner = oldData.owner;
            data.timeCaptured = oldData.timeCaptured;
            oldData.getUpgrades().forEach(data::setUpgradeLevel);
        }
    }
}

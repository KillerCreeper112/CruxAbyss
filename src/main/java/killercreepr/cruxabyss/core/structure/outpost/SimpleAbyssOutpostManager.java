package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.cruxabyss.api.structure.outpost.AbyssOutpostManager;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SimpleAbyssOutpostManager implements AbyssOutpostManager {
    @NotNull
    @Override
    public Collection<AbyssOutpostData> getAllOwnedAbyssOutposts(@NotNull UUID uuid) {
        Collection<AbyssOutpostData> list = new HashSet<>();
        forEachAbyssOutpost(e ->{
            if(e.isOwner(uuid)) list.add(e);
        });
        return list;
    }

    @NotNull
    @Override
    public Collection<AbyssOutpostData> getAllFriendlyAbyssOutposts(@NotNull UUID uuid) {
        Collection<AbyssOutpostData> list = new HashSet<>();
        forEachAbyssOutpost(e ->{
            if(e.isMemberOrOwner(uuid)) list.add(e);
        });
        return list;
    }

    @NotNull
    @Override
    public Collection<AbyssOutpostData> getAllAbyssOutposts() {
        Collection<AbyssOutpostData> list = new HashSet<>();
        forEachAbyssOutpost(list::add);
        return list;
    }

    @NotNull
    @Override
    public Collection<AbyssOutpostData> getAbyssOutposts(@Nullable Predicate<AbyssOutpostData> filter) {
        Collection<AbyssOutpostData> list = new HashSet<>();
        if(filter == null){
            forEachAbyssOutpost(list::add);
            return list;
        }
        forEachAbyssOutpost(e ->{
            if(filter.test(e)) list.add(e);
        });
        return list;
    }

    @Override
    public boolean checkFirstTrue(@NotNull Predicate<AbyssOutpostData> filter) {
        for(CruxWorld world : CruxCore.core().worldManager().getWorlds()){
            StructureWorldModule module = world.getModule(StructureWorldModule.class);
            if(module==null) continue;
            for (StoredStructure stored : module.getStored(e -> e.has(AbyssComponents.ABYSS_OUTPOST_DATA))) {
                AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
                if(filter.test(data)) return true;
            }
        }
        return false;
    }

    @Override
    public void forEachAbyssOutpost(@NotNull Consumer<AbyssOutpostData> consumer) {
        for(CruxWorld world : CruxCore.core().worldManager().getWorlds()){
            StructureWorldModule module = world.getModule(StructureWorldModule.class);
            if(module==null) continue;
            for (StoredStructure stored : module.getStored(e -> e.has(AbyssComponents.ABYSS_OUTPOST_DATA))) {
                AbyssOutpostData data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);
                consumer.accept(data);
            }
        }
    }
}

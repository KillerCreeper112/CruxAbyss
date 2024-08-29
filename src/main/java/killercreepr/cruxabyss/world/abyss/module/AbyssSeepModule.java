package killercreepr.cruxabyss.world.abyss.module;

import killercreepr.cruxabyss.world.generation.populator.AbyssOverworldPopulator;
import killercreepr.cruxworlds.world.CruxWorld;
import killercreepr.cruxworlds.world.module.SimpleWorldModule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class AbyssSeepModule extends SimpleWorldModule {
    public AbyssSeepModule(@NotNull CruxWorld parent) {
        super(parent);
    }

    @Override
    public void onInitiate() {
        super.onInitiate();
        World world = parent.toBukkitWorld();
        world.getPopulators().add(new AbyssOverworldPopulator());
    }
}

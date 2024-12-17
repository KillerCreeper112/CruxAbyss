package killercreepr.cruxabyss.core.world.abyss;

import killercreepr.cruxworlds.world.CruxWorld;
import killercreepr.cruxworlds.world.creator.CruxWorldType;
import killercreepr.cruxworlds.world.manager.CruxWorldManager;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;

public class AbyssWorldType implements CruxWorldType {
    protected final Key key;
    protected final CruxWorldManager worldManager;

    public AbyssWorldType(Key key, CruxWorldManager worldManager) {
        this.key = key;
        this.worldManager = worldManager;
    }

    @Override
    public @NotNull CruxWorld generate(@NotNull String name) {
        World world = new WorldCreator(name).type(WorldType.AMPLIFIED)/*.generator(new AbyssChunkGenerator())*/.createWorld();
        if(world==null) throw new IllegalStateException("Cannot generate world! " + key);

        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(4096D);

        CruxWorld activeWorld = worldManager.getWorld(name);
        if(!(activeWorld instanceof AbyssWorld a)) throw new UnsupportedOperationException(name + " is not an AbyssWorld!");
        return a;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}

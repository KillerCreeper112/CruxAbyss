package killercreepr.cruxabyss.core.world.abyss;

import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
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
    public @NotNull String defaultWorldName() {
        return "world_abyss";
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}

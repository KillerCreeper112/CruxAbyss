package killercreepr.cruxabyss.core.world.abyss;

import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.manager.CruxWorldManager;
import killercreepr.cruxworlds.api.world.type.CruxWorldType;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

public class AbyssWorldType implements CruxWorldType {
    protected final Key key;
    protected final CruxWorldManager worldManager;

    public AbyssWorldType(Key key, CruxWorldManager worldManager) {
        this.key = key;
        this.worldManager = worldManager;
    }

    @Override
    public @NotNull CruxWorld generate(@NotNull Key name) {
        World world = new WorldCreator(name.value()).type(WorldType.AMPLIFIED)/*.generator(new AbyssChunkGenerator())*/.createWorld();
        if(world==null) throw new IllegalStateException("Cannot generate world! " + key);

        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(4096D);
        world.setDifficulty(Difficulty.NORMAL);
        world.setGameRule(GameRule.LOCATOR_BAR, false);

        CruxWorld activeWorld = worldManager.getWorld(name);
        if(!(activeWorld instanceof AbyssWorld a)) throw new UnsupportedOperationException(name + " is not an AbyssWorld!");
        return a;
    }

    @Override
    public @NotNull Key defaultWorldKey() {
        return Key.key("world_abyss");
    }

    @Override
    public @NotNull Key key() {
        return key;
    }
}

package killercreepr.cruxabyss.game;

import killercreepr.crux.plugin.CruxPlugin;
import killercreepr.crux.registry.MappedRegistry;
import killercreepr.crux.registry.SimpleMappedRegistry;
import killercreepr.cruxabyss.world.generation.GenerationListener;
import killercreepr.cruxstructures.manager.StructureManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssWorld extends GameManager{
    public static @Nullable AbyssWorld getOrCreate(@NotNull CruxPlugin plugin, @NotNull String worldName){
        AbyssWorld check = ACTIVE.get(worldName);
        if(check!=null) return check;

        World world = Bukkit.getWorld(worldName);
        if(world != null) return new AbyssWorld(plugin, world);

        GenerationListener.addInitWorld(worldName);
        world = new WorldCreator(worldName).type(WorldType.AMPLIFIED).createWorld();
        if(world==null) return null;

        return new AbyssWorld(plugin, world);
    }

    public static MappedRegistry<String, AbyssWorld> ACTIVE = new SimpleMappedRegistry<>();
    public AbyssWorld(@NotNull CruxPlugin plugin, @NotNull World world) {
        super(plugin, world);
    }

    @Override
    public void tick() {
        super.tick();
    }
}

package killercreepr.cruxabyss.world.generation;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.crux.Crux;
import killercreepr.cruxabyss.world.generation.decoration.RockPopulator;
import killercreepr.cruxabyss.world.generation.populator.AbyssPopulator;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class GenerationListener implements Listener {
    private static final Set<String> initWorlds = new HashSet<>();
    public static void addInitWorld(@NotNull String name){
        initWorlds.add(name);
    }

    private static final Set<World> WORLDS = new HashSet<>();
    //tp -121 ~ 108
    private final int placeX = -121;
    private final int placeZ = 108;
    private final int minY = 63;

    public static void register(@NotNull World world){
        WORLDS.add(world);
    }

    public static void unregister(@NotNull World world){
        WORLDS.remove(world);
    }

    public static boolean has(@NotNull World world){
        return WORLDS.contains(world);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        WORLDS.remove(event.getWorld());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World w = event.getWorld();
        if(initWorlds.contains(event.getWorld().getName())){
            w.getPopulators().add(new RockPopulator(new BlockGenerator() {
                @Override
                public void set(@NotNull LimitedRegion region, int x, int y, int z) {
                    region.setType(x,y,z, Material.IRON_ORE);
                }
            }));
            AbyssPopulator master = new AbyssPopulator();
            w.getPopulators().add(master);
            //initWorlds.remove(event.getWorld().getName());
        }
    }
/*    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if(!event.isNewChunk() || !initWorlds.contains(event.getWorld().getUID())) return;
        Chunk chunk = event.getChunk();
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int xx = x + (chunk.getX() * 16);
                int zz = z + (chunk.getZ() * 16);
                for(int y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight() ; y++){
                    if(xx == placeX && placeZ == zz && y == minY){
                        //place
                        try {
                            placeSchematic(new Location(chunk.getWorld(), xx, y, zz), "test", false, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }*/

/*    @EventHandler(ignoreCancelled = true)
    public void onChunkPopulate(ChunkPopulateEvent event) {
        Chunk chunk = event.getChunk();
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int xx = x + (chunk.getX() * 16);
                int zz = z + (chunk.getZ() * 16);
                for(int y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight() ; y++){
                    if(xx == placeX && placeZ == zz && y == minY){
                        //place
                        try {
                            placeSchematic(new Location(chunk.getWorld(), xx, y, zz), "test", false, true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }*/

    public static void placeSchematic(@NotNull Location loc, @NotNull String filename, boolean ignoreAirBlocks, boolean randomRotation) throws IOException, WorldEditException {
        File schematicFile = new File(WorldEdit.getInstance().getSchematicsFolderPath().toString() + "/" + filename + ".schem");
        if(!schematicFile.exists()) {
            throw new RuntimeException("Cannot find schematic file!");
        }
        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        if(format == null) {
            Crux.log(Level.WARNING, "Invalid schematic format for schematic " + filename + "!");
            return;
        }

        Clipboard clipboard = format.load(schematicFile);
        //paste schematic
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(loc.getWorld()))) {
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            if(randomRotation){
                AffineTransform transform = new AffineTransform();
                transform = transform.rotateY(new Random().nextInt(4) * 90);
                holder.setTransform(holder.getTransform().combine(transform));
            }
            Operation operation = holder
                    .createPaste(editSession)
                    .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                    .ignoreAirBlocks(ignoreAirBlocks)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            Crux.log(Level.WARNING, "Couldn't place clipboard at: '" + loc + "'.");
            e.printStackTrace();
        }
    }
}

package killercreepr.cruxabyss.structure;

import com.sk89q.worldedit.session.ClipboardHolder;
import killercreepr.crux.Crux;
import killercreepr.crux.data.StoredChunk;
import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxstructures.event.StructurePlaceEvent;
import killercreepr.cruxstructures.registries.StructureRegistries;
import killercreepr.cruxstructures.structure.Structure;
import killercreepr.cruxstructures.structure.impl.CfgStoredBlocksStructure;
import killercreepr.cruxstructures.structure.module.StructureModule;
import killercreepr.cruxstructures.structure.stored.StoredStructure;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AbyssOutpost extends CfgStoredBlocksStructure {
    public AbyssOutpost(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent, @NotNull Collection<StructureModule> modules) {
        super(key, holder, persistent, modules);
    }

    public AbyssOutpost(@NotNull Key key, @NotNull String filename, boolean persistent, @NotNull Collection<StructureModule> modules) {
        super(key, filename, persistent, modules);
    }

    public AbyssOutpost(@NotNull Key key, @NotNull File schematicFile, boolean persistent, @NotNull Collection<StructureModule> modules) {
        super(key, schematicFile, persistent, modules);
    }

    @Override
    public @Nullable StoredStructure buildStored(@NotNull Location center, double rotation) {
        return new StoredAbyssOutpost(this, StoredChunk.from(center), CruxPosition.block(center), rotation);
    }

    @Override
    public @NotNull StructurePlaceEvent place(@NotNull Location at, double rotation) {
        StructurePlaceEvent event = super.place(at, rotation);
        if(event.isCancelled()) return event;

        Walls walls = new Walls();
        walls.getStructures().putAll(new HashMap<>(){{
            put(BlockFace.NORTH, List.of(Crux.key("abyss_outpost_wall_north_middle_1")));
            put(BlockFace.EAST, List.of(Crux.key("abyss_outpost_wall_east_middle_1")));
            put(BlockFace.WEST, List.of(Crux.key("abyss_outpost_wall_west_middle_1")));
            put(BlockFace.SOUTH, List.of(Crux.key("abyss_outpost_wall_south_middle_1")));


            put(BlockFace.SOUTH_EAST, List.of(Crux.key("abyss_outpost_wall_corner_south_east")));
            put(BlockFace.SOUTH_WEST, List.of(Crux.key("abyss_outpost_wall_corner_south_west")));
            put(BlockFace.NORTH_EAST, List.of(Crux.key("abyss_outpost_wall_corner_north_east")));
            put(BlockFace.NORTH_WEST, List.of(Crux.key("abyss_outpost_wall_corner_north_west")));
        }});

        double wallRotation = (CruxMath.RANDOM.nextInt( 4) * 90);
        walls.getStructures().forEach((face, structures) ->{
            Key first = new ArrayList<>(structures).getFirst();
            Structure structure = StructureRegistries.STRUCTURES.get(first);

            int width = 12;
            //if(!face.isCartesian()) width +=3;

            CruxPosition spawn = CruxPosition.location(
                at.clone().add(
                    face.getModX() * width,
                    face.getModY() * width,
                    face.getModZ() * width
                )
            );

            spawn = spawn.rotateAroundY(CruxPosition.location(at), wallRotation);

            structure.place(spawn.toLocation(at.getWorld()), wallRotation);
        });

        return event;
    }

    public static double getWidth(BoundingBox boundingBox, BlockFace direction) {
        switch (direction) {
            case EAST:
            case WEST:
                return boundingBox.getWidthX();
            case NORTH:
            case SOUTH:
                return boundingBox.getWidthZ();
            default:
                return boundingBox.getMaxZ() - boundingBox.getMinZ();
        }
    }
}

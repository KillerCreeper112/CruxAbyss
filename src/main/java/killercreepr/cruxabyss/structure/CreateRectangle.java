package killercreepr.cruxabyss.structure;

import killercreepr.crux.location.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateRectangle {
    protected final Location pos1;
    protected final Location pos2;
    protected final boolean wire;
    protected final boolean hollow;
    protected final double spacing;
    protected final int time;
    protected final boolean inverted;

    public CreateRectangle( @NotNull World world, @NotNull BoundingBox box, boolean wire, boolean hollow,
                           double spacing) {
        this(new Location(world, box.getMinX(), box.getMinY(), box.getMinZ()),new Location(world, box.getMaxX(), box.getMaxY(), box.getMaxZ()),
                wire,hollow,spacing);
    }

    public CreateRectangle(@NotNull Location pos1, @NotNull Location pos2, boolean wire, boolean hollow,
                           double spacing) {
        this(pos1,pos2,wire,hollow,spacing,0);
    }

    public CreateRectangle(@NotNull Location pos1, @NotNull Location pos2, boolean wire, boolean hollow,
                           double spacing, int time) {
        this(pos1,pos2,wire,hollow,spacing,time,false);
    }

    public CreateRectangle(@NotNull Location pos1, @NotNull Location pos2, boolean wire, boolean hollow,
                           double spacing, int time, boolean inverted) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.wire = wire;
        this.hollow = hollow;
        this.spacing = spacing;
        this.time = time;
        this.inverted = inverted;
    }

    public @NotNull Location getPos1() {
        return pos1;
    }

    public @NotNull Location getPos2() {
        return pos2;
    }

    public boolean isWire() {
        return wire;
    }

    public boolean isHollow() {
        return hollow;
    }

    public double getSpacing() {
        return spacing;
    }

    public int getTime() {
        return time;
    }

    public boolean isInverted() {
        return inverted;
    }

    public @NotNull List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        final Vector max = Vector.getMaximum(pos1.toVector(), pos2.toVector());
        final Vector min = Vector.getMinimum(pos1.toVector(), pos2.toVector());
        final Location minLocation = new Location(pos1.getWorld(),
                min.getX(), min.getY(), min.getZ(), pos1.getYaw(), pos1.getPitch());

        final int xParticleAmount = Math.max(1, (int) (((max.getX()-min.getX()) / spacing)));
        final int yParticleAmount = Math.max(1, (int) (((max.getY()-min.getY()) / spacing)));
        final int zParticleAmount = Math.max(1, (int) (((max.getZ()-min.getZ()) / spacing)));

        for(int x = 0; x <= xParticleAmount; x++){
            for(int y = 0; y <= yParticleAmount; y++){
                for(int z = 0; z <= zParticleAmount; z++){
                    if (wire) {
                        if(!(((x == 0 || x == xParticleAmount)) &&
                                ((y == 0 || y == yParticleAmount)) ||
                                ((x == 0 || x == xParticleAmount)) &&
                                        ((z == 0 || z == zParticleAmount)) ||
                                ((y == 0 || y == yParticleAmount)) &&
                                        ((z == 0 || z == zParticleAmount)))) continue;
                    }else if (hollow) {
                        if (!(x == 0 || x == xParticleAmount || y == 0 ||
                                y == yParticleAmount || z == 0 || z == zParticleAmount)) continue;
                    }
                    Vector vec = new Vector((max.getX() - min.getX()) * ((double) x /xParticleAmount),
                            (max.getY() - min.getY()) * ((double) y /yParticleAmount),
                            (max.getZ() - min.getZ()) * ((double) z /zParticleAmount));
                    locations.add(minLocation.clone().add(vec));
                }
            }
        }
        return locations;
    }

    public boolean playEvent(@NotNull DynamicLocation l){
        return true;
    }
}

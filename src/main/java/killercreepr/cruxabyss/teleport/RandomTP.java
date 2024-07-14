package killercreepr.cruxabyss.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;

public class RandomTP {
    protected final @NotNull World world;
    public RandomTP(@NotNull World world) {
        this.world = world;
    }

    public static HashSet<Material> bad_blocks = new HashSet<>();

    static {
        bad_blocks.add(Material.LAVA);
        bad_blocks.add(Material.FIRE);
        bad_blocks.add(Material.CACTUS);
        bad_blocks.add(Material.WATER);
        bad_blocks.add(Material.MAGMA_BLOCK);
    }

    public Location generateLocation() {
        //Called upon when generating a location
        Random random = new Random();

        int x = 0;
        int y = 0;
        int z = 0;

        int border = (int) world.getWorldBorder().getSize()/2;

        int var1, var2;
        var1 = random.nextInt(border);
        var2 = random.nextInt(border);

        int var3 = random.nextInt(2); //basically a random boolean
        if (var3 == 1) {
            var1 = var1 * -1; //50% chance the x coordinate will be negative
        }
        var3 = random.nextInt(2);
        if (var3 == 1) {
            var2 = var2 * -1; //50% chance the x coordinate will be negative
        }

        x = var1;
        y = 150; //useless line of code :)
        z = var2;

        Location randomLocation = new Location(world, x, y, z);

        switch (randomLocation.getWorld().getEnvironment()) {
            case NORMAL:
                setYOver(randomLocation);
                break;
            case NETHER:
                setYNether(randomLocation);
                break;
            case THE_END:
                setYOver(randomLocation);
                break;
            default:
                setYOver(randomLocation);
                break;
        }
        return randomLocation;
        //Note: don't return null here
    }

    public void setYOver(Location randomLocation) {
        int y = randomLocation.getWorld().getHighestBlockYAt(randomLocation); //set the Y coordinate to the highest point
        randomLocation.setY(y + 1);
    }

    public void setYNether(Location randomLocation) {
        int x = randomLocation.getBlockX();
        int z = randomLocation.getBlockZ();
        int y = 126;
        for (int i = 0; i < randomLocation.getWorld().getMaxHeight(); i++) {
            Block cblock = randomLocation.getWorld().getBlockAt(x, i, z);
            Block ublock = randomLocation.getWorld().getBlockAt(x, i - 1, z);
            Block ablock = randomLocation.getWorld().getBlockAt(x, i + 1, z);
            if (!ablock.getType().isSolid() &&
                !bad_blocks.contains(ablock.getType()) &&
                cblock.getType().isSolid() &&
                !bad_blocks.contains(cblock) &&
                !ublock.getType().isSolid() &&
                !(cblock.getY() >= 126) //Check if the Y isn't above the nether roof
            ) {
                y = i;
                randomLocation.setY(y + 1);
                return;
            }
        }
        randomLocation.setY(y + 1);
    }

    public @Nullable Location randomTeleport(@NotNull Player player){
        Location spawn = startGenerateLocation(player, 128);
        if(spawn==null){
            player.sendMessage("Could not generate a location.");
            return null;
        }
        tp(player, spawn);
        return spawn;
    }


    /**
     * Generates a safe location
     *
     * @param player Player to get the world from
     * @return Returns the location if successful, returns null if it couldn't generate a location
     */
    public Location startGenerateLocation(Player player, int maxAttempts) {
//        int maxAttempts = Utils.getMaxAttempts();
        int attempts = 0;
        while (attempts < maxAttempts) {
            Location loc = generateLocation();
            if (!isLocationSafe(loc)) {
                attempts++;
            } else {
                attempts = 0;
                return loc;
            }
        }
        attempts = 0;
        return null;
    }


    @Contract("null -> false")
    public boolean isLocationSafe(@Nullable Location location) {
        if(location == null) return false;
        //Checking if the generated random location is safe
        /*ClaimHookManager claimHookManager = new ClaimHookManager(plugin);

        if (claimHookManager.isClaimedAt(location)) return false;*/

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = location.getWorld().getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);

        /*if (Utils.getBiomeBlacklistEnabled()) {
            for (String b : Utils.getBiomes()) {
                try {
                    if (Biome.valueOf(b).equals(location.getBlock().getBiome())) {
                        return false;
                    }
                } catch (Exception e) {
                    Log.log(Log.LogLevel.ERROR, "Wrong biome name was used in the config!");
                }
            }
        }*/

        //if (location.getBlockY() > Utils.getMaxYLevel()) return false;

        if (location.getWorld().getEnvironment() == World.Environment.NORMAL) {
            return !(bad_blocks.contains(above.getType()))
                && !(bad_blocks.contains(block.getType()))
                && !(bad_blocks.contains(below.getType()))
                && !(above.getType().isSolid())
                && !(block.getType().isSolid())
                && below.getType().isSolid();
        } else if (location.getWorld().getEnvironment() == World.Environment.NETHER) {
            return (
                !bad_blocks.contains(below.getType()) &&
                    !block.getType().isSolid() &&
                    !above.getType().isSolid() &&
                    below.getType().isSolid() &&
                    !(location.getBlockY() >= 126)
            );
        } else if (location.getWorld().getEnvironment() == World.Environment.THE_END) {
            return !(bad_blocks.contains(above.getType()))
                && !(bad_blocks.contains(block.getType()))
                && !(bad_blocks.contains(below.getType()))
                && !(above.getType().isSolid())
                && !(block.getType().isSolid())
                && below.getType().isSolid();
        } else {
            return false;
        }

    }

    public void tp(Player player, Location location) {
        location.add(0.5, 0, 0.5); //Set the location to the center of the block
        location.getWorld().getChunkAt(location.getBlock()).load(true);
        location.setPitch(player.getLocation().getPitch());
        location.setYaw(player.getLocation().getYaw());
        player.teleport(location);
    }
}

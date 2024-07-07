package killercreepr.cruxabyss.world;

import killercreepr.cruxabyss.world.generation.GenerationListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldManager {
    public static @NotNull List<String> getWorldsFromFiles(){
        List<String> list = new ArrayList<>();
        for(File f : Bukkit.getWorldContainer().listFiles()){
            if(!f.isDirectory()) continue;
            for(File folderF : f.listFiles()){
                if(folderF.getName().equals("level.dat")){
                    list.add(f.getName());
                    break;
                }
            }
        }
        return list;
    }

    public static @Nullable World getWorld(@NotNull String worldName){
        for(File f : Bukkit.getWorldContainer().listFiles()){
            if(!f.getName().equals(worldName) || !f.isDirectory()) continue;
            boolean foundLevel = false;
            for(File folderF : f.listFiles()){
                if(folderF.getName().equals("level.dat")){
                    foundLevel = true;
                    break;
                }
            }
            if(!foundLevel) break;
            return new WorldCreator(worldName).type(WorldType.AMPLIFIED).createWorld();
        }
        return null;
    }

    public static @Nullable World getOrCreateGrimWorld(@NotNull String worldName){
        World world = Bukkit.getWorld(worldName);
        if(world != null) return world;
        GenerationListener.addInitWorld(worldName);
        world = new WorldCreator(worldName).type(WorldType.AMPLIFIED).createWorld();
        return world;
    }

    public static void visitWorld(@NotNull String worldName, @NotNull Player visitor){
        final World world = getWorld(worldName);
        if(world == null){
            visitor.sendMessage(worldName + " does not exist.");
            return;
        }
        visitor.sendMessage("Teleporting to " + worldName + ".");
        visitor.teleport(world.getSpawnLocation());
    }

    public static void visitOrCreateWorld(@NotNull String worldName, @NotNull Player p){
        World world = getWorld(worldName);
        if(world != null){
            p.sendMessage("Teleporting to " + worldName + ".");
            p.teleport(world.getSpawnLocation());
            return;
        }
        p.sendMessage("Attempting to create a new world...");

        final World template = Bukkit.createWorld(new WorldCreator("realm_schematic"));
        if(template == null){
            p.sendMessage("Error, SCHEMATIC_NULL");
            return;
        }
        world = copyWorld(template, worldName);
        if(world == null){
            p.sendMessage("Error, WORLD_NULL");
            return;
        }
        p.teleport(world.getSpawnLocation());
        Bukkit.unloadWorld(template, false);
    }

    private static World copyWorld(@NotNull World originalWorld, @NotNull String newWorldName) {
        copyFileStructure(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));
        return new WorldCreator(newWorldName).createWorld();
    }

    private static void copyFileStructure(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");
                    String[] files = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteWorld(@NotNull String name){
        for(File f : Bukkit.getWorldContainer().listFiles()){
            if(!f.getName().equals(name) || !f.isDirectory()) continue;
            boolean foundLevel = false;
            for(File folderF : f.listFiles()){
                if(folderF.getName().equals("level.dat")){
                    foundLevel = true;
                    break;
                }
            }
            if(foundLevel) return f.delete();
            break;
        }
        return false;
    }

    public static boolean unloadWorld(@NotNull World world, boolean save) {
        return Bukkit.getServer().unloadWorld(world, save);
    }
}

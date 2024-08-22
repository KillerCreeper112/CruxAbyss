package killercreepr.cruxabyss.world.generation;

import killercreepr.crux.data.world.CruxPosition;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StructurePopulator extends GrimPopulator {
    private final Structure structure;
    private final int chance;
    public StructurePopulator(@NotNull Structure structure) {
        this.structure = structure;
        this.chance = 75;
    }
    public StructurePopulator(@NotNull Structure structure, int chance) {
        this.structure = structure;
        this.chance = chance;
    }

    @Override
    public void populateBeforeFunctions(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if(CruxMath.random(1, 100) <= chance) return;
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                int xx = x + (chunkX * 16);
                int zz = z + (chunkZ * 16);
                for(int y = worldInfo.getMaxHeight()-10; y > 65; y--){
                    Location l = new Location(limitedRegion.getWorld(), xx, y, zz);
                    Block b = limitedRegion.getBlockState(l).getBlock();
                    Block ground = limitedRegion.getBlockState(xx, y-1, zz).getBlock();
                    if((b.isReplaceable() || b.isEmpty()) && (ground.isSolid())){
                        place(worldInfo, random, limitedRegion, l);
                        //structure.place(l, true, StructureRotation.NONE, Mirror.NONE, -1, 1f, random);
                        return;
                    }
                }
            }
        }
    }

    public void place(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion, @NotNull Location corner){
        for(Palette p : structure.getPalettes()){
            for(BlockState s : p.getBlocks()){
                int x = corner.getBlockX() + s.getX();
                int y = corner.getBlockY() + s.getY();
                int z = corner.getBlockZ() + s.getZ();
                function(worldInfo, random, limitedRegion, CruxPosition.block(x, y, z), new ChunkFunction() {
                    @Override
                    public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                        limitedRegion.setType(x, y, z, s.getType());
                        if(limitedRegion.getBlockData(x, y, z).getClass().isAssignableFrom(s.getBlockData().getClass())){
                            limitedRegion.setBlockData(x, y, z, s.getBlockData());
                        }
                    }
                });
            }
        }
    }
}

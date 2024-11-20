package killercreepr.cruxabyss.world.generation.decoration;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxabyss.world.generation.populator.GrimPopulator;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ToxicMireTreePopulator extends GrimPopulator {
    public void place(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion region, @NotNull Location center){
        int height = CruxMath.random(5, 10, random);

        for(int h = 0; h < height; h++){
            Location rel = center.clone().add(0, h, 0);
            ChunkFunction function = new ChunkFunction() {
                @Override
                public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                    AbyssBlocks.PLAGUE_STEM.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.Y)
                        .setBlock(region, rel.getBlockX(), rel.getBlockY(), rel.getBlockZ());
                    //limitedRegion.setType(rel.getBlockX(), rel.getBlockY(), rel.getBlockZ(), Material.STONE);
                }
            };
            function(worldInfo, random, region, CruxPosition.block(rel), function);

            //reached max height
            if((h+1) == height){
                generateBranch(worldInfo, random, region, rel.clone().add(0, random.nextBoolean() ? 0 : -1, 0), 1, 0);
                generateBranch(worldInfo, random, region, rel.clone().add(0, random.nextBoolean() ? 0 : -1, 0), -1, 0);
                generateBranch(worldInfo, random, region, rel.clone().add(0, random.nextBoolean() ? 0 : -1, 0), 0, 1);
                generateBranch(worldInfo, random, region, rel.clone().add(0, random.nextBoolean() ? 0 : -1, 0), 0, -1);
            }
        }
    }

    public CruxBlock fromDirection(int xDir, int zDir){
        if(xDir == 1 || xDir == -1){
            return AbyssBlocks.PLAGUE_STEM.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.X);
        }
        if(zDir == 1 || zDir == -1){
            return AbyssBlocks.PLAGUE_STEM.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.Z);
        }
        return AbyssBlocks.PLAGUE_STEM.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(Axis.Y);
    }

    public Location addDirection(Location l, int xDir, int zDir, int amount){
        Location pos = l.clone();
        if(xDir == 1 || xDir == -1){
            pos.setX(pos.getX() + (xDir*amount));
        }
        if(zDir == 1 || zDir == -1){
            pos.setZ(pos.getZ() + (zDir*amount));
        }
        return pos;
    }

    public void generateLeaves(@NotNull WorldInfo worldInfo,
                               @NotNull Random random,
                               @NotNull LimitedRegion region,
                               @NotNull Location center){
        int height = CruxMath.random(1, 4, random);
        for(int h = 0; h < height; h++){
            Location pos = center.clone();
            pos.add(0, h, 0);
            ChunkFunction function = new ChunkFunction() {
                @Override
                public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                    AbyssBlocks.PLAGUE_WART.getBaseBlock().setBlock(region, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
                }
            };
            function(worldInfo, random, region, CruxPosition.block(pos), function);
        }
        if(CruxMath.testChance(random, 35D)) return;
        int amount = CruxMath.random(1, 3, random);
        int xAddon = CruxMath.random(-1, 1, random);
        int zAddon = CruxMath.random(-1, 1, random);

        for(int i = 0; i < amount; i++){
            for(int x = xAddon; x < 1; x++){
                for(int z = zAddon; z < 1; z++){
                    Location pos = center.clone();
                    pos.add(xAddon, 0, zAddon);

                    ChunkFunction function = new ChunkFunction() {
                        @Override
                        public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                            AbyssBlocks.PLAGUE_WART.getBaseBlock().setBlock(region, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
                        }
                    };
                    function(worldInfo, random, region, CruxPosition.block(pos), function);
                }
            }
        }
    }

    public void generateBranch(@NotNull WorldInfo worldInfo,
                               @NotNull Random random,
                               @NotNull LimitedRegion region,
                               @NotNull Location center, int xDir, int zDir){
        int length = CruxMath.random(1, 6);
        final int height = CruxMath.random(0, 3, random);
        int currentHeight = 0;

        int xAddon = 0;
        int zAddon = 0;
        for(int l = 0; l < length; l++){
            Location pos = addDirection(center, xDir, zDir, l);
            double heightChance = Math.min(1.0, (double)l / length);
            if(currentHeight < height && random.nextDouble() < heightChance){
                currentHeight++;
            }
            pos.setY(pos.getY() + currentHeight);
            if(random.nextDouble() < heightChance){
                xAddon += CruxMath.random(-1, 1, random);
                zAddon += CruxMath.random(-1, 1, random);
            }

            if(xDir != 0 && xAddon != 0) xAddon--;
            else if(zDir != 0 && zAddon != 0) zAddon--;

            pos.setX(pos.getX() + xAddon);
            pos.setZ(pos.getZ() + zAddon);
            ChunkFunction function = new ChunkFunction() {
                @Override
                public void accept(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull LimitedRegion limitedRegion) {
                    fromDirection(xDir, zDir).setBlock(region, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
                }
            };
            function(worldInfo, random, region, CruxPosition.block(pos), function);

            if(CruxMath.testChance(random, 25D)){
                generateLeaves(worldInfo, random, region, pos);
                continue;
            }
            if((l+1) == length){
                generateLeaves(worldInfo, random, region, pos);
            }
        }
    }
}

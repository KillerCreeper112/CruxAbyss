package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.block.AbyssBlocks;
import killercreepr.cruxabyss.core.persistence.AbyssPersist;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.event.CustomBlockExplodeEvent;
import killercreepr.cruxblocks.api.event.CustomEntityExplodeEvent;
import killercreepr.cruxcore.CruxCore;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbyssWoodFunctionListener implements Listener {
    public boolean canReflect(@NotNull Block block){
        CruxBlock crux = CruxCore.inst().cruxBlocks().getBlockRegistry().getByBlock(block);
        if(crux == null) return false;
        return AbyssBlocks.PLAGUE_STEM.containsBlock(crux) || AbyssBlocks.PLAGUE_WART.containsBlock(crux);
    }

    public @Nullable Float explodeChance(@NotNull Block block){
        CruxBlock crux = CruxCore.inst().cruxBlocks().getBlockRegistry().getByBlock(block);
        if(crux == null) return null;
        if(AbyssBlocks.PLAGUE_STEM.containsBlock(crux)){
            return 50f;
        }
        if(AbyssBlocks.PLAGUE_WART.containsBlock(crux)){
            return 40f;
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(CustomEntityExplodeEvent event) {
        onExplode(event.blockList());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(CustomBlockExplodeEvent event) {
        onExplode(event.blockList());
    }

    public void onExplode(@NotNull Collection<Block> blocks){
        blocks.removeIf(block ->{
            Float chance = explodeChance(block);
            if(chance==null) return false;
            return CruxMath.testChance(chance);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        Block block = event.getHitBlock();
        if(block==null) return;

        if(!canReflect(block)) return;

        int reflected = AbyssPersist.REFLECTED_TIMES.get(proj, 0);
        if(reflected > 3) return;

        Vector normal;
        if(event.getHitBlockFace() != null){
            normal = event.getHitBlockFace().getDirection().normalize();
        }else return;

        event.setCancelled(true);

        Vector dir = proj.getVelocity();
        Vector v = CruxMath.reflect(dir, normal).multiply(.7);
        double speed = v.length();
        if(speed < 0.32) return;
        proj.setVelocity(v);
        if(event.getHitEntity() == null){
            Location spawn = CruxLoc.shift(proj.getLocation(), v, .2, 0, 0);
            proj.getWorld().spawnEntity(spawn, proj.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM, x->{
                CruxTag.copyAll(x, proj);
                x.setVelocity(v);
                x.setGlowing(proj.isGlowing());
                x.setFireTicks(proj.getFireTicks());
                x.setPersistent(proj.isPersistent());
                x.setSilent(proj.isSilent());
                x.setGravity(proj.hasGravity());
                x.setInvisible(proj.isInvisible());
                x.setTicksLived(proj.getTicksLived());

                x.setVisualFire(proj.isVisualFire());
                if(x instanceof ThrowableProjectile newSpawn && proj instanceof ThrowableProjectile old){
                    newSpawn.setItem(old.getItem());
                    newSpawn.setShooter(old.getShooter());
                }
                if(x instanceof AbstractArrow newSpawn && proj instanceof AbstractArrow old){
                    newSpawn.setHitSound(old.getHitSound());
                    newSpawn.setDamage(old.getDamage());
                    newSpawn.setCritical(old.isCritical());
                    newSpawn.setItemStack(old.getItemStack());
                    newSpawn.setLifetimeTicks(old.getLifetimeTicks());
                    newSpawn.setPickupStatus(old.getPickupStatus());
                    newSpawn.setWeapon(old.getWeapon());
                    newSpawn.setPierceLevel(old.getPierceLevel());
                }
                AbyssPersist.REFLECTED_TIMES.set(x, reflected+1);
            });
            proj.remove();
        }
    }

}

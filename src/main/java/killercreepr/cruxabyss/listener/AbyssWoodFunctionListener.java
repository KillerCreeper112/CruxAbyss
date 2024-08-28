package killercreepr.cruxabyss.listener;

import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxTag;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class AbyssWoodFunctionListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        Block block = event.getHitBlock();
        if(block==null) return;
        event.setCancelled(true);
        Vector normal;
        if(event.getHitBlockFace() != null){
            normal = event.getHitBlockFace().getDirection().normalize();
        }else normal = null;
        if(normal != null){
            Vector dir = proj.getVelocity();
            Vector v = CruxMath.reflect(dir, normal).multiply(.7);
            proj.setVelocity(v);
            if(event.getHitEntity() == null){
                proj.getWorld().spawnEntity(proj.getLocation(), proj.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM, x->{
                    CruxTag.copyAll(x, proj);
                    x.setVelocity(v);

                    x.setVisualFire(proj.isVisualFire());
                    if(x instanceof ThrowableProjectile newSpawn && proj instanceof ThrowableProjectile old){
                        newSpawn.setItem(old.getItem());
                    }
                });
                proj.remove();
            }
        }
    }

}

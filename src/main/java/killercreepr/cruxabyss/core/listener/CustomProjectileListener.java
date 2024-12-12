package killercreepr.cruxabyss.core.listener;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxTag;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomProjectileListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity e = event.getEntity();
        if(!CruxTag.has(e, "plaguewing_spit")) return;
        e.getWorld().spawn(e.getLocation(), AreaEffectCloud.class, x ->{
            x.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 80, 1), false);
        });
        CreateSound.sound(Sound.ENTITY_PLAYER_SPLASH, 1.5f).playAt(e);
    }

}

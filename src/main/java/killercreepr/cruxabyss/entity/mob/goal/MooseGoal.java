package killercreepr.cruxabyss.entity.mob.goal;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.event.CruxEntityDamageEvent;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.GetEntityNear;
import killercreepr.crux.util.GetNear;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MooseGoal extends CruxMobModeledGoal {
    public MooseGoal(@NotNull Mob mob, @NotNull ActiveModel model) {
        super(mob, model);
        /*targetCheck = e -> (e instanceof Monster) || !(e instanceof Mob m) || !(Bukkit.getMobGoals().getGoal(m, key) instanceof MooseGoal);*/
    }
    private Item interested;
    private int shouldTake;
    private int cooldown = 0;
    private int taking;
    private UUID customer;
    @Override
    public void tick() {
        super.tick();
        if(target != null) return;
        if(cooldown > 0){
            cooldown--;
            if(cooldown == 80 && customer != null){
                customer = null;
                playAnimation("throw", true);
                mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1f, 1f);
                for(int i = CruxMath.random(3, 6); i > 0; i--){
                    /*todo Entity e = GrimEntity.WALKING_COD.spawn(
                            GameManager.get(mob),
                            mob.getEyeLocation()
                    );
                    Vector vel = mob.getEyeLocation().getDirection();
                    vel.add(new Vector(CruxMath.random(-.5f, .5f),
                            CruxMath.random(-.5f, .5f),
                            CruxMath.random(-.5f, .5f)));
                    e.setVelocity(vel);*/
                }
            }
            return;
        }
        if(interested != null){
            if(isPlayingAnimation("pickup")){
                taking++;
                if(taking == 2){
                    customer = interested.getThrower();
                    if(!interested.isValid()){
                        Entity d = interested.getThrower() == null ? null : Bukkit.getEntity(interested.getThrower());
                        if((d instanceof LivingEntity s)){
                            setTarget(s);
                        }else{
                            for(LivingEntity e : new GetEntityNear<>(LivingEntity.class)
                                .center(mob)
                                .range(8D)
                                .operation(GetNear.Operation.NEAREST)
                                .find()){
                                setTarget(e);
                                break;
                            }
                        }
                        return;
                    }
                    cooldown = 100;
                    interested.remove();
                    interested = null;
                    mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                    return;
                }
            }
            if(!interested.isValid()){
                interested = null;
                return;
            }
            mob.lookAt(interested);
            mob.getPathfinder().moveTo(interested.getLocation());
            shouldTake++;
            if(shouldTake >= 20){
                shouldTake = 0;
                playAnimation("pickup", true);
                taking = 0;
            }
            return;
        }
        shouldTake = 0;
        for(Item i : mob.getWorld().getNearbyEntitiesByType(Item.class, mob.getLocation(), 3D)){
            //if(!CustomMaterial.isCustomItem(i.getItemStack(), "bee")) continue;
            interested = i;
            break;
        }
    }

    @EventHandler
    private void cruxEntityDamage(CruxEntityDamageEvent event){
        if(!mob.equals(event.getDamager())) return;
        playAnimation("attack", true);
    }
}

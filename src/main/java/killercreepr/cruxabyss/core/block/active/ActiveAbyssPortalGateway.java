package killercreepr.cruxabyss.core.block.active;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.component.impl.AbyssPortalGateway;
import killercreepr.cruxabyss.core.entity.goal.AbyssAltarPortalGoal;
import killercreepr.cruxabyss.core.entity.goal.AbyssReturnPortalGoal;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxTickedBlock;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ActiveAbyssPortalGateway extends SimpleActiveCruxBlock implements ActiveCruxTickedBlock {
    protected final AbyssPortalGateway data;
    protected final int tickPeriod;
    protected final int checkRange;
    protected final Location center;
    public ActiveAbyssPortalGateway(@NotNull Block block, @NotNull CruxBlock cruxBlock, AbyssPortalGateway data) {
        super(block, cruxBlock);
        this.data = data;
        this.tickPeriod = data.getTickPeriod().value().intValue();
        this.checkRange = data.getCheckRange().value().intValue();
        this.center = block.getLocation().toCenterLocation();
    }

    public AbyssPortalGateway getData() {
        return data;
    }

    @Override
    public void stopped() {
        Crux.scheduler().runTask(this::removePortal);
    }

    protected int tick = 0;
    protected int cooldown;
    protected Entity spawnedPortal;
    @Override
    public void tick() {
        if(cooldown > 0){
            cooldown--;
            return;
        }
        tick++;
        if(tick % tickPeriod != 0) return;
        tick = 0;
        Crux.scheduler().runTask(task);
    }

    public final Runnable task = new Runnable() {
        @Override
        public void run() {
            Player p = getNearestPlayer();
            if(p != null){
                if(hasSpawnedPortal()) return;
                cooldown = data.getCooldown().value().intValue();
                spawnPortal();
                Location spawn = block.getLocation().toCenterLocation().add(0, 1, 0);
                Vector dir = p.getLocation().add(0, p.getHeight()/2, 0).toVector().subtract(spawn.toVector());
                int amount = CruxMath.random(5, 7);
                while(amount > 0){
                    amount--;
                    new ParticleBuilder(Particle.DRAGON_BREATH)
                        .location(spawn.clone().add(
                            CruxMath.random(-.1, .1),
                            CruxMath.random(-.1, .1),
                            CruxMath.random(-.1, .1)
                        ))
                        .data(0.3f)
                        .count(0)
                        .offset(dir.getX(), dir.getY(), dir.getZ())
                        .extra(CruxMath.random(.05, .1))
                        .spawn()
                    ;
                }
                return;
            }
            cooldown = data.getCooldown().value().intValue();
            removePortal();
        }
    };

    public void removePortal(){
        if(spawnedPortal == null) return;
        if(spawnedPortal instanceof LivingEntity l) l.damage(999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
        else spawnedPortal.remove();
        if(spawnedPortal.isValid()) spawnedPortal.remove();
        spawnedPortal = null;
        if(data.getDespawnSound() != null){
            data.getDespawnSound().playAt(center);
        }
    }

    public Entity spawnPortal(){
        if(data.getSpawnSound() != null){
            data.getSpawnSound().playAt(center);
        }
        spawnedPortal = AbyssMob.RETURN_PORTAL.spawn(center.clone().add(0, .52, 0), data.getDestination().value());
        if(spawnedPortal instanceof Mob mob){
            mob.setPersistent(false);
            mob.setRemoveWhenFarAway(true);
            AbyssReturnPortalGoal goal = (AbyssReturnPortalGoal) Crux.getServer().getMobGoals().getGoal(mob, AbyssAltarPortalGoal.defaultKey());
            if(goal != null){
                goal.setLifeSpan(-1);
                goal.setEventReason("abyss_portal_gateway");
            }
        }
        return spawnedPortal;
    }

    public boolean hasSpawnedPortal(){
        return spawnedPortal != null && spawnedPortal.isValid();
    }

    public boolean invisibleCanBeSeen(Player p){
        if(p.hasPotionEffect(PotionEffectType.GLOWING)) return true;
        for(EquipmentSlot slot : EquipmentSlot.values()){
            if(!p.canUseEquipmentSlot(slot)) continue;
            EntityEquipment equipment = p.getEquipment();
            ItemStack item = equipment.getItem(slot);
            if(!CruxItem.isEmpty(item)) return true;
        }
        return false;
    }

    public boolean canBeSeen(Player p){
        if(p.getGameMode() == GameMode.SPECTATOR) return false;
        if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            return invisibleCanBeSeen(p);
        }
        return true;
    }

    public Collection<Player> getPlayersInRange(){
        return block.getWorld().getNearbyPlayers(center, checkRange, this::canBeSeen);
    }

    public Player getNearestPlayer(){
        for(Player p : getPlayersInRange()){
            return p;
        }
        return null;
    }

    public boolean hasPlayersInRange(){
        return !getPlayersInRange().isEmpty();
    }
}

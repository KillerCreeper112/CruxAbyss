package killercreepr.cruxabyss.entity.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.location.DynamicLocation;
import killercreepr.crux.persistence.CruxPersistence;
import killercreepr.crux.util.*;
import killercreepr.cruxabyss.CruxAbyss;
import killercreepr.cruxabyss.entity.mob.AbyssMob;
import killercreepr.cruxabyss.event.EntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.event.SuccessfulEntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.item.AbyssItemTags;
import killercreepr.cruxabyss.structure.StoredAbyssSafezone;
import killercreepr.cruxabyss.values.ValuesProvider;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxteleport.teleport.world.RandomWorldTP;
import killercreepr.usurvive.USurvivePlugin;
import killercreepr.usurvive.death.DeathManager;
import killercreepr.usurvive.death.PlayerDeath;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class AbyssAltarPortalGoal extends CruxMobModeledGoal {
    public AbyssAltarPortalGoal(@NotNull Mob mob) {
        this(CruxMobGoal.defaultKey(),mob);
    }

    protected World world;
    protected ItemStack crystal;
    public AbyssAltarPortalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        attemptGetWorld();
        this.crystal = CruxTag.get(mob, "crystal_item", CruxPersistence.ITEM_STACK, null);
    }

    public void setColor(Color color){
        applyModel(model ->{
            model.setDefaultTint(color);
            model.setDamageTint(color);
        });
    }

    public void setCrystal(ItemStack crystal) {
        this.crystal = crystal;
        CruxTag.set(mob, "crystal_item", CruxPersistence.ITEM_STACK, crystal);
    }

    public ItemStack getCrystal() {
        return crystal;
    }

    public void attemptGetWorld(){
        this.world = CruxWorldUtil.getOrLoadWorld("world_abyss");
    }

    public RandomWorldTP buildRandomTP(Player p){
        if(crystal == null){
            return RandomWorldTP.tp(world);
        }
        ValuesProvider cfg = CruxAbyss.inst().values();
        if(AbyssItemTags.ABYSS_GEMS_DEATH.isTagged(crystal)){
            DeathManager manager = USurvivePlugin.inst().getDeathManager();
            PlayerDeath death = CruxCollection.getRandom(manager.getAllDeathsInWorld(p.getUniqueId(), world.getUID()));
            if(death == null) return RandomWorldTP.tp(world);

            Location l = death.getPosition().toLocation(world);
            return RandomWorldTP.tpNear(l, cfg.ABYSS_GEMS_DEATH_TP_NEAR_DISTANCE());
        }
        if(AbyssItemTags.ABYSS_GEMS_SAFEZONE.isTagged(crystal)){
            StoredAbyssSafezone safezone = CruxCollection.getRandom(
                CruxCore.inst().structureManager().getStored(world.getUID(), StoredAbyssSafezone.class, null)
            );
            if(safezone == null) return RandomWorldTP.tp(world);
            Location center = safezone.getPosition().toLocation(world);
            return RandomWorldTP.tpNear(center, cfg.ABYSS_GEMS_SAFEZONE_TP_NEAR_DISTANCE());
        }
        return RandomWorldTP.tp(world);
    }

    protected int particle;
    public void particleTick(){
        if(particle > 0){
            particle--;
            return;
        }
        particle = CruxMath.random(5, 20);
        new ParticleBuilder(Particle.REVERSE_PORTAL)
            .count(10)
            .offset(1, 1, 1)
            .location(mob.getLocation())
            .extra(.3)
            .spawn()
        ;
    }

    protected final GetNear<Player> getNearestPlayer = new GetEntityNear<>(DynamicLocation.from(mob), Player.class)
        .range(4D)
        .amount(1)
        .operation(GetNear.Operation.NEAREST)
        ;
    public void rotate(){
        Player at = getNearestPlayer.findFirst();
        if(at==null) return;

        Location loc = CruxLoc.lookAt(mob.getEyeLocation(), at.getEyeLocation());
        mob.lookAt(at);
        mob.setBodyYaw(loc.getYaw());
    }

    @Override
    public void tick() {
        rotate();
        particleTick();

        if(world == null){
            attemptGetWorld();
            if(world == null) return;
        }

        for (Entity e : mob.getWorld().getNearbyEntities(mob.getBoundingBox(), e -> e instanceof Player)) {
            Player p = (Player) e;
            EntityTravelThroughRiftEvent event = new EntityTravelThroughRiftEvent(e, this, buildRandomTP(p));
            if(!event.callEvent()){
                continue;
            }

            if(event.isRemovePortal() && mob.isValid()){
                mob.damage(999999D, DamageSource.builder(DamageType.GENERIC_KILL).build());
                CreateSound.sound(Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.2f).playAt(mob.getLocation());
            }
            if(event.isCancelTeleport()) continue;
            RandomWorldTP to = event.getTo();
            to.randomlyTeleportAsync(p).whenComplete((spawn, throwable) ->{
                if(throwable != null) Crux.log(Level.WARNING, throwable.getMessage());
                if(spawn==null) return;
                Crux.scheduler().runTask(() ->{
                    Entity returnPortal = AbyssMob.RETURN_PORTAL.spawn(spawn, mob.getLocation());
                    SuccessfulEntityTravelThroughRiftEvent successEvent = new SuccessfulEntityTravelThroughRiftEvent(p, to, returnPortal);
                    successEvent.callEvent();
                });
            });
        }
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getParticle() {
        return particle;
    }

    public void setParticle(int particle) {
        this.particle = particle;
    }
}

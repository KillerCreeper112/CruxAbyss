package killercreepr.cruxabyss.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.event.CruxEntityDamageEvent;
import killercreepr.crux.util.CruxLoc;
import killercreepr.crux.util.CruxMath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class GroundDwellerGoal extends CruxMobModeledGoal {
    public GroundDwellerGoal(@NotNull Mob mob) {
        super(mob);

        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_HOGLIN_AMBIENT, 1.6f);
            }

            @Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_HOGLIN_ATTACK, 1.6f);
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_HOGLIN_HURT, 1.6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_HOGLIN_DEATH, 2f);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void entityDamage(CruxEntityDamageEvent event){
        if(mob.equals(event.getDamager())){
            playAnimation("attack", true);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event){
        if(mob.equals(event.getEntity())){
            switch (event.getCause()){
                case SUFFOCATION, FALLING_BLOCK -> event.setCancelled(true);
            }
        }
    }

    private int noTargetTicks = 0;
    private boolean goUnderGround = false;
    private Location targetLoc;

    private final double moveDistance = .35D;
    private final CreateSound digSound = CreateSound.sound(Sound.BLOCK_SAND_STEP, 1.6f);

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target){
        if(CruxMob.isInCategory(target, MobCategory.MONSTER)) return false;
        if(goUnderGround) return isValidTarget(target);
        return isValidTarget(target) && hasLineOfSight(target);
    }

    @Override
    public void tick() {
        if(target == null){
            if(goUnderGround){
                if(targetLoc != null) targetLoc = null;
                Block current = mob.getLocation().getBlock();
                if(current.isSolid() && current.getRelative(BlockFace.UP).isSolid() && current.getRelative(BlockFace.UP).getRelative(BlockFace.UP).isSolid()){
                    if(findTargetCooldown > 0){
                        findTargetCooldown--;
                        return;
                    }
                    if(!findAndSetTarget(targetCheck)){
                        findTargetCooldown = 40;
                        target = null;
                        mob.setTarget(null);
                    }
                    return;
                }
                Block b = mob.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if(b.isSolid() && b.getRelative(BlockFace.DOWN).isSolid() &&
                        b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isSolid()){
                    Location l = mob.getLocation();
                    l.setPitch(90f);
                    l.subtract(0, moveDistance, 0);
                    mob.teleport(l);
                    digSound.playAt(mob.getLocation());

                    new ParticleBuilder(Particle.BLOCK).count(10)
                        .offset(.5, .5, .5)
                        .extra(.1)
                        .location(mob.getLocation())
                        .data(b.getBlockData())
                        .spawn();
                }
                return;
            }
            if(noTargetTicks++ > 200){
                noTargetTicks = 0;
                goUnderGround = true;
                return;
            }
        }
        if(goUnderGround){
            if(targetLoc == null){
                for(Block b : CruxLoc.getNearbyBlocks(target.getLocation().getBlock(), 5)){
                    if((b.getRelative(BlockFace.UP).isPassable() || b.getRelative(BlockFace.UP).isEmpty()) &&
                            target.getLocation().distanceSquared(b.getLocation().toCenterLocation()) > (3*3)){
                        targetLoc = b.getLocation().toCenterLocation().add(0, .5, 0);
                    }
                }
            }
            Block current = mob.getLocation().getBlock();
            if(current.isSolid()){
                digSound.playAt(mob.getLocation());
                new ParticleBuilder(Particle.BLOCK).count(10)
                    .offset(.5, .5, .5)
                    .extra(.1)
                    .location(mob.getLocation())
                    .data(current.getBlockData())
                    .spawn();

                Block surface = mob.getLocation().add(0, .3, 0).getBlock();
                if(surface.isEmpty() || surface.isPassable()){
                    goUnderGround = false;
                    Vector vel = target.getEyeLocation().toVector().subtract(mob.getLocation().toVector()).normalize();
                    vel.setY(CruxMath.random(.5D, .6D));
                    mob.setVelocity(vel);
                    targetLoc = null;
                    mob.getWorld().playSound(mob.getLocation(), Sound.ENTITY_HOGLIN_ANGRY, 1f, 1.6f);
                }else{
                    Location l = CruxLoc.lookAt(mob.getLocation(), target.getEyeLocation());
                    mob.teleport(CruxLoc.shiftToward(l, targetLoc, moveDistance));
                    return;
                }
            }else{
                goUnderGround = false;
                targetLoc = null;
            }
        }
        super.tick();
    }


}

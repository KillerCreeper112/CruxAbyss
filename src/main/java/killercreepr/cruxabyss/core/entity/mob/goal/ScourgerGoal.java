package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxentities.api.entity.mob.goal.LocationTargetMobGoal;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ScourgerGoal extends CruxMobModeledGoal implements Listener, LocationTargetMobGoal {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    public ScourgerGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_AMBIENT, .6f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_DEATH, .6f);
            }
        });
    }
    protected Location locationTarget;

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if(!event.getEntity().equals(mob)) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(mob)) return;
        switch (event.getCause()){
            case FIRE, FIRE_TICK, HOT_FLOOR, CAMPFIRE ->{
                event.setCancelled(true);
            }
        }
    }

    private final Spell[] spells = new Spell[]{
        new Spell("prepare_shoot_spell_1", 12/2, 17/2){
            @Override
            public void shootSpell(Mob mob) {
                CreateSound.sound(Sound.ENTITY_EVOKER_CAST_SPELL, 1.3f).playAt(mob);
                AbyssMob.SCOURGER_BULLET.spawn(mob.getEyeLocation(), e ->{
                    if(e instanceof Projectile proj){
                        proj.setShooter(mob);
                    }
                    Vector vel = mob.getEyeLocation().getDirection().multiply(1.5f);
                    e.setVelocity(vel);
                });
            }

            @Override
            public void startSpell(Mob mob) {
                CreateSound.sound(Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.4f).playAt(mob);
            }
        },
        new Spell("prepare_shoot_spell_2", 30/2, 35/2){
            @Override
            public void shootSpell(Mob mob) {
                CreateSound.sound(Sound.ENTITY_EVOKER_CAST_SPELL, 1.3f).playAt(mob);
                AbyssMob.SCOURGER_BULLET_LARGE.spawn(mob.getEyeLocation(), e ->{
                    if(e instanceof Projectile proj){
                        proj.setShooter(mob);
                    }
                    Vector vel = mob.getEyeLocation().getDirection().multiply(1.5f);
                    e.setVelocity(vel);
                });
            }

            @Override
            public void startSpell(Mob mob) {
                CreateSound.sound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.2f).playAt(mob);
            }
        }
    };

    @Override
    protected boolean findAndSetTarget(@Nullable Predicate<Entity> targetCheck) {
        if(locationTarget != null){
            return false;
        }
        return super.findAndSetTarget(targetCheck);
    }

    protected int currentPrepareSpell = -1;

    protected long lastShotSpell;
    protected int spellCooldown;
    protected int preparingSpellTick = 0;
    @Override
    public void tick() {
        swimmer.tick();
        super.tick();
        if(locationTarget != null && target == null){
            moveTo(locationTarget, 1.1D);
        }
        if(mob.getTarget() == null) return;
        if(currentPrepareSpell == -1){
            noPrepareSpellTick();
            return;
        }
        prepareSpellTick();
    }

    @Override
    public double getFindTargetRange() {
        return getFollowDistance();
    }

    private Spell spell(){
        return spells[currentPrepareSpell];
    }

    public void prepareSpellTick(){
        Spell spell = spell();
        if(isPlayingAnimation(spell.id)){
            preparingSpellTick++;
            if(preparingSpellTick == spell.shootTime){
                LivingEntity target = mob.getTarget();
                if(target != null) mob.lookAt(target);
                spell.shootSpell(mob);
            }
            return;
        }
        //spell has ended
        if(preparingSpellTick >= (getAnimationLengthTicks(spell.id)/2)){// divide by 2 because mob goals get ticked every 2 ticks.
            currentPrepareSpell = -1;
            preparingSpellTick = 0;
            lastShotSpell = System.currentTimeMillis();
            spellCooldown = CruxMath.random(40, 80);
            return;
        }
        preparingSpellTick = 0;
        playAnimation(spell.id, true);
        spell.startSpell(mob);
    }

    public void noPrepareSpellTick(){
        if(CruxMath.hasOccurredWithin(lastShotSpell, spellCooldown)){
            return;
        }
        currentPrepareSpell = CruxMath.random(0, spells.length-1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getDamager().equals(mob)) return;
        String attackID = "attack_" + CruxMath.random(1, 3);
        playAnimation(attackID, true);
    }

    @Override
    public @Nullable Location getTargetLocation() {
        return locationTarget;
    }

    @Override
    public void setTargetLocation(@Nullable Location location) {
        this.locationTarget = location;
    }

    private static class Spell{
        public final String id;
        public final int shootTime;
        public final int maxTime;

        public Spell(String id, int shootTime, int maxTime) {
            this.id = id;
            this.shootTime = shootTime;
            this.maxTime = maxTime;
        }

        public void shootSpell(Mob mob){

        }
        public void startSpell(Mob mob){

        }
    }
}

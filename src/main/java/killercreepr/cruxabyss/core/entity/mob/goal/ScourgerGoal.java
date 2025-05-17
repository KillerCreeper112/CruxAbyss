package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.entity.mob.goal.OutpostTargeterGoal;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxentities.api.entity.mob.goal.PathTargetMobGoal;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
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

public class ScourgerGoal extends CruxMobModeledGoal implements Listener, PathTargetMobGoal, OutpostTargeterGoal {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final PathTargetMobGoal pathTarget = PathTargetMobGoal.pathTargetMobGoal(this, 1.1D);
    public ScourgerGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_AMBIENT,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, .6f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, .6f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_DEATH,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, .6f);
            }
        });
    }

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
                    Vector vel = mob.getEyeLocation().getDirection().multiply(CruxMath.random(2.5f, 3.8f));
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
                    Vector vel = mob.getEyeLocation().getDirection().multiply(CruxMath.random(2.5f, 3.8f));
                    e.setVelocity(vel);
                });
            }

            @Override
            public void startSpell(Mob mob) {
                CreateSound.sound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.2f).playAt(mob);
            }
        }
    };

    public boolean isWithinTargetedOutpost(){
        if(targetOutpost == null) return false;
        return targetOutpost.getOrDefault(StoredStructureComponents.OUTER_BOX, targetOutpost.getBoundingBox()).contains(mob.getLocation().toVector());
    }

    @Override
    protected boolean findAndSetTarget(@Nullable Predicate<Entity> targetCheck) {
        if(hasPath() && !isWithinTargetedOutpost()){
            return false;
        }
        return super.findAndSetTarget(targetCheck);
    }

    protected int currentPrepareSpell = -1;

    protected long lastShotSpell;
    protected int spellCooldown;
    protected int preparingSpellTick = 0;

    protected GoalPath lastPath;
    @Override
    public @Nullable GoalPath getPath() {
        return pathTarget.getPath();
    }

    @Override
    public void setPath(@Nullable GoalPath goalPath) {
        pathTarget.setPath(goalPath);
        if(goalPath != null) lastPath = goalPath;
    }
    public void structureTick(){
        if(targetOutpost == null || lastPath == null || pathTarget.hasPath()) return;
        if(isWithinTargetedOutpost()) return;
        setPath(GoalPath.goalPath(lastPath.getNodes()));
    }

    public void mountTick(){
        if(mob.getVehicle() != null){
            playAnimation("mounted", false);
        }else if(isPlayingAnimation("mounted")){
            stopAnimation("mounted");
        }
    }

    @Override
    public void tick() {
        swimmer.tick();
        super.tick();
        mountTick();
        structureTick();
        if(target == null) pathTarget.tick();
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

    protected StoredStructure targetOutpost;
    @Override
    public StoredStructure getOutpostTarget() {
        return targetOutpost;
    }

    @Override
    public void setOutpostTarget(StoredStructure structure) {
        this.targetOutpost = structure;
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

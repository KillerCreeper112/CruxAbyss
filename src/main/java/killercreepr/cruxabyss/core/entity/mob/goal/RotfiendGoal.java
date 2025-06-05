package killercreepr.cruxabyss.core.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.event.CruxEntityDamageEvent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxabyss.core.entity.memory.RotfiendOozeHolder;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.sound.CruxGoalSounds;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttack;
import killercreepr.usurvive.core.entity.mob.goals.data.MobAttackHandler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RotfiendGoal extends CruxMobModeledGoal implements Listener {
    protected final SwimmerGoal swimmer = new SwimmerGoal(this);
    protected final MobAttackHandler attackHandler;
    public RotfiendGoal(@NotNull Mob mob) {
        super(mob);
        sounds(new CruxGoalSounds(mob) {
            @Override
            public @NotNull CreateSound ambient() {
                return CreateSound.sound(Sound.ENTITY_DROWNED_AMBIENT,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }

            /*@Override
            public @NotNull CreateSound attack() {
                return CreateSound.sound(Sound.ENTITY_PILLAGER_HURT, 1.85f);
            }*/

            @Override
            public int ambientMin() {
                return 100;
            }

            @Override
            public int ambientMax() {
                return 160;
            }

            @Override
            public @NotNull CreateSound hurt() {
                return CreateSound.sound(Sound.ENTITY_DROWNED_HURT_WATER,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }

            @Override
            public @NotNull CreateSound death() {
                return CreateSound.sound(Sound.ENTITY_DROWNED_DEATH_WATER,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.5f);
            }
        });

        attackHandler = new MobAttackHandler(mob, this, List.of(
            /*new StrongMobAttack(1) {
                @Override
                public void onUse() {
                    mob.getAttribute(Attribute.MOVEMENT_SPEED).addTransientModifier(
                        new AttributeModifier(STRONG_ATTACK_KEY, -5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
                    );
                    *//*CruxAttribute.addModifier(mob, CruxAttribute.MOVEMENT_SPEED,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, -5D, CruxAttribute.Operation.MULTIPLY));*//*

                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_DAMAGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_AOE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .2D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .1D, CruxAttribute.Operation.MULTIPLY));
                    CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_KNOCKBACK,
                        CruxAttributeModifier.modifier(STRONG_ATTACK_KEY, .4D, CruxAttribute.Operation.MULTIPLY));
                }

                @Override
                public int getHitTime() {
                    return 9;
                }
            }*/
        ), List.of(
            new MobAttack() {
                @Override
                public String getAnimationID() {
                    return "ooze";
                }

                @Override
                public void onUse() {
                    MobAttack.super.onUse();
                    mob.getAttribute(Attribute.MOVEMENT_SPEED).addTransientModifier(
                        new AttributeModifier(MobAttackHandler.STRONG_ATTACK_KEY, -0.7D, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
                    );
                }

                @Override
                public void onTick() {
                    MobAttack.super.onTick();
                    if(attackHandler.getAttackTime() == 16){ //31 / 2 = 16 rounded up
                        ooze();
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    double distance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 2;
                    return getSquaredDistanceFromTarget() < (distance*distance);
                }
            },
            new MobAttack() {
                @Override
                public String getAnimationID() {
                    return "ooze_shoot";
                }

                @Override
                public void onUse() {
                    MobAttack.super.onUse();
                    mob.getAttribute(Attribute.MOVEMENT_SPEED).addTransientModifier(
                        new AttributeModifier(MobAttackHandler.STRONG_ATTACK_KEY, -5D, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
                    );
                }

                @Override
                public void onTick() {
                    MobAttack.super.onTick();
                    if(attackHandler.getAttackTime() == 15){ //30 / 2 = 15
                        if(target == null) return;
                        oozeShoot(target.getLocation());
                    }
                }

                @Override
                public int getHitTime() {
                    return 0;
                }

                @Override
                public boolean canUseAttack() {
                    if(target == null) return false;
                    double distance = CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 2;
                    return getSquaredDistanceFromTarget() > (distance*distance);
                }
            }
        ));
    }

    public void oozeShoot(Location target){
        var itemHolder = Crux.handlers().item().getItem(Crux.key("moldering_spore"));
        ItemStack item;
        if(itemHolder == null) item = new ItemStack(Material.SLIME_BALL);
        else item = itemHolder.value();

        Location spawn = getOozePos();
        mob.getWorld().spawn(spawn, Snowball.class, e ->{
            e.setItem(item);
            Vector dir = CruxMath.parabolicMotion(spawn.toVector(), target.toVector(), 4, 0.115D);
            e.setVelocity(dir);
            EntityMemory.getOrCreateDataHolder(e, RotfiendOozeHolder.class);
        });
    }

    public void ooze(){
        mob.getWorld().spawn(mob.getLocation(), AreaEffectCloud.class, e ->{
            e.setRadius(e.getRadius()*CruxMath.random(1.3f, 1.5f));
            e.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 300, 0), true);
        });

        new GetEntityNear<>(LivingEntity.class)
            .center(mob)
            .filter(this::isValidNaturalTarget)
            .range(CruxAttribute.get(mob, CruxAttribute.ATTACK_RANGE) * 2)
            .find().forEach(this::attack);

        oozeSound().playAt(mob);
        Location oozePos = getOozePos();
        int amount = CruxMath.random(6, 10);

        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .location(oozePos)
            .colorTransition(Color.fromRGB(0xC0FF00), Color.fromRGB(0x7F670E))
            .offset(0.5, 0.5, 0.5)
            .extra(.5)
            .spawn();

        for(int i = 0; i < amount; i++){
            new ParticleBuilder(Particle.SPLASH)
                .location(oozePos)
                .offset(
                    CruxMath.random(-1, 1),
                    CruxMath.random(0.5, 1),
                    CruxMath.random(-1, 1)
                )
                .extra(.3)
                .spawn();
        }
    }

    public CreateSound prepareShootSound(){
        return CreateSound.sound(Sound.ENTITY_SLIME_SQUISH,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.4f);
    }
    public CreateSound shootSound(){
        return CreateSound.sound(Sound.ENTITY_SLIME_ATTACK,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.4f);
    }
    //todo ooze sounds
    public CreateSound prepareOozeSound(){
        return CreateSound.sound(Sound.ENTITY_SLIME_SQUISH,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.4f);
    }
    public CreateSound oozeSound(){
        return CreateSound.sound(Sound.ENTITY_SLIME_ATTACK,  net.kyori.adventure.sound.Sound.Source.HOSTILE,0.4f, 1.4f);
    }

    @Override
    public boolean preAttemptAttack() {
        if(attackHandler.preAttemptAttack()){
            return super.preAttemptAttack();
        }
        return false;
    }

    @Override
    public void attacked(@NotNull CruxEntityDamageEvent event) {
        super.attacked(event);
        if(attackHandler.isUsingStrongAttack()) return;
        String id = generateAttackAnimationID();
        if(id == null) return;
        playAnimation(id, true);
    }


    public String generateAttackAnimationID(){
        return "attack_" + CruxMath.random(1,1);
    }

    public Location getOozePos(){
        return getModel().getBone("ooze_pos").get().getLocation();
    }

    @Override
    public boolean isValidNaturalTarget(@NotNull LivingEntity target) {
        return !CruxMob.isInCategory(target, MobCategory.ENEMY) && super.isValidNaturalTarget(target);
    }

    public void movementTick(){
        double moveSpeed = CruxAttribute.get(mob, CruxAttribute.MOVEMENT_SPEED);
        if(moveSpeed > 0D){
            swimmer.tick();
        }
    }

    @Override
    public void tick() {
        super.tick();
        attackHandler.tick();
        movementTick();
    }
}

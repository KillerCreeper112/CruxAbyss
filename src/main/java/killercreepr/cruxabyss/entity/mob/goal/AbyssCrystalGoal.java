package killercreepr.cruxabyss.entity.mob.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.Crux;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbyssCrystalGoal extends CruxMobModeledGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("abyss_crystal"));
    public AbyssCrystalGoal(@NotNull Mob mob, ActiveModel model) {
        super(KEY, mob, model);
    }

    public AbyssCrystalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob, ActiveModel model) {
        super(key, mob, model);
    }

    public void setItem(@NotNull ItemStack item){
        model.getBone("base").orElseThrow().setModel(item);
    }

    protected int tick = 0;
    protected final int lifeSpan = 200;
    protected double speed = .7D;
    protected final double speedIncrease = .1;

    protected boolean explode = false;
    @Override
    public void tick() {
        playAnimation("size", false);
        if(explode){
            if(isPlayingAnimation("portal_spawn")) return;
            mob.remove();
            new CreateSound(Sound.ENTITY_ITEM_BREAK, 1.2f).playAt(mob.getLocation());
            new CreateSound(Sound.BLOCK_GLASS_BREAK, 1.1f).playAt(mob.getLocation());
            new CreateSound(Sound.ENTITY_GENERIC_EXPLODE, 2f).playAt(mob.getLocation());
            new ParticleBuilder(Particle.ITEM)
                .location(mob.getLocation())
                .count(20)
                .offset(.1, .1, .1)
                .extra(1.5)
                .data(model.getBone("base").orElseThrow().getModel())
                .spawn()
            ;
            return;
        }
        setSpinSpeed(speed);
        playAnimation("float", false);

        speed += speedIncrease;
        tick++;
        if(tick >= lifeSpan){
            explode = true;
            stopAnimation("spin");
            playAnimation("portal_spawn", true);
        }
    }

    public void setSpinSpeed(double speed){
        IAnimationProperty property = model.getAnimationHandler().getAnimation("spin");
        if(property == null){
            property = model.getAnimationHandler().playAnimation(
                "spin", 0D, 0D, speed, true
            );
            return;
        }
        property.setSpeed(speed);
    }
}

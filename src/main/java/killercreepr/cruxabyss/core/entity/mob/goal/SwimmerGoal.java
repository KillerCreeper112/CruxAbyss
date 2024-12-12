package killercreepr.cruxabyss.core.entity.mob.goal;

import killercreepr.cruxentities.entity.mob.goal.CruxGoalBase;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

public class SwimmerGoal {
    protected final CruxGoalBase goal;
    public SwimmerGoal(CruxGoalBase goal) {
        this.goal = goal;
    }

    public void tick(){
        swimTick();
    }

    public double swimSpeed(){
        return .1D;
    }

    public boolean isSwimming(){
        return swimmingTicks > 0;
    }

    public CruxGoalBase getGoal() {
        return goal;
    }

    public int getComingBackForAirTicks() {
        return comingBackForAirTicks;
    }

    public void setComingBackForAirTicks(int comingBackForAirTicks) {
        this.comingBackForAirTicks = comingBackForAirTicks;
    }

    public int getSwimmingTicks() {
        return swimmingTicks;
    }

    public void setSwimmingTicks(int swimmingTicks) {
        this.swimmingTicks = swimmingTicks;
    }

    protected int comingBackForAirTicks;
    protected int swimmingTicks = 0;
    public void swimTick(){
        Mob mob = goal.getMob();
        if(comingBackForAirTicks > 0){
            swimmingTicks = 0;
            comingBackForAirTicks++;
            if(mob.getRemainingAir() >= mob.getMaximumAir()){
                comingBackForAirTicks = 0;
                return;
            }
            return;
        }
        LivingEntity target = goal.getTarget();
        if(target == null || !mob.isInWater()){
            swimmingTicks = 0;
            return;
        }
        if(mob.getRemainingAir() <= (mob.getMaximumAir() * .25f)){
            comingBackForAirTicks = 1;
            return;
        }
        Vector dir = target.getLocation().toVector().subtract(mob.getLocation().toVector()).normalize();
        dir.multiply(swimSpeed());
        mob.setVelocity(mob.getVelocity().add(dir));
        swimmingTicks++;
    }
}

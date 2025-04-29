package killercreepr.cruxabyss.core.entity.mob.goal.data;

public abstract class StrongMobAttack implements MobAttack{
    protected final int id;

    public StrongMobAttack(int id) {
        this.id = id;
    }

    @Override
    public String getAnimationID() {
        return "attack_strong_" + id;
    }
}

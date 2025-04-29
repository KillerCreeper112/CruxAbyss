package killercreepr.cruxabyss.core.entity.mob.goal.data;

public interface MobAttack {
    String getAnimationID();
    void onUse();
    int getHitTime();
    default void onFinish(){}
    default void onTick(){}
}

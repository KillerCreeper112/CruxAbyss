package killercreepr.cruxabyss.core.entity.mob.goal.data;

public interface MobAttack {
    String getAnimationID();
    default void onUse(){};
    int getHitTime();
    default void onFinish(){}
    default void onTick(){}
    default int getCooldown(){
        return -1;
    }

    default boolean canUseAttack(){
        return true;
    }
}

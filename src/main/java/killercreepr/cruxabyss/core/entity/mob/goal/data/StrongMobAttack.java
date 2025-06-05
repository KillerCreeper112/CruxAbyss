package killercreepr.cruxabyss.core.entity.mob.goal.data;

import killercreepr.crux.core.Crux;
import org.bukkit.NamespacedKey;

public abstract class StrongMobAttack implements MobAttack{
    public static final NamespacedKey STRONG_ATTACK_KEY = Crux.key("strong_attack");
    protected final int id;

    public StrongMobAttack(int id) {
        this.id = id;
    }

    @Override
    public String getAnimationID() {
        return "attack_strong_" + id;
    }
}

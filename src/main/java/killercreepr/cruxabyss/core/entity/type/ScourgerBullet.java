package killercreepr.cruxabyss.core.entity.type;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.entity.memory.ScourgerBulletData;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ScourgerBullet extends SimpleCruxMob {
    public ScourgerBullet(@NotNull Key key) {
        super(key);
    }

    public ScourgerBullet() {
        super(Crux.key("scourger_bullet"));
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return location.getWorld().spawn(location, Arrow.class, e ->{
            e.setSilent(true);
            CruxTag.set(e, "ignore_abyssal_mobs", PersistentDataType.INTEGER, 1);
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(CruxMath.random(10, 15)));
            CruxAttribute.addModifier(e, CruxAttribute.ATTACK_DAMAGE, CruxAttributeModifier.baseModifier(CruxMath.random(7, 9)));
            load(e);
            if(consumer != null) consumer.accept(e);
        });
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        ModelEntity modelEntity = new ModelEntity(e);
        modelEntity.setBaseEntityVisible(false).getOrAddModelAsync(key.value()).whenComplete((model, throwable) ->{
            model.getAnimationHandler().playAnimation("spin", 0D, 0D, 1D, true);
        });
        //modelEntity.playAnimation("spin", true);
        EntityMemory.getOrCreateDataHolder(e, ScourgerBulletData.class, mem -> new ScourgerBulletData(mem));
    }
}

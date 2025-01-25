package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.CharredBonesGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AbyssCharredBones extends SimpleAbyssMob {
    public AbyssCharredBones() {
        super(Crux.key("charred_bones"), EntityType.SKELETON);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Charred Bones"));
            e.setCustomNameVisible(false);
            new ModelEntity(e, key.value()).getOrCreateModeledEntity().setBaseEntityVisible(false);

            if(e instanceof Skeleton x) x.setShouldBurnInDay(false);
            if(e instanceof LivingEntity d){
                EntityEquipment equipment = d.getEquipment();
                if(equipment != null) equipment.setItemInMainHand(null);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = super.getAttributes(world, e);
        setAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(CruxMath.random(8D, 14D) *
                        (world == null ? 1D : world.getDifficulty())));
        setAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-15));
        setAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(36));
        setAttribute(map, CruxAttribute.ATTACK_KNOCKBACK_UP, CruxAttributeModifier.baseModifier(12));
        setAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, CruxAttributeModifier.baseModifier(6));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new DesignEntity(e)
            .setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        return new CharredBonesGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY, MobCategory.UNDEAD};
    }
}

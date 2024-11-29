package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.PlagueStalkerGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlagueStalker extends SimpleAbyssMob {
    public PlagueStalker() {
        super(Crux.key("plague_stalker"), EntityType.WOLF);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Plague Stalker"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            new ModelEntity(e, key.value()).getOrCreateModeledEntity().setBaseEntityVisible(false);
            if(e instanceof Mob mob){
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40D);
                mob.setHealth(40D);
                mob.getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(1.2D);
                CruxAttribute.addModifier(
                    e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(
                        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()
                    )
                );
                /*mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(.85);
                mob.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(0);
                mob.setSilent(true);*/
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(CruxMath.random(5D, 7D) *
                        (world == null ? 1D : world.getWave() * .1D) * (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-12));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(2.6D));
        return map;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        //stop wolf from attacking skeletons
        Bukkit.getMobGoals().getAllGoals(mob).forEach(mobGoal -> {
            if(!mobGoal.getKey().getNamespacedKey().equals(NamespacedKey.minecraft("nearest_attackable"))) return;
            Bukkit.getMobGoals().removeGoal(mob, mobGoal.getKey());
        });
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new DesignEntity(e).getOrAddModelAsync(key.value());
        return new PlagueStalkerGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY};
    }
}

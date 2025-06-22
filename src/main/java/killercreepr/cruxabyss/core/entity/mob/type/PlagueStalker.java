package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
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
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlagueStalker extends SimpleAbyssMob implements Listener {
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
                mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(CruxMath.random(40D, 50D));
                mob.setHealth(mob.getAttribute(Attribute.MAX_HEALTH).getValue());
                mob.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(1.2D);
                CruxAttribute.addModifier(
                    e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(
                        mob.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue()
                    )
                );
                /*mob.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(.85);
                mob.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0);
                mob.setSilent(true);*/
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(7D *
                        (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-14));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11));
        setAttribute(map, CruxAttribute.SHIELD_PIERCING, CruxAttributeModifier.baseModifier(30D));
        return map;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        //stop wolf from attacking skeletons
        Bukkit.getMobGoals().getAllGoals(mob).forEach(mobGoal -> {
            switch (mobGoal.getKey().getNamespacedKey().asString()){
                case "minecraft:nearest_attackable", "minecraft:melee_attack" ->{
                    Bukkit.getMobGoals().removeGoal(mob, mobGoal.getKey());
                }
            }
        });
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);

        if(!CruxAttribute.hasAttributeData(mob, CruxAttribute.ATTACK_RANGE)){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
                mob.getWidth() + .95D
            ));
        }
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new DesignEntity(e)
            .setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(e, active);
        return new PlagueStalkerGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY, AbyssMobCategory.ABYSSAL};
    }

}

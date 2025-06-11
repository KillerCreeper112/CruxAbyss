package killercreepr.cruxabyss.core.entity.mob.type;

import com.destroystokyo.paper.entity.ai.Goal;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.VoidDwellerGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VoidDweller extends SimpleAbyssMob {

    public VoidDweller() {
        super(Crux.key("void_dweller"), EntityType.VEX);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Void Dweller"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            if(e instanceof Zombie z){
                z.setShouldBurnInDay(false);
            }
            /*if(e instanceof Mob mob){
                mob.setAware(false);
            }*/

            if(e instanceof LivingEntity ee){
                double movement = ee.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.3D;
                ee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(movement);
                ee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(CruxMath.random(25D, 35D));
                ee.setHealth(ee.getAttribute(Attribute.MAX_HEALTH).getValue());
                CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED, CruxAttributeModifier.baseModifier(movement));
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(5.7D * (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.3D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-15));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(9));
        return map;
    }

    @Override
    public void onModelApplied(Mob mob) {
        super.onModelApplied(mob);

        if(!CruxAttribute.hasAttributeData(mob, CruxAttribute.ATTACK_RANGE)){
            CruxAttribute.addModifier(mob, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(
                mob.getWidth() + .7D
            ));
        }
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
        /*if(Bukkit.getMobGoals().getGoal(mob, VoidDwellerGoal.defaultKey()) instanceof VoidDwellerGoal g){
            EntityMemory.getOrCreate(e, mem -> new VoidDwellerMemory(e, g));
        }*/
        for (Goal<Mob> goal : Crux.getServer().getMobGoals().getAllGoals(mob)) {
            switch (goal.getKey().getNamespacedKey().asString()){
                case "minecraft:nearest_attackable", "minecraft:hurt_by" ->{
                    Crux.getServer().getMobGoals().removeGoal(mob, goal);
                }
            }
        }
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new ModelEntity(e).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        applyWhenCompleteModel(e, active);
        return new VoidDwellerGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY};
    }
}

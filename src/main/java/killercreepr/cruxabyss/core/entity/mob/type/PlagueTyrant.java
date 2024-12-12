package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.PlagueTyrantGoal;
import killercreepr.cruxabyss.core.entity.mob.goal.ScourgerGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PlagueTyrant extends SimpleAbyssMob {
    public PlagueTyrant() {
        super(Crux.key("plague_tyrant"), EntityType.PIG);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Plague Tyrant"));
            e.setCustomNameVisible(false);
            e.setSilent(true);

            if(e instanceof LivingEntity ee){
                ee.getAttribute(Attribute.STEP_HEIGHT).setBaseValue(2.5D);
                ee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100D);
                ee.setHealth(ee.getAttribute(Attribute.MAX_HEALTH).getValue());

                CruxAttribute.addModifier(e, CruxAttribute.MOVEMENT_SPEED,
                    CruxAttributeModifier.baseModifier(ee.getAttribute(Attribute.MOVEMENT_SPEED).getValue()*1.2D));
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(CruxMath.random(7D, 9D) *
                        (world == null ? 1D : world.getWave() * .1D) * (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-12));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(20));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(2.6D));
        return map;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new ModelEntity(e).setBaseEntityVisible(false).getOrAddModelAsync(key.value());
        return new PlagueTyrantGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY, AbyssMobCategory.ABYSSAL, AbyssMobCategory.ABYSS_OUTPOST};
    }
}

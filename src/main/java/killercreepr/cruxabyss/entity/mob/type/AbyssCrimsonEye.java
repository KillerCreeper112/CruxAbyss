package killercreepr.cruxabyss.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.Crux;
import killercreepr.crux.data.Holder;
import killercreepr.crux.loot.LootContext;
import killercreepr.crux.loot.LootTable;
import killercreepr.crux.loot.impl.SimpleLootContext;
import killercreepr.crux.loot.impl.item.SimpleItemLootPool;
import killercreepr.crux.loot.impl.item.SimpleItemLootTable;
import killercreepr.crux.loot.impl.item.functions.ItemEnchantFunction;
import killercreepr.crux.loot.impl.item.pool.SimpleItemLootPoolObject;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxTag;
import killercreepr.crux.valueproviders.number.ConstantNumber;
import killercreepr.cruxabyss.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.entity.mob.goal.CrimsonEyeGoal;
import killercreepr.cruxabyss.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.DesignEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class AbyssCrimsonEye extends SimpleAbyssMob {
    public AbyssCrimsonEye() {
        super(Crux.key("crimson_eye"), EntityType.HUSK);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            CruxTag.set(e, "hide", PersistentDataType.INTEGER, 1);
            new ModelEntity(e, key.value()).getOrCreateModeledEntity().setBaseEntityVisible(false);
            if(e instanceof Mob mob){
                mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(-999D);
                mob.setSilent(true);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                new CruxAttributeModifier(CruxMath.random(4D, 6D) *
                        (world == null ? 1D : world.getWave() * .1D) * (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, new CruxAttributeModifier(.35D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, new CruxAttributeModifier(-5));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, new CruxAttributeModifier(20));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, new CruxAttributeModifier(2.2D));
        addAttribute(map, CruxAttribute.KNOCKBACK_RESISTANCE, new CruxAttributeModifier(9999));
        return map;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        ActiveModel active = new DesignEntity(e).getModel(key.value()).orElse(null);
        if(active != null) return new CrimsonEyeGoal(e, active);
        return null;
    }


    @Override
    public void onDeath(@NotNull Entity e, @NotNull EntityDeathEvent event) {
        super.onDeath(e, event);
        event.getDrops().clear();
        LootTable<ItemStack> lootTable = new SimpleItemLootTable(
            Crux.key("test"), new ConstantNumber(1),
            List.of(
                new SimpleItemLootPool(
                    10, 0f,
                    null,
                    List.of(
                        new ItemEnchantFunction(new ConstantNumber(3), Set.of(
                            new ItemEnchantFunction.Enchant(0, 0f, Key.key("protection"),
                                new ConstantNumber(1))
                        ))
                    ),
                    new ConstantNumber(1),
                    List.of(
                        new SimpleItemLootPoolObject(10, 0f, Holder.empty())
                    )
                )
            )
        );
        event.getDrops().addAll(
            lootTable.populateLoot(LootContext.builder().build())
        );
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER};
    }
}

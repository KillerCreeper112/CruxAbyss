package killercreepr.cruxabyss.core.entity.mob.type;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMobCategory;
import killercreepr.cruxabyss.core.entity.mob.SimpleAbyssMob;
import killercreepr.cruxabyss.core.entity.mob.goal.ToxicatorGoal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.MobCategory;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Toxicator extends SimpleAbyssMob {
    public Toxicator() {
        super(Crux.key("toxicator"), EntityType.VINDICATOR);
    }

    @Override
    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l) {
        return e ->{
            e.customName(Component.text("Toxicator"));
            e.setCustomNameVisible(false);
            e.setSilent(true);
            if(e instanceof Mob mob){
                LootTable<ItemStack> helmetLootTable = CruxRegistries.ITEM_LOOT_TABLE.get(Crux.key("entity/toxicator/helmets"));
                if(helmetLootTable != null){
                    List<ItemStack> items = helmetLootTable.populateLoot(LootContext.builder().looted(e).build());
                    if(!items.isEmpty()) mob.getEquipment().setHelmet(items.getFirst());
                }
                mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(CruxMath.random(36D, 64D));
                mob.setHealth(mob.getAttribute(Attribute.MAX_HEALTH).getValue());
                mob.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(
                    7.5D * (world == null ? 1D : world.getDifficulty())
                );
                mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mob.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue()*1.1);
                mob.getEquipment().setItemInMainHand(null);
            }
        };
    }

    @Override
    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e) {
        /*Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        addAttribute(map, CruxAttribute.ATTACK_DAMAGE,
                CruxAttributeModifier.baseModifier(CruxMath.random(5D, 7D) *
                        (world == null ? 1D : world.getDifficulty())));
        addAttribute(map, CruxAttribute.ATTACK_AOE, CruxAttributeModifier.baseModifier(.4D));
        addAttribute(map, CruxAttribute.ATTACK_SPEED, CruxAttributeModifier.baseModifier(-12));
        addAttribute(map, CruxAttribute.ATTACK_KNOCKBACK, CruxAttributeModifier.baseModifier(11));
        addAttribute(map, CruxAttribute.ATTACK_RANGE, CruxAttributeModifier.baseModifier(2.6D));
        return map;*/
        return Map.of();
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob mob)) return;
    }

    @Override
    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        CompletableFuture<ActiveModel> active = new ModelEntity(e).setBaseEntityVisible(false).getOrAddModelAsync(key.value())
            .whenComplete((model, throwable) ->{
                if(throwable != null) Crux.log(Level.SEVERE, throwable.getMessage());
                model.getAnimationHandler().playAnimation("helmet_size", 0D, 0D, 1D, true);
                model.getBone("helmet").orElseThrow().setModel(
                    e.getEquipment().getHelmet()
                );
            });
        return new ToxicatorGoal(e).model(active);
    }

    @Override
    public MobCategory[] getCategories() {
        return new MobCategory[]{MobCategory.MONSTER, MobCategory.ENEMY, AbyssMobCategory.ABYSSAL, AbyssMobCategory.ABYSS_OUTPOST};
    }
}

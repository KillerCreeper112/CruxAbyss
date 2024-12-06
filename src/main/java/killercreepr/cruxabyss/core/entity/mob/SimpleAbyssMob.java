package killercreepr.cruxabyss.core.entity.mob;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxattributes.api.attribute.CruxAttribute;
import killercreepr.cruxattributes.api.attribute.CruxAttributeInstance;
import killercreepr.cruxattributes.api.attribute.CruxAttributeModifier;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.entity.SimpleCruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.persistence.CruxEntitiesPersist;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleAbyssMob extends SimpleCruxMob implements AbyssMob {
    protected final @NotNull EntityType spawnType;
    public SimpleAbyssMob(@NotNull Key key, @NotNull EntityType spawnType) {
        super(key);
        this.spawnType = spawnType;
    }

    public @Nullable Consumer<Entity> spawnFunction(@Nullable AbyssWorld world, @NotNull Location l){ return null; }

    public @NotNull Entity spawn(@Nullable AbyssWorld world, @NotNull Location l){
        return spawnAt(world, l, null);
    }

    public @NotNull Entity spawnAt(@Nullable AbyssWorld world, @NotNull Location l, @Nullable Consumer<Entity> consumer){
        return l.getWorld().spawnEntity(l, spawnType, CreatureSpawnEvent.SpawnReason.NATURAL, e ->{
            CruxEntitiesPersist.ENTITY.set(e, this.key);
            CruxTag.set(e, "level", PersistentDataType.INTEGER, (int) (world == null ? 1 : (world.getWave() * world.getDifficulty())));

            //Equipment
            if(e instanceof LivingEntity lE){
                if(lE.getEquipment() != null){
                    Map<EquipmentSlot, ItemStack> equipment = getEquipment(world, l);
                    if(equipment != null){
                        for(Map.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()){
                            lE.getEquipment().setItem(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(lE.getAttribute(Attribute.FOLLOW_RANGE) != null){
                    lE.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(64D);
                }
            }
            //Atributes
            Map<CruxAttribute, Collection<CruxAttributeModifier>> attributes = getAttributes(world, e);
            if(attributes != null){
                for(Map.Entry<CruxAttribute, Collection<CruxAttributeModifier>> entry : attributes.entrySet()){
                    for(CruxAttributeModifier m : entry.getValue()){
                        CruxAttribute.addModifier(e, entry.getKey(), m);
                    }
                }
            }

            Consumer<Entity> spawnFunction = spawnFunction(world, l);
            if(spawnFunction != null) spawnFunction.accept(e);
            if(consumer != null) consumer.accept(e);

            //Mob goal
            load(e);
        });
    }

    public @Nullable Map<EquipmentSlot, ItemStack> getEquipment(@Nullable AbyssWorld world, @NotNull Location l){
        return null;
    }

    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable AbyssWorld world, @NotNull Entity e){
        int wave = world == null ? 1 : world.getWave();
        float difficulty = world == null ? 1f : world.getDifficulty();
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        Collection<CruxAttributeModifier> list = new HashSet<>();
        list.add(CruxAttributeModifier.baseModifier(CruxMath.random(1D, 7D) * (wave * .1D) * difficulty));
        map.put(CruxAttribute.ATTACK_DAMAGE, list);

        list = new HashSet<>();
        list.add(CruxAttributeModifier.baseModifier(((CruxMath.random(50D, 100D) * (wave * .01D + 1D)) * difficulty)));
        map.put(CruxAttribute.ATTACK_KNOCKBACK, list);

        list = new HashSet<>();
        list.add(CruxAttributeModifier.baseModifier(CruxMath.random(-10, -6)));
        map.put(CruxAttribute.ATTACK_SPEED, list);

        list = new HashSet<>();

        list.add(CruxAttributeModifier.baseModifier(CruxMath.random(0D, .5D)));
        map.put(CruxAttribute.ATTACK_AOE, list);

        list = new HashSet<>();
        list.add(CruxAttributeModifier.baseModifier(e.getBoundingBox().getWidthX() + CruxMath.random(.1D, .5D)));
        map.put(CruxAttribute.ATTACK_RANGE, list);
        return map;
    }

    protected @NotNull Map<CruxAttribute, Collection<CruxAttributeModifier>> setAttribute(@Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> map, @NotNull CruxAttribute attribute, @NotNull CruxAttributeModifier modifier){
        if(map == null) map = new HashMap<>();
        Collection<CruxAttributeModifier> list = map.getOrDefault(attribute, new HashSet<>());
        list.clear();
        list.add(modifier);
        map.put(attribute, list);
        return map;
    }

    protected @NotNull Map<CruxAttribute, Collection<CruxAttributeModifier>> setAttribute(@Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> map, @NotNull CruxAttribute attribute, @NotNull Collection<CruxAttributeModifier> modifiers){
        if(map == null) map = new HashMap<>();
        Collection<CruxAttributeModifier> list = map.getOrDefault(attribute, new HashSet<>());
        list.clear();
        list.addAll(modifiers);
        map.put(attribute, list);
        return map;
    }

    protected @NotNull Map<CruxAttribute, Collection<CruxAttributeModifier>> addAttribute(@Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> map, @NotNull CruxAttribute attribute, @NotNull CruxAttributeModifier modifier){
        if(map == null) map = new HashMap<>();
        Collection<CruxAttributeModifier> list = map.getOrDefault(attribute, new HashSet<>());
        list.removeIf(m -> m.key().equals(modifier.key()));
        list.add(modifier);
        map.put(attribute, list);
        return map;
    }

    protected double getAttributeValue(@Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> map, @NotNull CruxAttribute attribute){
        if(map == null) return 0D;
        Collection<CruxAttributeModifier> list = map.getOrDefault(attribute, new HashSet<>());
        return CruxAttributeInstance.instance(attribute, list).getValue();
    }

    protected double getBaseAttributeValue(@Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> map, @NotNull CruxAttribute attribute){
        if(map == null) return 0D;
        Collection<CruxAttributeModifier> list = map.getOrDefault(attribute, new HashSet<>());
        for(CruxAttributeModifier m : list){
            if(m.isBase()) return m.getAmount();
        }
        return 0D;
    }

    @Override
    public void load(@NotNull Entity e) {
        super.load(e);
        if(!(e instanceof Mob m)) return;
        CruxMobGoal goal = getGoal(m);
        if(goal==null) return;
        if(Crux.getServer().getMobGoals().getGoal(m, goal.getKey()) != null) return;
        Crux.getServer().getMobGoals().addGoal(m, 0, goal);
    }

    public @Nullable CruxMobGoal getGoal(@NotNull Mob e) {
        return null;
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location, @Nullable Consumer<Entity> consumer) {
        return spawnAt(
            CruxCore.inst().worldManager().getWorldOrNull(location.getWorld().getUID(), AbyssWorld.class), location, consumer
        );
    }
}

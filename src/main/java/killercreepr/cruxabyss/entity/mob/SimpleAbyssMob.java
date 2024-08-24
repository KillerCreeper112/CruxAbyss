package killercreepr.cruxabyss.entity.mob;

import killercreepr.crux.Crux;
import killercreepr.crux.util.CruxMath;
import killercreepr.crux.util.CruxTag;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxattributes.attribute.CruxAttribute;
import killercreepr.cruxattributes.attribute.CruxAttributeInstance;
import killercreepr.cruxattributes.attribute.CruxAttributeModifier;
import killercreepr.cruxentities.entity.GenericCruxMob;
import killercreepr.cruxentities.entity.mob.goal.CruxMobGoal;
import killercreepr.cruxentities.persistence.CruxEntitiesPersistTags;
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

public class SimpleAbyssMob extends GenericCruxMob implements AbyssMob {
    protected final @NotNull EntityType spawnType;
    public SimpleAbyssMob(@NotNull Key key, @NotNull EntityType spawnType) {
        super(key);
        this.spawnType = spawnType;
    }

    @Override
    protected @NotNull Entity spawnAt(@NotNull Location location) {
        return spawn(null, location);
    }

    public @Nullable Consumer<Entity> spawnFunction(@Nullable GameManager game, @NotNull Location l){ return null; }

    public @NotNull Entity spawn(@Nullable GameManager game, @NotNull Location l){
        return spawnAt(game, l, null);
    }

    public @NotNull Entity spawnAt(@Nullable GameManager game, @NotNull Location l, @Nullable Consumer<Entity> consumer){
        return l.getWorld().spawnEntity(l, spawnType, CreatureSpawnEvent.SpawnReason.NATURAL, e ->{
            CruxEntitiesPersistTags.ENTITY.set(e, this.key);
            CruxTag.set(e, "level", PersistentDataType.INTEGER, (int) (game == null ? 1 : (game.getWave() * game.getDifficulty())));

            //Equipment
            if(e instanceof LivingEntity lE){
                if(lE.getEquipment() != null){
                    Map<EquipmentSlot, ItemStack> equipment = getEquipment(game, l);
                    if(equipment != null){
                        for(Map.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()){
                            lE.getEquipment().setItem(entry.getKey(), entry.getValue());
                        }
                    }
                }
                if(lE.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null){
                    lE.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(64D);
                }
            }
            //Atributes
            Map<CruxAttribute, Collection<CruxAttributeModifier>> attributes = getAttributes(game, e);
            if(attributes != null){
                for(Map.Entry<CruxAttribute, Collection<CruxAttributeModifier>> entry : attributes.entrySet()){
                    for(CruxAttributeModifier m : entry.getValue()){
                        CruxAttribute.addModifier(e, entry.getKey(), m);
                    }
                }
            }

            Consumer<Entity> spawnFunction = spawnFunction(game, l);
            if(spawnFunction != null) spawnFunction.accept(e);
            if(consumer != null) consumer.accept(e);

            //Mob goal
            load(e);
        });
    }

    public @Nullable Map<EquipmentSlot, ItemStack> getEquipment(@Nullable GameManager game, @NotNull Location l){
        return null;
    }

    public @Nullable Map<CruxAttribute, Collection<CruxAttributeModifier>> getAttributes(@Nullable GameManager game, @NotNull Entity e){
        int wave = game == null ? 1 : game.getWave();
        float difficulty = game == null ? 1f : game.getDifficulty();
        Map<CruxAttribute, Collection<CruxAttributeModifier>> map = new HashMap<>();
        Collection<CruxAttributeModifier> list = new HashSet<>();
        list.add(new CruxAttributeModifier(CruxMath.random(1D, 7D) * (wave * .1D) * difficulty));
        map.put(CruxAttribute.ATTACK_DAMAGE, list);

        list = new HashSet<>();
        list.add(new CruxAttributeModifier(((CruxMath.random(50D, 100D) * (wave * .01D + 1D)) * difficulty)));
        map.put(CruxAttribute.ATTACK_KNOCKBACK, list);

        list = new HashSet<>();
        list.add(new CruxAttributeModifier(CruxMath.random(-10, -6)));
        map.put(CruxAttribute.ATTACK_SPEED, list);

        list = new HashSet<>();

        list.add(new CruxAttributeModifier(CruxMath.random(0D, .5D)));
        map.put(CruxAttribute.ATTACK_AOE, list);

        list = new HashSet<>();
        list.add(new CruxAttributeModifier(e.getBoundingBox().getWidthX() + CruxMath.random(.1D, .5D)));
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
        return new CruxAttributeInstance(attribute, list).getValue();
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
}

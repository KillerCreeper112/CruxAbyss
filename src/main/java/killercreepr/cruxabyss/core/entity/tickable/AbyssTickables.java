package killercreepr.cruxabyss.core.entity.tickable;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.component.parser.InputDecodeContext;
import killercreepr.crux.api.component.parser.hybrid.PersistParser;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.api.data.ParticleBuilderSupplier;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.cruxattributes.api.attribute.CruxAttributeContainer;
import killercreepr.cruxattributes.api.equipment.CruxSlot;
import killercreepr.cruxattributes.core.component.CruxAttributeCompParsers;
import killercreepr.cruxtickables.api.entity.tickable.ActiveEntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickable;
import killercreepr.cruxtickables.api.entity.tickable.EntityTickableModifier;
import killercreepr.cruxtickables.core.entity.tickable.SimpleDataEntityTickable;
import killercreepr.cruxtickables.core.registries.CruxTickableRegistries;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AbyssTickables {
    public static void register(){}

    public static final EntityTickable TOXIC_FUMES = register(new SimpleDataEntityTickable(Crux.key("toxic_fumes")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, e -> (CruxAttributeContainer) e.get("attributes")))
            .field("chance", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("chance")))
            .field("range", TextInputField.field(PersistTextParser.FLOAT, e -> (Float) e.get("range")))
            .field("effects", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, e -> (List<PotionEffect>) e.get("effects")))
            .field("particle", TextInputField.field(ComponentInputParsers.PARTICLE_BUILDER_SUPPLIER, e -> (ParticleBuilderSupplier) e.get("particle")))
            .field("sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, e -> (CreateSound) e.get("sound")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();
            if(data==null) data= Map.of();
            ParticleBuilderSupplier particleSupplier = (ParticleBuilderSupplier) data.get("particle");
            if(particleSupplier == null){
                particleSupplier = ParticleBuilderSupplier.builder()
                    .particle(Particle.DUST_COLOR_TRANSITION)
                    .data(new Particle.DustTransition(Color.GREEN, Color.YELLOW, 1f))
                    .offset(.5, .5, .5)
                    .extra(.2)
                    .count(15)
                    .build();
            }
            CreateSound sound = (CreateSound) data.get("sound");
            if(sound == null){
                sound = CreateSound.sound(Sound.ENTITY_PUFFER_FISH_BLOW_UP, 1.3f);
            }
            return new ActiveToxicFumesTickable(
                entity, this, slot,
                (CruxAttributeContainer) data.getOrDefault("attributes", CruxAttributeContainer.empty()),
                (float) data.getOrDefault("chance", 30f),
                (float) data.getOrDefault("range", 2f),
                (List<PotionEffect>) data.get("effects"),
                particleSupplier, sound
            );
        }
    });

    public static final EntityTickable SCOURGER_HORN = register(new SimpleDataEntityTickable(Crux.key("scourger_horn")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("spore_attributes", TextInputField.field(CruxAttributeCompParsers.CRUX_ATTRIBUTE_CONTAINER, e -> (CruxAttributeContainer) e.get("spore_attributes")))
            .field("spore_effects", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, e -> (List<PotionEffect>) e.get("spore_effects")))
            .field("spore_particle", TextInputField.field(ComponentInputParsers.PARTICLE_BUILDER_SUPPLIER, e -> (ParticleBuilderSupplier) e.get("spore_particle")))
            .field("spore_spawn_sound", TextInputField.field(ComponentInputParsers.CREATE_SOUND, e -> (CreateSound) e.get("spore_spawn_sound")))
            .field("run_up", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("run_up")))
            .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("cooldown")))
            .field("max_spores", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("max_spores")))
            .field("spore_spawn_rate", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("spore_spawn_rate")))
            .field("spore_lifespan", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("spore_lifespan")))
            .field("item_dmg", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("item_dmg")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();
            if(data==null) data= Map.of();
            ParticleBuilderSupplier particleSupplier = (ParticleBuilderSupplier) data.get("spore_particle");
            if(particleSupplier == null){
                particleSupplier = ParticleBuilderSupplier.builder()
                    .particle(Particle.DUST_COLOR_TRANSITION)
                    .data(new Particle.DustTransition(Color.GREEN, Color.YELLOW, 1f))
                    .offset(.5, .5, .5)
                    .extra(.2)
                    .count(10)
                    .build();
            }
            CreateSound sound = (CreateSound) data.get("spore_spawn_sound");
            if(sound == null){
                sound = CreateSound.sound(Sound.ENTITY_PUFFER_FISH_BLOW_UP, 2f);
            }
            return new ActiveScourgerHornTickable(
                entity, this, slot,
                (CruxAttributeContainer) data.getOrDefault("spore_attributes", CruxAttributeContainer.empty()),
                (List<PotionEffect>) data.get("spore_effects"),
                (int) data.getOrDefault("run_up", 60),
                (int) data.getOrDefault("cooldown", 100),
                (int) data.getOrDefault("max_spores", 4),
                (int) data.getOrDefault("item_dmg", 1),
                (int) data.getOrDefault("spore_spawn_rate", 3),
                (int) data.getOrDefault("spore_lifespan", 100),
                particleSupplier,
                sound
            );
        }
    });

    public static final EntityTickable PLAGUE_STALKER = register(new SimpleDataEntityTickable(Crux.key("plague_stalker")) {
        public static final PersistParser<?> DATA_PARSER = PersistTextParser.mapBuilder(Map.class)
            .field("friends_range", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("friends_range")))
            .field("min_time", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("min_time")))
            .field("max_time", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("max_time")))
            .field("timed_effects", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, e -> (List<PotionEffect>) e.get("timed_effects")))
            .field("combat_friend_nearby_effects", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, e -> (List<PotionEffect>) e.get("combat_friend_nearby_effects")))
            .field("min_friends", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("min_friends")))
            .field("combat_cooldown", TextInputField.field(PersistTextParser.INTEGER, e -> (Integer) e.get("combat_cooldown")))
            .apply(InputDecodeContext::get)
            .createInput(Crux.key("data"));

        @Override
        public PersistParser<?> getDataParser() {
            return DATA_PARSER;
        }

        @Override
        public @Nullable ActiveEntityTickable buildActive(@NotNull Entity entity, @Nullable CruxSlot slot, @NotNull EntityTickableModifier mod) {
            Map data = (Map) mod.getData();
            if(data==null) data = Map.of();
            return new ActivePlagueStalkerTickable(
                entity, this, slot,
                (int) data.getOrDefault("friends_range", 16D),
                (int) data.getOrDefault("min_time", 13000),
                (int) data.getOrDefault("max_time", 24000),
                (Collection<PotionEffect>) data.get("timed_effects"),
                (Collection<PotionEffect>) data.get("combat_friend_nearby_effects"),
                (int) data.getOrDefault("min_friends", 2),
                (int) data.getOrDefault("combat_cooldown", 200)
            );
        }
    });

    private static EntityTickable register(EntityTickable tickable){
        return CruxTickableRegistries.ENTITY_TICKABLE.register(tickable);
    }
}

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
import killercreepr.cruxattributes.api.attribute.CruxAttributeInstance;
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

    private static EntityTickable register(EntityTickable tickable){
        return CruxTickableRegistries.ENTITY_TICKABLE.register(tickable);
    }
}

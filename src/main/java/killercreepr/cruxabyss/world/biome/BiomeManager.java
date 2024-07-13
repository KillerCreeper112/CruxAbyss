package killercreepr.cruxabyss.world.biome;

import killercreepr.crux.Crux;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.logging.Level;

public class BiomeManager {
    public static Holder<Biome> TOXIC_MIRE; //toxic mire
    public static Holder<Biome> CHARRED_WASTES;
    public static Holder<Biome> CORRUPT;
    public static void register(){
        WritableRegistry<Biome> registrywritable = (WritableRegistry<Biome>) MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.BIOME);

        try {
            Field frozen = MappedRegistry.class.getDeclaredField("frozen");
            frozen.setAccessible(true);
            frozen.set(registrywritable, false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Field unregisteredIntrusiveHolders;
        try {
            unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            unregisteredIntrusiveHolders.setAccessible(true);
            unregisteredIntrusiveHolders.set(registrywritable, new IdentityHashMap<>());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Biome plains = registrywritable.get(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath("minecraft", "plains")));
        Crux.log(Level.INFO, "Plains biome registry: " + (plains == null ? "null" : "FOUND"));
        new Color(0x04B449);
        new Color(0x3BD545);
        new Color(0x85A23F);
        Biome biome = new Biome.BiomeBuilder()
            .specialEffects(new BiomeSpecialEffects.Builder()
                .fogColor(0x3BD545)
                .foliageColorOverride(0x3BD545)
                .skyColor(0x3BD545)
                .waterColor(0x3BD545)
                .waterFogColor(0x3BD545)
                .grassColorOverride(0x3BD545)
                .ambientParticle(new AmbientParticleSettings(
                    new ParticleOptions() {
                        @Override
                        public @NotNull ParticleType<?> getType() {
                            return ParticleTypes.WHITE_ASH;
                        }
                    }, .03f
                ))
                .build())
            .downfall(.15f)
            .temperature(0f)
            .mobSpawnSettings(plains == null ? MobSpawnSettings.EMPTY : plains.getMobSettings())
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .build();
        registrywritable.createIntrusiveHolder(biome);
        TOXIC_MIRE = registrywritable.register(ResourceKey.create(Registries.BIOME,
                        ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "toxic_mire")),
                biome, RegistrationInfo.BUILT_IN);

        biome = new Biome.BiomeBuilder()
            .specialEffects(new BiomeSpecialEffects.Builder()
                .fogColor(0x504C4A)
                .foliageColorOverride(0x62554F)
                .skyColor(0x62554F)
                .waterColor(0x332925)
                .waterFogColor(0x564942)
                .grassColorOverride(0x67504D)
                .ambientParticle(new AmbientParticleSettings(
                    new ParticleOptions() {
                        @Override
                        public @NotNull ParticleType<?> getType() {
                            return ParticleTypes.ASH;
                        }
                    }, .01f
                ))
                .build())
            .downfall(0f)
            .temperature(1f)
            .mobSpawnSettings(plains == null ? MobSpawnSettings.EMPTY : plains.getMobSettings())
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .build();
        registrywritable.createIntrusiveHolder(biome);
        CHARRED_WASTES = registrywritable.register(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "charred_wastes")),
                biome, RegistrationInfo.BUILT_IN);

        biome = new Biome.BiomeBuilder()
            .specialEffects(new BiomeSpecialEffects.Builder()
                .fogColor(0xB285A7)
                .foliageColorOverride(0x804D71)
                .skyColor(0x7D576A)
                .waterColor(0x8C5B8C)
                .waterFogColor(0x8C5B8C)
                .grassColorOverride(0xA86582)
                .build())
            .downfall(.2f)
            .temperature(.4f)
            .mobSpawnSettings(plains == null ? MobSpawnSettings.EMPTY : plains.getMobSettings())
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .build();
        registrywritable.createIntrusiveHolder(biome);
        CORRUPT = registrywritable.register(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "corruption")),
                biome, RegistrationInfo.BUILT_IN);

        try {
            unregisteredIntrusiveHolders.set(registrywritable, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            Field frozen = MappedRegistry.class.getDeclaredField("frozen");
            frozen.setAccessible(true);
            frozen.set(registrywritable, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

package killercreepr.cruxabyss.core.world.biome;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.usurvive.USurvivePlugin;
import org.bukkit.block.Biome;

public class BiomeManager {
    //biomes added via datapack
    public static final Biome TOXIC_MIRE = biome("toxic_mire");
    public static final Biome CHARRED_WASTES = biome("charred_wastes");
    public static final Biome CORRUPTION = biome("corruption");
    public static final Biome ELDRITCH_WASTES = biome("eldritch_wastes");
    public static final Biome TOXIC_GRASSLANDS = biome("toxic_grasslands");

    public static Biome biome(String id){
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(USurvivePlugin.inst().key(id));
    }
    /*public static void register(){
        WritableRegistry<Biome> registrywritable = (WritableRegistry<Biome>) MinecraftServer.getServer()
            .registryAccess().get(Registries.BIOME).orElseThrow(() -> new RuntimeException("NOOOOOOOOOO")).value();
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
                ResourceLocation.fromNamespaceAndPath("minecraft", "plains"))).get().value();
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
        *//*TOXIC_MIRE = registrywritable.register(ResourceKey.create(Registries.BIOME,
                        ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "toxic_mire")),
                biome, RegistrationInfo.BUILT_IN);*//*

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
                            return ParticleTypes.SMOKE;
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
        *//*CHARRED_WASTES = registrywritable.register(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "charred_wastes")),
                biome, RegistrationInfo.BUILT_IN);*//*

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
        *//*CORRUPT = registrywritable.register(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "corruption")),
                biome, RegistrationInfo.BUILT_IN);*//*

        biome = new Biome.BiomeBuilder()
            .specialEffects(new BiomeSpecialEffects.Builder()
                .fogColor(0x151941)
                .foliageColorOverride(0x0D1573)
                .skyColor(0x151941)
                .waterColor(0x371D81)
                .waterFogColor(0x371D81)
                .grassColorOverride(0x340D73)
                .build())
            .downfall(.1f)
            .temperature(0f)
            .mobSpawnSettings(plains == null ? MobSpawnSettings.EMPTY : plains.getMobSettings())
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .build();
        registrywritable.createIntrusiveHolder(biome);
        *//*ELDRITCH_WASTES = registrywritable.register(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "eldritch_wastes")),
            biome, RegistrationInfo.BUILT_IN);*//*

        biome = new Biome.BiomeBuilder()
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
        *//*TOXIC_GRASSLANDS = registrywritable.register(ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "toxic_grasslands")),
            biome, RegistrationInfo.BUILT_IN);*//*

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
    }*/
}

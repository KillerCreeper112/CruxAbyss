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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class BiomeManager {
    public static Holder<Biome> CRIMSON;
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
        Biome biome = new Biome.BiomeBuilder()
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(0xE32323)
                        .foliageColorOverride(0xDE3030)
                        .skyColor(0xE32323)
                        .waterColor(0xE32323)
                        .waterFogColor(0xE32323)
                        .grassColorOverride(0xB22424)
                        .ambientParticle(new AmbientParticleSettings(
                                new ParticleOptions() {
                                    @Override
                                    public @NotNull ParticleType<?> getType() {
                                        return ParticleTypes.CRIMSON_SPORE;
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
        CRIMSON = registrywritable.register(ResourceKey.create(Registries.BIOME,
                        ResourceLocation.fromNamespaceAndPath(Crux.NAMESPACE, "crimson")),
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

    public static @NotNull NamespacedKey getBiome(@NotNull Location l){
        return getBiome(l.getBlock());
    }

    public static @NotNull NamespacedKey getBiome(@NotNull Block l){
        ServerLevel w = ((CraftWorld)l.getWorld()).getHandle();
        BlockPos pos = new BlockPos(l.getX(), l.getY(), l.getZ());
        if (w.isLoaded(pos)) {
            LevelChunk chunk = w.getChunkAt(pos);
            Optional<ResourceKey<Biome>> optional = chunk.getNoiseBiome(l.getX() >> 2, l.getY() >> 2, l.getZ() >> 2).unwrapKey();
            if(optional.isPresent()){
                ResourceKey<Biome> biome = optional.get();
                return new NamespacedKey(
                        biome.location().getNamespace(),
                        biome.location().getPath()
                );
            }
        }
        return l.getBiome().getKey();
    }

    public static boolean setBiome(@NotNull Holder<Biome> biomeHolder, @NotNull Chunk c) {
        ServerLevel w = ((CraftWorld)c.getWorld()).getHandle();
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for(int y = 0; y <= c.getWorld().getMaxHeight(); y++) {
                    setBiome(c.getX() * 16 + x, y, c.getZ() * 16 + z, w, biomeHolder);
                }
            }
        }
        refreshChunksForAll(c);
        return true;
    }

    public static boolean setBiome(@NotNull Holder<Biome> biomeHolder, @NotNull Location l) {
        setBiome(l.getBlockX(), l.getBlockY(), l.getBlockZ(), ((CraftWorld)l.getWorld()).getHandle(), biomeHolder);
        refreshChunksForAll(l.getChunk());
        return true;
    }

    public static void setBiome(int x, int y, int z, @NotNull ServerLevel w, @NotNull Holder<Biome> bb) {
        BlockPos pos = new BlockPos(x, 0, z);
        if (w.isLoaded(pos)) {
            LevelChunk chunk = w.getChunkAt(pos);
            chunk.setBiome(x >> 2, y >> 2, z >> 2, bb);
            chunk.setUnsaved(true);
        }
    }

    public static void refreshChunksForAll(@NotNull Chunk chunk) {
        ServerLevel level = ((CraftChunk)(chunk)).getCraftWorld().getHandle();
        level.getChunkSource().chunkMap.resendBiomesForChunks(List.of(level.getChunk(chunk.getX(),chunk.getZ())));
    }
}

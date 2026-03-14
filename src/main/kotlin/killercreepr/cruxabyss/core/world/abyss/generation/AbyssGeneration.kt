package killercreepr.cruxabyss.core.world.abyss.generation

import killercreepr.crux.core.util.CruxMath
import killercreepr.cruxabyss.core.world.abyss.generation.biome.BasaltSpires
import killercreepr.cruxabyss.core.world.abyss.generation.biome.CharredWastes
import killercreepr.cruxabyss.core.world.abyss.generation.biome.EldritchWastes
import killercreepr.cruxabyss.core.world.abyss.generation.biome.ToxicMireBiome
import killercreepr.cruxabyss.core.world.abyss.generation.zone.AbyssZone
import killercreepr.cruxworldgen.api.biome.Biome
import killercreepr.cruxworldgen.api.biome.BiomeRegistry
import killercreepr.cruxworldgen.api.decor.DecorationPipeline
import killercreepr.cruxworldgen.api.generation.GenerationPipeline
import killercreepr.cruxworldgen.api.noise.NoiseAutoInstaller
import killercreepr.cruxworldgen.api.structure.StructurePipeline
import killercreepr.cruxworldgen.api.zone.ZoneRegistry
import killercreepr.cruxworldgen.bukkit.generation.BukkitGenerationChunkGenerator
import killercreepr.cruxworldgen.bukkit.generation.WorldDetails
import killercreepr.cruxworldgen.core.biome.SimpleBiomeRegistry
import killercreepr.cruxworldgen.core.biome.volumetric.VolumetricBiomeRegistry
import killercreepr.cruxworldgen.core.decor.SimpleDecorationPipeline
import killercreepr.cruxworldgen.core.decor.SimplePropPointGrid
import killercreepr.cruxworldgen.core.feature.SimpleFeaturePipeline
import killercreepr.cruxworldgen.core.generation.SimpleGenerationPipeline
import killercreepr.cruxworldgen.core.noise.BaseNoiseModule
import killercreepr.cruxworldgen.core.noise.SimpleNoiseBank
import killercreepr.cruxworldgen.core.structure.SimpleStructurePipeline
import killercreepr.cruxworldgen.core.structure.SimpleStructureRegistry
import killercreepr.cruxworldgen.core.zone.SimpleZoneRegistry
import killercreepr.cruxworldgen.core.generation.chunk.SimpleChunkSampler

object AbyssGeneration {
  lateinit var abyssZone: AbyssZone
  lateinit var biomeRegistry: BiomeRegistry
  lateinit var zones : ZoneRegistry
  lateinit var generationPipeline : GenerationPipeline
  lateinit var decorationPipeline : DecorationPipeline
  lateinit var structurePipeline: StructurePipeline
  lateinit var volBiomes : VolumetricBiomeRegistry
  val defaultWorldDetails = WorldDetails(
    62,
    16, 16
  )

  fun register() {
    biomeRegistry = SimpleBiomeRegistry(
      biomes = listOf(
        ToxicMireBiome(),
        CharredWastes(),
        BasaltSpires(),
        EldritchWastes()
      ),
      biomeCellSizeBlocks = 256,
      blendRadiusBlocks = 32.0,
      //selector = SimpleBiomeRegistry.WeightedRaritySelector(),
      rules = object: SimpleBiomeRegistry.BiomeRuleProvider{
        override fun ruleFor(biome: Biome): SimpleBiomeRegistry.BiomeRule? {
          return null//todo
        }
      }
    )

    abyssZone = AbyssZone(biomeRegistry)

    zones = SimpleZoneRegistry(listOf(abyssZone))
    volBiomes = VolumetricBiomeRegistry(
      listOf(
      )
    )
    val structureRegistry = SimpleStructureRegistry(listOf())
    generationPipeline = SimpleGenerationPipeline(
      zones,
      volBiomes
    )
    decorationPipeline = SimpleDecorationPipeline(SimplePropPointGrid())
    structurePipeline = SimpleStructurePipeline(structureRegistry)
  }

  fun buildGenerator(
    seed : Long = CruxMath.random().nextLong(),
    worldDetails : WorldDetails = defaultWorldDetails,
  ) : BukkitGenerationChunkGenerator{
    val noise = SimpleNoiseBank(seed)
    BaseNoiseModule.install(noise)

    //auto install noises
    NoiseAutoInstaller(noise).apply {
      installAllFromZones(zones)
      installFromAll(volBiomes)
    }
    val features = SimpleFeaturePipeline(listOf())

    return BukkitGenerationChunkGenerator(
      generation = generationPipeline,
      decorations =decorationPipeline,
      structures = structurePipeline,
      noise = noise,
      worldDetails = worldDetails,
      features = features,
      chunkSampler = SimpleChunkSampler(
        generationPipeline,
        noise, worldDetails, 4,
        4
      )
    )
  }
}
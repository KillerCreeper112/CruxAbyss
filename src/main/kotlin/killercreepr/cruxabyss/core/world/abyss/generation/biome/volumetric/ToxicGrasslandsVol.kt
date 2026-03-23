package killercreepr.cruxabyss.core.world.abyss.generation.biome.volumetric

import killercreepr.cruxabyss.core.world.abyss.generation.biome.ToxicMire
import killercreepr.cruxabyss.core.world.biome.BiomeManager
import killercreepr.cruxgeneration.util.CruxNoise
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiome
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiomeShape
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.context.GenerateContext
import killercreepr.cruxworldgen.api.context.MaterialContext
import killercreepr.cruxworldgen.api.context.volumetric.VolumeEnv
import killercreepr.cruxworldgen.api.decor.VolumetricDecoration
import killercreepr.cruxworldgen.api.density.VolDensityStack
import killercreepr.cruxworldgen.api.generation.BiomeBlendSample
import killercreepr.cruxworldgen.api.material.MaterialProvider
import killercreepr.cruxworldgen.api.noise.*
import killercreepr.cruxworldgen.api.signal.SignalWriter
import killercreepr.cruxworldgen.bukkit.biome.BukkitBiome
import killercreepr.cruxworldgen.extension.remap01
import org.bukkit.block.Biome

class ToxicGrasslandsVol(
) : VolumetricBiome, Noised, BukkitBiome {

  override fun toBukkitBiome(): Biome = BiomeManager.TOXIC_GRASSLANDS

  override fun allowedIn(surface: BiomeBlendSample): Boolean = surface.primaryBiome() is ToxicMire
  override fun suitability(
    ctx: GenerateContext,
    worldX: Int,
    y: Int,
    worldZ: Int,
    env: VolumeEnv,
    signals: SignalWriter
  ): Double {
    // Only affect a very small band around the surface
    //if (env.depthBelowSurface < -3) return 0.0          // above surface
    //if (env.depthBelowSurface > 3) return 0.0          // too far below surface

    // Rare patch mask across XZ
    val patchNoise = ctx.noise.get(Noise.Patch2D).noise2D(worldX, worldZ)
    val patch01 = patchNoise.remap01()

    // Only some areas of Toxic Mire get this biome
    if (patch01 < 0.6) return 0.0

    // Strongest exactly at surface, fades just below it
    val surfaceFade = when (env.depthBelowSurface) {
      -3 -> 0.1
      -2 -> 0.3
      -1 -> 0.65
      0 -> 1.0
      1 -> 0.65
      2 -> 0.3
      3 -> 0.1
      else -> 0.0
    }

    // Make higher patch values stronger
    val rarityStrength = ((patch01 - 0.82) / (1.0 - 0.82)).coerceIn(0.0, 1.0)

    return patch01//surfaceFade * rarityStrength
  }

  override val volumetricDecorations: List<VolumetricDecoration> = listOf(
  )

  object Noise : NoiseModule {

    object Patch2D : NoiseKey {
      override val id = "biome.toxic_grasslands.patch2D"
    }

    override fun install(bank: NoiseBank) {
      bank.register(Patch2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.01)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }
    }
  }

  override val noiseModule = Noise

  override val materialProvider: MaterialProvider = object : MaterialProvider {
    override fun chooseMaterial(ctx: MaterialContext): BlockData = BlockData.PRIMARY_BIOME
  }

  override val shape = object : VolumetricBiomeShape{
    override fun density(
      ctx: GenerateContext,
      worldX: Int,
      y: Int,
      worldZ: Int,
      env: VolumeEnv,
      signals: SignalWriter
    ): VolDensityStack? {
      val terrain = env.terrainDensity

      // flatter target height for this biome
      val flatTargetY = ctx.chunkContext.seaLevel + 40.0

      // signed field for a simple flat surface:
      // positive below target surface, negative above it
      val flatField = (flatTargetY - y)

      // how strongly to pull toward the flat field
      val flattenStrength = 0.4

      // delta needed to nudge current terrain toward the flatter field
      val flattenDelta = (flatField - terrain) * flattenStrength

      return VolDensityStack.volDensityStack(
        base = flattenDelta,
        add = 0.0,
        carve = 0.0,
        replaceMask = 0.0
      )
    }
  }
}
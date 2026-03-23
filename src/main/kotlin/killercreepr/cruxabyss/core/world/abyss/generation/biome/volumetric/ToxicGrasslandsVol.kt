package killercreepr.cruxabyss.core.world.abyss.generation.biome.volumetric

import killercreepr.crux.core.Crux
import killercreepr.crux.core.util.CruxMath
import killercreepr.cruxabyss.core.block.AbyssBlocks
import killercreepr.cruxabyss.core.world.abyss.generation.biome.ToxicGrasslands
import killercreepr.cruxabyss.core.world.abyss.generation.biome.ToxicMire
import killercreepr.cruxabyss.core.world.biome.BiomeManager
import killercreepr.cruxblocks.api.block.component.BushType
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents
import killercreepr.cruxgeneration.util.CruxNoise
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiome
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiomeShape
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.block.BlockPicker
import killercreepr.cruxworldgen.api.context.GenerateContext
import killercreepr.cruxworldgen.api.context.LimitedRegion
import killercreepr.cruxworldgen.api.context.MaterialContext
import killercreepr.cruxworldgen.api.context.volumetric.VolumeEnv
import killercreepr.cruxworldgen.api.decor.Placement
import killercreepr.cruxworldgen.api.decor.PropPoint
import killercreepr.cruxworldgen.api.decor.VolumetricDecoration
import killercreepr.cruxworldgen.api.density.VolDensityStack
import killercreepr.cruxworldgen.api.generation.BiomeBlendSample
import killercreepr.cruxworldgen.api.material.MaterialProvider
import killercreepr.cruxworldgen.api.noise.*
import killercreepr.cruxworldgen.api.signal.SignalWriter
import killercreepr.cruxworldgen.api.util.Curve
import killercreepr.cruxworldgen.bukkit.biome.BukkitBiome
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockAdapter
import killercreepr.cruxworldgen.extension.remap01
import killercreepr.cruxworldgen.standard.decor.Group2DDecor
import killercreepr.cruxworldgen.standard.decor.TallGrassTriDecor
import killercreepr.cruxworldgen.standard.decor.volumetric.Group3DDecor
import killercreepr.cruxworldgen.standard.decor.volumetric.TallGrassTriVolDecor
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
    val patchNoise = ctx.noise.get(Noise.Patch2D).noise2D(worldX, worldZ)
    val patch01 = patchNoise.remap01()
    if (patch01 < 0.6) return 0.0

    val d = env.depthBelowSurface.toDouble()

    val maxAbove = 64.0
    val maxBelow = 48.0

    val verticalFade = when {
      d < 0.0 -> 1.0 - Curve.smoothstep(0.0, maxAbove, -d)
      else -> 1.0 - Curve.smoothstep(0.0, maxBelow, d)
    }

    val rarityStrength = Curve.smoothstep(0.6, 0.9, patch01)
    return verticalFade * rarityStrength
  }

  override val volumetricDecorations: List<VolumetricDecoration> = listOf(
    Group3DDecor(
      chancePerPoint = 1.0,
      minRadius = 0,
      maxRadius = 10,
      minPickAmount = 10,
      maxPickAmount = 20,
      minYRadius = 0,
      maxYRadius = 5,
      decorations = listOf(
        TallGrassTriVolDecor(
          chancePerPoint = 0.42,
          minHeight = 3,
          maxHeight = 5,
          top = BlockPicker.constant(
            BukkitBlockAdapter.resolver().resolve(
              AbyssBlocks.PLAGUE_ROOTS.components.get(CruxBlockComponents.BUSH_GROUP)!!.getBlock(BushType.TOP)!!
            )
          ),
          middle = BlockPicker.constant(
            BukkitBlockAdapter.resolver().resolve(
              AbyssBlocks.PLAGUE_ROOTS.components.get(CruxBlockComponents.BUSH_GROUP)!!.getBlock(BushType.MIDDLE)!!
            )
          ),
          bottom = BlockPicker.constant(
            BukkitBlockAdapter.resolver().resolve(
              AbyssBlocks.PLAGUE_ROOTS.components.get(CruxBlockComponents.BUSH_GROUP)!!
                .getBlock(BushType.BOTTOM)!!
            )
          ),
          chanceSalt = CruxMath.random().nextLong()
        )
      )
    )
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
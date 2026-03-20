package killercreepr.cruxabyss.core.world.abyss.generation.biome

import killercreepr.crux.api.data.Holder
import killercreepr.crux.core.util.CruxMath
import killercreepr.cruxabyss.core.world.abyss.generation.feature.AbyssFeatures
import killercreepr.cruxabyss.core.world.biome.BiomeManager
import killercreepr.cruxgeneration.util.CruxNoise
import killercreepr.cruxworldgen.api.biome.Biome
import killercreepr.cruxworldgen.api.biome.BiomeShape
import killercreepr.cruxworldgen.api.biome.BiomeShapeProfile
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.cave.CaveProfile
import killercreepr.cruxworldgen.api.cave.CaveShape
import killercreepr.cruxworldgen.api.context.BiomeEdgeContext
import killercreepr.cruxworldgen.api.context.GenerateContext
import killercreepr.cruxworldgen.api.context.MaterialContext
import killercreepr.cruxworldgen.api.decor.Decoration
import killercreepr.cruxworldgen.api.decor.VolumetricDecoration
import killercreepr.cruxworldgen.api.density.DensityStack
import killercreepr.cruxworldgen.api.feature.PlacedFeature
import killercreepr.cruxworldgen.api.material.MaterialProvider
import killercreepr.cruxworldgen.api.noise.NoiseBank
import killercreepr.cruxworldgen.api.noise.NoiseField
import killercreepr.cruxworldgen.api.noise.NoiseKey
import killercreepr.cruxworldgen.api.noise.NoiseModule
import killercreepr.cruxworldgen.api.signal.SignalWriter
import killercreepr.cruxworldgen.api.util.Curve.smoothstep01
import killercreepr.cruxworldgen.api.util.NoiseShaper
import killercreepr.cruxworldgen.bukkit.biome.BukkitBiome
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockAdapter
import killercreepr.cruxworldgen.standard.cave.SpaghettiCaves
import killercreepr.cruxworldgen.standard.cave.Standard3DCaves
import killercreepr.cruxworldgen.standard.cave.WormCaves
import killercreepr.cruxworldgen.standard.decor.volumetric.GrassVolDecor
import killercreepr.cruxworldgen.test.biome.AbyssStartOverhang
import org.bukkit.Material
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow

class FungalGrove(
  private val baseYAboveSea: Double = 10.0,

  // --- General height ---
  private val continentAmp: Double = 10.0,
  private val moundAmp: Double = 24.0,
  private val ridgeAmp: Double = 14.0,
  private val basinAmp: Double = 18.0,
  private val detailAmp: Double = 4.0,

  // --- Rare larger “fungal dome” landforms ---
  private val domeAmp: Double = 30.0,
  private val domeStart01: Double = 0.74,
  private val domeEnd01: Double = 0.95,
  private val domePower: Double = 2.2,

  // --- Ridge / basin shaping ---
  private val ridgeStart01: Double = 0.42,
  private val ridgeEnd01: Double = 0.88,
  private val ridgePower: Double = 1.7,
  private val basinPower: Double = 1.4,

  // --- Domain warp ---
  private val warpAmpBlocks: Double = 68.0,

  private val terraceStep: Double = 0.0,
  private val terraceBlend: Double = 0.35,

  override val caves: CaveShape<*, *> = CaveProfile(
    listOf(
      WormCaves(),
      SpaghettiCaves(),
      Standard3DCaves(),
    )
  ),

  override val materialProvider: MaterialProvider = object : MaterialProvider {
    override fun chooseMaterial(ctx: MaterialContext): BlockData {
      if (!ctx.isSolid) return BlockData.NONE

      val depth = ctx.depthBelowSurface

      // Sea-floor / basin crust
      if (ctx.depthFromSeaFloor in 0..<3) {
        return BukkitBlockAdapter.resolver().resolve(Material.SAND)
      }

      if(ctx.airRun > 2){
        return BukkitBlockAdapter.resolver().resolve(Material.MYCELIUM)
      }

      if(ctx.surfaceDepth < 4){
        return BukkitBlockAdapter.resolver().resolve(Material.DIRT)
      }

      if (ctx.surfaceDepth < 90) {
        return BukkitBlockAdapter.resolver().resolve(Material.STONE)
      }
      return BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE)
    }
  },

  override val volumetricDecorations: List<VolumetricDecoration> = listOf(
    GrassVolDecor(
      chancePerPoint = 0.15,
      minAirAbove = 1,
      maxSlope01 = 0.0,
      block = Holder.direct(BukkitBlockAdapter.resolver().resolve(Material.BROWN_MUSHROOM)),
      salt = CruxMath.random().nextLong()
    ),
    GrassVolDecor(
      chancePerPoint = 0.15,
      minAirAbove = 1,
      maxSlope01 = 0.0,
      block = Holder.direct(BukkitBlockAdapter.resolver().resolve(Material.RED_MUSHROOM)),
      salt = CruxMath.random().nextLong()
    )
  ),

  override val features: List<PlacedFeature<*>> = listOf(
    AbyssFeatures.Misc.REMOVE_BOTTOM_LATER,
    AbyssFeatures.Ores.MOULDITE_CRUST,
    AbyssFeatures.Ores.EMERALD_LOW,
    AbyssFeatures.Ores.EMERALD_HIGH,
    AbyssFeatures.Ores.FUNGIRE,

    AbyssFeatures.Ores.GOLD_LOW,
    AbyssFeatures.Ores.REDSTONE_LOW,
    AbyssFeatures.Ores.LAPIS_LOW,
    AbyssFeatures.Ores.IRON_LOW,
    AbyssFeatures.Ores.IRON_HIGH,
    AbyssFeatures.Ores.COPPER,
    AbyssFeatures.Ores.COAL,
    AbyssFeatures.Ores.COAL_HIGH,
  ),

  override val decorations: List<Decoration> = listOf(
    /*BrownMushroomDecor(
      chancePerPoint = 0.05,
      stemHeightMin = 3,
      stemHeightMax = 6,
      stemRadiusMin = 1f,
      stemRadiusMax = 1f,
      capRadiusMin = 3f,
      capRadiusMax = 5f,
      capHeightScaleMin = 0.1f,
      capHeightScaleMax = 0.1f,
      stemWanderStrength = 0.15f,
      capNoise = Noise.BrownMushroomCap,
      stemNoise = Noise.BrownMushroomStem,
      capBlock = BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.BROWN_MUSHROOM_BLOCK)),
      stemBlock = BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.MUSHROOM_STEM))
    ),*/
    /*BrownMushroomDecor(
      chancePerPoint = 0.05,
      stemHeightMin = 12,
      stemHeightMax = 36,
      capRadiusMin = 8f,
      capRadiusMax = 16f,
      capHeightScaleMin = 0.12f,
      capHeightScaleMax = 0.25f,
      stemWanderStrength = 0.3f,
      capNoise = Noise.BrownMushroomCap,
      stemNoise = Noise.BrownMushroomStem,
      capBlock = BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.BROWN_MUSHROOM_BLOCK)),
      stemBlock = BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.MUSHROOM_STEM))
    ),
    RoundedRedMushroomDecor(
      chancePerPoint = 0.03,
      stemHeightMin = 10,
      stemHeightMax = 28,
      capRadiusMin = 8f,
      capRadiusMax = 16f,
      stemWanderStrength = 0.3f,

      rimCurlMin = 0.5,
      rimCurlMax = 1.5,

      rimDropFractionMin = 0.5,
      rimDropFractionMax = 0.8,

      capNoise = Noise.BrownMushroomCap,
      stemNoise = Noise.BrownMushroomStem,
      capBlock = BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.RED_MUSHROOM_BLOCK)),
      stemBlock = BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.MUSHROOM_STEM))
    ),
    FungiSpikeDecor(
      chancePerPoint = 0.3,
      noise = Noise.Spike,
      blocks = listOf(
        BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.RED_MUSHROOM_BLOCK)),
        BlockGetter.constant(BukkitBlockAdapter.resolver().resolve(Material.BROWN_MUSHROOM_BLOCK))
      )
    )*/
  )
) : Biome.Noised, BukkitBiome {

  override fun toBukkitBiome() = BiomeManager.FUNGAL_GROVE

  object Noise : NoiseModule {
    object Warp2D : NoiseKey {
      override val id = "biome.fungal_groves.warp2D"
    }

    object Continent2D : NoiseKey {
      override val id = "biome.fungal_groves.continent2D"
    }

    object Mounds2D : NoiseKey {
      override val id = "biome.fungal_groves.mounds2D"
    }

    object Roots2D : NoiseKey {
      override val id = "biome.fungal_groves.roots2D"
    }

    object Basins2D : NoiseKey {
      override val id = "biome.fungal_groves.basins2D"
    }

    object Domes2D : NoiseKey {
      override val id = "biome.fungal_groves.domes2D"
    }

    object Detail2D : NoiseKey {
      override val id = "biome.fungal_groves.detail2D"
    }

    // --- Overhang / arch support ---
    object Overhang3D : NoiseKey {
      override val id = "biome.fungal_groves.overhang3D"
    }

    object OverhangWarp2D : NoiseKey {
      override val id = "biome.fungal_groves.overhang_warp2D"
    }

    object OverhangX3D : NoiseKey {
      override val id = "biome.fungal_groves.overhang_x3D"
    }

    object OverhangY3D : NoiseKey {
      override val id = "biome.fungal_groves.overhang_y3D"
    }

    object OverhangZ3D : NoiseKey {
      override val id = "biome.fungal_groves.overhang_z3D"
    }

    object OverhangCarve3D : NoiseKey {
      override val id = "biome.fungal_groves.overhang_carve3D"
    }

    object Spike : NoiseKey {
      override val id = "biome.fungal_groves.spike"
    }

    object BrownMushroomCap : NoiseKey {
      override val id = "biome.fungal_groves.brown_mushroom.cap"
    }
    object BrownMushroomStem : NoiseKey {
      override val id = "biome.fungal_groves.brown_mushroom.stem"
    }

    override fun install(bank: NoiseBank) {
      bank.register(Noise.BrownMushroomCap) { seed ->
        NoiseField.Companion.noiseField(seed) {
          frequency(0.015)
            .noiseType(CruxNoise.NoiseType.OpenSimplex2)
            .fractalType(CruxNoise.FractalType.FBm)
            .fractalOctaves(3)
        }
      }
      bank.register(Noise.BrownMushroomStem) { seed ->
        NoiseField.Companion.noiseField(seed) {
          frequency(0.07)
            .noiseType(CruxNoise.NoiseType.OpenSimplex2)
            .fractalType(CruxNoise.FractalType.FBm)
            .fractalOctaves(2)
        }
      }
      bank.register(Spike) { seed ->
        NoiseField.noiseField(seed) {
          frequency(.09)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(3)
        }
      }
      bank.register(Warp2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0016)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(3)
        }
      }

      bank.register(Continent2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0022)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(Mounds2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0050)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(3)
        }
      }

      bank.register(Roots2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0062)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(Basins2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0032)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(3)
        }
      }

      bank.register(Domes2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0028)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(Detail2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.022)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(3)
        }
      }

      // Subtle overhang setup: slower fields, low octaves
      bank.register(Overhang3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.007)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(OverhangWarp2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0035)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(OverhangX3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0055)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(OverhangY3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0055)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(OverhangZ3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0055)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(OverhangCarve3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.015)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }
    }
  }

  override val noiseModule = Noise

  private val shaper = NoiseShaper(
    listOf(
      NoiseShaper.Point(-1.0, NoiseShaper.ShapingFunction.VALLEY),
      NoiseShaper.Point(-0.55, NoiseShaper.ShapingFunction.VALLEY),
      NoiseShaper.Point(-0.15, NoiseShaper.ShapingFunction.FLAT),
      NoiseShaper.Point(0.22, NoiseShaper.ShapingFunction.FLAT),
      NoiseShaper.Point(0.58, NoiseShaper.ShapingFunction.HILLS),
      NoiseShaper.Point(0.82, NoiseShaper.ShapingFunction.HILLS),
      NoiseShaper.Point(1.0, NoiseShaper.ShapingFunction.FLAT)
    )
  )

  override val shape = BiomeShapeProfile(
    object : BiomeShape {
      override fun density(
        ctx: GenerateContext,
        worldX: Int,
        y: Int,
        worldZ: Int,
        edge: BiomeEdgeContext,
        signalWriter: SignalWriter
      ): DensityStack {
        val sea = ctx.chunkContext.seaLevel.toDouble()
        val baseSurface = sea + baseYAboveSea

        val wx = worldX.toDouble()
        val wz = worldZ.toDouble()

        val warpN = ctx.noise.get(Noise.Warp2D)
        val warpX = warpN.noise2D(wx, wz) * warpAmpBlocks
        val warpZ = warpN.noise2D(wx + 1000.0, wz - 1000.0) * warpAmpBlocks
        val xw = wx + warpX
        val zw = wz + warpZ

        val continentN = shaper.smoothShape(ctx.noise.get(Noise.Continent2D).noise2D(xw, zw))
        val moundN = shaper.smoothShape(ctx.noise.get(Noise.Mounds2D).noise2D(xw, zw))
        val rootN = shaper.smoothShape(ctx.noise.get(Noise.Roots2D).noise2D(xw, zw))
        val basinN = shaper.smoothShape(ctx.noise.get(Noise.Basins2D).noise2D(xw, zw))
        val domeN = shaper.smoothShape(ctx.noise.get(Noise.Domes2D).noise2D(xw + 2500.0, zw - 2500.0))
        val detailN = ctx.noise.get(Noise.Detail2D).noise2D(wx, wz)

        val continent01 = ((continentN + 1.0) * 0.5).coerceIn(0.0, 1.0)
        val mound01 = ((moundN + 1.0) * 0.5).coerceIn(0.0, 1.0)
        val basin01 = ((basinN + 1.0) * 0.5).coerceIn(0.0, 1.0)
        val dome01 = ((domeN + 1.0) * 0.5).coerceIn(0.0, 1.0)

        val rootLine01 = (1.0 - abs(rootN)).coerceIn(0.0, 1.0)
        val ridgeT = ((rootLine01 - ridgeStart01) / (ridgeEnd01 - ridgeStart01)).coerceIn(0.0, 1.0)
        val ridgeMask = smoothstep01(ridgeT).pow(ridgePower)

        val moundMask = smoothstep01(((mound01 - 0.34) / (0.86 - 0.34)).coerceIn(0.0, 1.0))

        val basinDepth = smoothstep01(basin01).pow(basinPower) * (1.0 - 0.35 * moundMask)

        val domeT = ((dome01 - domeStart01) / (domeEnd01 - domeStart01)).coerceIn(0.0, 1.0)
        val domeMask = smoothstep01(domeT).pow(domePower) * (0.45 + 0.55 * moundMask)

        val softLowland = 1.0 - 0.35 * smoothstep01(((basin01 - 0.45) / (0.92 - 0.45)).coerceIn(0.0, 1.0))

        var offset = 0.0

        offset += (continent01 - 0.5) * 2.0 * continentAmp
        offset += (mound01 - 0.5) * 2.0 * moundAmp * softLowland
        offset += ridgeMask * ridgeAmp * (0.60 + 0.40 * moundMask)
        offset += domeMask * domeAmp
        offset -= basinDepth * basinAmp

        val detailMul = 0.45 + 0.35 * moundMask + 0.20 * ridgeMask
        offset += detailN * detailAmp * detailMul * (1.0 - 0.30 * basinDepth)

        if (terraceStep > 0.0) {
          val q = floor(offset / terraceStep) * terraceStep
          offset = q * (1.0 - terraceBlend) + offset * terraceBlend
        }

        val surfaceY = baseSurface + offset
        val density = surfaceY - y.toDouble()

        return DensityStack.densityStack(
          base = density,
          add = 0.0,
          carve = 0.0
        )
      }
    },
    listOf(
      AbyssStartOverhang(
        NoiseShaper(
          listOf(
            NoiseShaper.Point(-1.0, NoiseShaper.ShapingFunction.VALLEY),
            NoiseShaper.Point(-0.35, NoiseShaper.ShapingFunction.FLAT),
            NoiseShaper.Point(0.10, NoiseShaper.ShapingFunction.FLAT),
            NoiseShaper.Point(0.68, NoiseShaper.ShapingFunction.FLAT),
            NoiseShaper.Point(0.82, NoiseShaper.ShapingFunction.HILLS),
            NoiseShaper.Point(1.0, NoiseShaper.ShapingFunction.MOUNTAIN)
          )
        ),
        Noise.Overhang3D,
        Noise.OverhangWarp2D,
        Noise.OverhangX3D,
        Noise.OverhangY3D,
        Noise.OverhangZ3D,
        Noise.OverhangCarve3D
      )
    )
  )
}
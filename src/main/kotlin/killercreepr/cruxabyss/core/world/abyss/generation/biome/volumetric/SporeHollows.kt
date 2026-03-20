package killercreepr.cruxabyss.core.world.abyss.generation.biome.volumetric

import killercreepr.cruxabyss.core.world.abyss.generation.biome.FungalGrove
import killercreepr.cruxgeneration.util.CruxNoise
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiome
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiomeShape
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.block.BlockPicker
import killercreepr.cruxworldgen.api.context.GenerateContext
import killercreepr.cruxworldgen.api.context.MaterialContext
import killercreepr.cruxworldgen.api.context.volumetric.VolumeEnv
import killercreepr.cruxworldgen.api.decor.VolumetricDecoration
import killercreepr.cruxworldgen.api.density.VolDensityStack
import killercreepr.cruxworldgen.api.feature.HeightFilter
import killercreepr.cruxworldgen.api.generation.BiomeBlendSample
import killercreepr.cruxworldgen.api.material.MaterialProvider
import killercreepr.cruxworldgen.api.noise.*
import killercreepr.cruxworldgen.api.signal.SignalWriter
import killercreepr.cruxworldgen.api.util.Curve
import killercreepr.cruxworldgen.bukkit.biome.BukkitBiome
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockAdapter
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockResolver
import killercreepr.cruxworldgen.extension.remap01
import killercreepr.cruxworldgen.standard.decor.BrownMushroomDecor
import killercreepr.cruxworldgen.standard.decor.volumetric.StalactiteVolDecor
import org.bukkit.Material
import org.bukkit.block.Biome
import kotlin.math.abs
import kotlin.math.max

class SporeHollows(
  val noise: Noise = DefaultNoise,
  val minDepthBelowSurface: Int,
) : VolumetricBiome, Noised, BukkitBiome {

  override fun allowedIn(surface: BiomeBlendSample): Boolean = surface.primaryBiome() is FungalGrove

  override val volumetricDecorations: List<VolumetricDecoration> = listOf(
    StalactiteVolDecor(
      chancePerPoint = 0.3,
      yOffset = -1,
      minLength = 6,
      maxLength = 40,
      baseSizeMin = 1,
      baseSizeMax = 3,
      sizeTaperOffMin = 0.2,
      sizeTaperOffMax = 0.45,
      block = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.MUSHROOM_STEM))
    ),
    BrownMushroomDecor(
      chancePerPoint = 0.04,
      stemHeightMin = 3,
      stemHeightMax = 7,
      stemRadiusMin = 0.5f,
      stemRadiusMax = 0.5f,
      capRadiusMin = 4f,
      capRadiusMax = 7f,
      capHeightScaleMin = 0.2f,
      capHeightScaleMax = 0.3f,
      stemWanderStrength = 0.15f,
      capNoise = FungalGrove.Noise.BrownMushroomCap,
      stemNoise = FungalGrove.Noise.BrownMushroomStem,
      capBlock = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.BROWN_MUSHROOM_BLOCK)),
      stemBlock = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.MUSHROOM_STEM))
    ),
  )

  interface Noise {
    val biomeMask3D: NoiseKey
    val layerWarp3D: NoiseKey
    val vault2D: NoiseKey

    val cavern3D: NoiseKey
    val chamber3D: NoiseKey
    val aisle3D: NoiseKey
    val detail3D: NoiseKey

    val warpX3D: NoiseKey
    val warpY3D: NoiseKey
    val warpZ3D: NoiseKey

    val shelf3D: NoiseKey
    val pillar2D: NoiseKey
    val canopy3D: NoiseKey
  }

  object DefaultNoise : NoiseModule, Noise {
    override val biomeMask3D = object : NoiseKey { override val id = "biome3D.spore_hollows.mask3D" }
    override val layerWarp3D = object : NoiseKey { override val id = "biome3D.spore_hollows.layerWarp3D" }

    override val vault2D     = object : NoiseKey { override val id = "biome3D.spore_hollows.vault2D" }

    override val cavern3D    = object : NoiseKey { override val id = "biome3D.spore_hollows.cavern3D" }
    override val chamber3D   = object : NoiseKey { override val id = "biome3D.spore_hollows.chamber3D" }
    override val aisle3D     = object : NoiseKey { override val id = "biome3D.spore_hollows.aisle3D" }
    override val detail3D    = object : NoiseKey { override val id = "biome3D.spore_hollows.detail3D" }

    override val warpX3D     = object : NoiseKey { override val id = "biome3D.spore_hollows.warpX3D" }
    override val warpY3D     = object : NoiseKey { override val id = "biome3D.spore_hollows.warpY3D" }
    override val warpZ3D     = object : NoiseKey { override val id = "biome3D.spore_hollows.warpZ3D" }

    override val shelf3D     = object : NoiseKey { override val id = "biome3D.spore_hollows.shelf3D" }
    override val pillar2D    = object : NoiseKey { override val id = "biome3D.spore_hollows.pillar2D" }
    override val canopy3D    = object : NoiseKey { override val id = "biome3D.spore_hollows.canopy3D" }

    override fun install(bank: NoiseBank) {
      bank.register(biomeMask3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0022)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(layerWarp3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0040)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      // Shifts how "thick" the cave band is per x/z column so some places open
      // into huge cathedrals while others pinch down.
      bank.register(vault2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0018)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      // Main giant cavern field
      bank.register(cavern3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0030)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          rotationType3D(CruxNoise.RotationType3D.ImproveXZPlanes)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      // Extra broad chambers / pockets so it feels less uniform
      bank.register(chamber3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0048)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          rotationType3D(CruxNoise.RotationType3D.ImproveXZPlanes)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      // Ridged-ish connective field for wide aisles between large chambers
      bank.register(aisle3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0065)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(detail3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.012)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(warpX3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0040)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(warpY3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0040)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(warpZ3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0040)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      // Slow lumpy field that makes shelves / wall bulges / ledges
      bank.register(shelf3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0016)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          rotationType3D(CruxNoise.RotationType3D.ImproveXZPlanes)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(1)
        }
      }

      // 2D so supports stay vertically coherent like giant fungal trunks
      bank.register(pillar2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0038)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      // Extra chunky irregularity for ceiling/floor bulges
      bank.register(canopy3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0055)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          rotationType3D(CruxNoise.RotationType3D.ImproveXZPlanes)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }
    }
  }

  override val noiseModule = DefaultNoise

  override val materialProvider = object : MaterialProvider {
    override fun chooseMaterial(context: MaterialContext): BlockData {
      if (!context.isSolid) return BlockData.NONE

      // Keep this neutral for now so shape is easier to tune.
      // Later you can swap to a fungal palette block resolver.
      return BukkitBlockResolver.INSTANCE.resolve("stone")
    }
  }

  override fun suitability(
    ctx: GenerateContext,
    worldX: Int,
    y: Int,
    worldZ: Int,
    env: VolumeEnv,
    signals: SignalWriter
  ): Double {
    val depthBelowSurface = (-env.heightAboveSurface.toDouble()).coerceAtLeast(0.0)
    if (depthBelowSurface < minDepthBelowSurface) return 0.0

    // Main 3D biome placement mask
    val maskRaw = ctx.noise.get(noise.biomeMask3D).noise3D(worldX, y, worldZ).remap01()
    val biomeMask = Curve.smoothstep(0.54, 0.86, maskRaw)

    // Broad envelope so the biome doesn't continue forever downward
    val depthIn = Curve.smoothstep(8.0, 18.0, depthBelowSurface)
    val depthOut = 1.0 - Curve.smoothstep(120.0, 170.0, depthBelowSurface)
    val depthEnvelope = max(0.0, depthIn * depthOut)
    if (depthEnvelope <= 0.0) return 0.0

    // Repeating stacked layers
    val layerWarp = ctx.noise.get(noise.layerWarp3D).noise3D(worldX, y, worldZ) * 10.0
    val layeredDepth = depthBelowSurface + layerWarp

    val layerSpacing = 34.0      // distance between layer centers
    val layerThickness = 18.0    // full-ish thickness of each layer
    val halfThickness = layerThickness * 0.5

    val phase = layeredDepth % layerSpacing
    val distToCenter = kotlin.math.abs(phase - layerSpacing * 0.5)

    // High near the center of each repeated band, low between them
    val stackedMask = 1.0 - Curve.smoothstep(
      halfThickness,
      halfThickness + 6.0,
      distToCenter
    )

    return biomeMask * depthEnvelope * stackedMask
  }

  override val shape = object : VolumetricBiomeShape {
    override fun density(
      ctx: GenerateContext,
      worldX: Int,
      y: Int,
      worldZ: Int,
      env: VolumeEnv,
      signals: SignalWriter
    ): VolDensityStack? {
      if(env.depthBelowSurface < minDepthBelowSurface) return null

      val suitability = suitability(ctx, worldX, y, worldZ, env, signals)
      if (suitability <= 0.0) return VolDensityStack.emptyStack()

      val solidDensity = max(0.0, env.terrainDensity)
      if (solidDensity <= 0.0) return VolDensityStack.emptyStack()

      val x = worldX.toDouble()
      val yy = y.toDouble()
      val z = worldZ.toDouble()

      // Organic warp so rooms don't feel spherical/grid-aligned
      val wx = x + (ctx.noise.get(noise.warpX3D).noise3D(x, yy, z) * 44.0)
      val wy = yy + (ctx.noise.get(noise.warpY3D).noise3D(x, yy, z) * 16.0)
      val wz = z + (ctx.noise.get(noise.warpZ3D).noise3D(x, yy, z) * 44.0)

      // Main large-room field
      val cavernN = ctx.noise.get(noise.cavern3D).noise3D(wx, wy * 0.85, wz).remap01()
      val cavern = Curve.smoothstep(0.36, 0.62, cavernN)

      // Secondary pocket field for broader side chambers
      val chamberN = ctx.noise.get(noise.chamber3D).noise3D(wx * 1.10, wy * 0.90, wz * 1.10).remap01()
      val chamber = Curve.smoothstep(0.46, 0.72, chamberN)

      // Ridged corridor field: wide connective aisles between rooms
      val aisleRaw = ctx.noise.get(noise.aisle3D).noise3D(wx * 1.15, wy * 0.85, wz * 1.15)
      val aisleRidge = 1.0 - abs(aisleRaw)
      val aisle = Curve.smoothstep(0.52, 0.80, aisleRidge)

      // huge rooms first, aisles help connect them
      val cavity = max(cavern, max(chamber * 0.82, aisle * 0.68))
      if (cavity <= 0.01) return VolDensityStack.emptyStack()

      // Much looser fade
      val edgeFade = Curve.smoothstep(0.03, 0.22, suitability)

      // Stronger carve
      val carveStrength = (solidDensity + 2.2) * cavity * edgeFade

      // Keep shelves subtle or they refill too much
      val shelf = ctx.noise.get(noise.shelf3D).noise3D(x * 0.55, yy * 0.35, z * 0.55) * 2.8

      // Vertical coherent supports like fungal trunks / buttresses
      val pillarMask = ctx.noise.get(noise.pillar2D).noise2D(x * 0.85, z * 0.85).remap01()
      var pillar = Curve.smoothstep(0.76, 0.92, pillarMask)
      pillar *= Curve.smoothstep(0.18, 0.58, cavern + chamber) * edgeFade
      val pillarAdd = pillar * (solidDensity * 0.28 + 0.18)

      // Ceiling / floor chunkiness
      val canopyN = ctx.noise.get(noise.canopy3D).noise3D(x * 0.75, yy * 0.90, z * 0.75).remap01()
      val canopy = Curve.smoothstep(0.80, 0.94, canopyN) * 0.10 * edgeFade

      val baseVariation =
        ctx.noise.get(noise.detail3D).noise3D(x * 0.45, yy * 0.18, z * 0.45) * 0.03

      return VolDensityStack.volDensityStack(
        base = baseVariation,
        carve = carveStrength,
        add = (shelf * 0.18 * edgeFade) + pillarAdd + canopy,
        replaceMask = 0.0
      )
    }
  }

  override fun toBukkitBiome(): Biome = Biome.LUSH_CAVES
}
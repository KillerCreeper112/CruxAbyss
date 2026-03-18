package killercreepr.cruxworldgen.test.biome.volumetric

import killercreepr.cruxgeneration.util.CruxNoise
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiome
import killercreepr.cruxworldgen.api.biome.volumetric.VolumetricBiomeShape
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.context.GenerateContext
import killercreepr.cruxworldgen.api.context.MaterialContext
import killercreepr.cruxworldgen.api.context.volumetric.VolumeEnv
import killercreepr.cruxworldgen.api.density.DensityStack
import killercreepr.cruxworldgen.api.density.VolDensityStack
import killercreepr.cruxworldgen.api.feature.HeightFilter
import killercreepr.cruxworldgen.api.material.MaterialProvider
import killercreepr.cruxworldgen.api.noise.*
import killercreepr.cruxworldgen.api.signal.SignalWriter
import killercreepr.cruxworldgen.api.util.Curve.smoothstep01
import killercreepr.cruxworldgen.bukkit.biome.BukkitBiome
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockResolver
import org.bukkit.block.Biome
import kotlin.math.pow

class GlacialCaverns(
  val noise: Noise = DefaultNoise,
  val yRange: HeightFilter,
  val threshold: Double = 0.48,
  val sharpness: Double = 1.1
) : VolumetricBiome, Noised, BukkitBiome {

  interface Noise {
    val biomeMask3D: NoiseKey
    val cavern3D: NoiseKey
    val tunnel3D: NoiseKey
    val detail3D: NoiseKey
    val icicle3D: NoiseKey
    val warpX3D: NoiseKey
    val warpY3D: NoiseKey
    val warpZ3D: NoiseKey
    val biomeRegion2D: NoiseKey
    val shelf3D: NoiseKey
    val pillar3D: NoiseKey
  }

  object DefaultNoise : NoiseModule, Noise {
    override val biomeMask3D = object : NoiseKey { override val id = "biome3D.glacial_caverns.mask3D" }
    override val cavern3D   = object : NoiseKey { override val id = "biome3D.glacial_caverns.cavern3D" }
    override val tunnel3D   = object : NoiseKey { override val id = "biome3D.glacial_caverns.tunnel3D" }
    override val detail3D   = object : NoiseKey { override val id = "biome3D.glacial_caverns.detail3D" }
    override val icicle3D   = object : NoiseKey { override val id = "biome3D.glacial_caverns.icicle3D" }
    override val warpX3D    = object : NoiseKey { override val id = "biome3D.glacial_caverns.warpX3D" }
    override val warpY3D    = object : NoiseKey { override val id = "biome3D.glacial_caverns.warpY3D" }
    override val warpZ3D    = object : NoiseKey { override val id = "biome3D.glacial_caverns.warpZ3D" }
    override val biomeRegion2D    = object : NoiseKey { override val id = "biome3D.glacial_caverns.biome_region2D" }
    override val shelf3D    = object : NoiseKey { override val id = "biome3D.glacial_caverns.shelf3D" }
    override val pillar3D    = object : NoiseKey { override val id = "biome3D.glacial_caverns.pillar3D" }

    override fun install(bank: NoiseBank) {
      bank.register(biomeRegion2D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.001)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(1)
        }
      }
      bank.register(shelf3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0025)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }
      bank.register(pillar3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.002)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }
      bank.register(biomeMask3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0007)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(cavern3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0042)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(3)
        }
      }

      bank.register(tunnel3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0075)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(detail3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.011)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(icicle3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.010)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(warpX3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0050)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(warpY3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0050)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
          fractalType(CruxNoise.FractalType.FBm)
          fractalOctaves(2)
        }
      }

      bank.register(warpZ3D) { seed ->
        NoiseField.noiseField(seed) {
          frequency(0.0050)
          noiseType(CruxNoise.NoiseType.OpenSimplex2)
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
      return BukkitBlockResolver.INSTANCE.resolve("packed_ice")
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
    if (!yRange.isWithinRange(ctx, y)) return 0.0

    val depthBelowSurface = (-env.heightAboveSurface.toDouble()).coerceAtLeast(0.0)
    if (depthBelowSurface <= 0.0) return 0.0

    val preferredDepth = 42.0
    val halfDepthBand = 38.0

    val depthT = ((halfDepthBand - kotlin.math.abs(depthBelowSurface - preferredDepth)) / halfDepthBand)
      .coerceIn(0.0, 1.0)
    val depthMask = smoothstep01(depthT)
    if (depthMask <= 0.001) return 0.0

    val regionRaw = (ctx.noise.get(noise.biomeRegion2D).noise2D(worldX.toDouble(), worldZ.toDouble()) * 0.5 + 0.5)
    val regionT = ((regionRaw - 0.46) / (1.0 - 0.46)).coerceIn(0.0, 1.0)
    val regionMask = smoothstep01(regionT).pow(1.15)

    val detailRaw = (ctx.noise.get(noise.biomeMask3D).noise3D(worldX, y, worldZ) * 0.5 + 0.5)
    val detailT = ((detailRaw - 0.40) / (1.0 - 0.40)).coerceIn(0.0, 1.0)
    val detailMask = smoothstep01(detailT)

    val biomeMask = regionMask * (0.75 + 0.25 * detailMask)
    return (depthMask * biomeMask).coerceIn(0.0, 1.0)
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
      if (!yRange.isWithinRange(ctx, y)) return VolDensityStack.emptyStack()

      val depthBelowSurface = (-env.heightAboveSurface.toDouble()).coerceAtLeast(0.0)
      if (depthBelowSurface <= 0.0) return VolDensityStack.emptyStack()

      val terrainDensity = env.terrainDensity

      val centerDepth = 42.0
      val halfDepth = 38.0
      val bandT = ((halfDepth - kotlin.math.abs(depthBelowSurface - centerDepth)) / halfDepth).coerceIn(0.0, 1.0)
      val bandMask = smoothstep01(bandT)
      if (bandMask <= 0.001) return VolDensityStack.emptyStack()

      val regionRaw = (ctx.noise.get(noise.biomeRegion2D).noise2D(worldX.toDouble(), worldZ.toDouble()) * 0.5 + 0.5)
      val regionT = ((regionRaw - 0.46) / (1.0 - 0.46)).coerceIn(0.0, 1.0)
      val regionMask = smoothstep01(regionT).pow(1.15)

      val regionDetailRaw = (ctx.noise.get(noise.biomeMask3D).noise3D(worldX, y, worldZ) * 0.5 + 0.5)
      val regionDetailT = ((regionDetailRaw - 0.40) / (1.0 - 0.40)).coerceIn(0.0, 1.0)
      val regionDetailMask = smoothstep01(regionDetailT)

      val biomeWorldMask = bandMask * regionMask * (0.75 + 0.25 * regionDetailMask)
      if (biomeWorldMask <= 0.001) return VolDensityStack.emptyStack()

      val cavernN = ctx.noise.get(noise.cavern3D)
      val tunnelN = ctx.noise.get(noise.tunnel3D)
      val detailN = ctx.noise.get(noise.detail3D)
      val icicleN = ctx.noise.get(noise.icicle3D)
      val warpXN = ctx.noise.get(noise.warpX3D)
      val warpYN = ctx.noise.get(noise.warpY3D)
      val warpZN = ctx.noise.get(noise.warpZ3D)
      val shelfN = ctx.noise.get(noise.shelf3D)
      val pillarN = ctx.noise.get(noise.pillar3D)

      val warpAmpXZ = 16.0
      val warpAmpY = 9.0

      val wx = worldX + warpXN.noise3D(worldX, y, worldZ) * warpAmpXZ
      val wy = y + warpYN.noise3D(worldX + 211, y - 137, worldZ + 419) * warpAmpY
      val wz = worldZ + warpZN.noise3D(worldX - 373, y + 97, worldZ - 587) * warpAmpXZ

      val cavernRaw = (cavernN.noise3D(wx, wy * 0.72, wz) * 0.5 + 0.5).coerceIn(0.0, 1.0)
      val cavernT = ((cavernRaw - 0.53) / (1.0 - 0.53)).coerceIn(0.0, 1.0)
      val cavernMask = smoothstep01(cavernT).pow(1.15)

      val tunnelRaw = (tunnelN.noise3D(wx + 1400.0, wy * 1.05 - 700.0, wz - 1100.0) * 0.5 + 0.5).coerceIn(0.0, 1.0)
      val tunnelT = ((tunnelRaw - 0.58) / (1.0 - 0.58)).coerceIn(0.0, 1.0)
      val tunnelMask = smoothstep01(tunnelT).pow(1.8)

      val open01 = (0.80 * cavernMask + 0.20 * tunnelMask).coerceIn(0.0, 1.0)
      val edge01 = (open01 * (1.0 - open01) * 4.0).coerceIn(0.0, 1.0)

      val vertical01 = ((depthBelowSurface - centerDepth) / halfDepth).coerceIn(-1.0, 1.0)
      val floorBias = smoothstep01(((vertical01 + 1.0) * 0.5).coerceIn(0.0, 1.0))
      val ceilingBias = smoothstep01(((1.0 - vertical01) * 0.5).coerceIn(0.0, 1.0))

      val shelfRaw = (shelfN.noise3D(wx * 0.80, wy * 0.55, wz * 0.80) * 0.5 + 0.5).coerceIn(0.0, 1.0)
      val shelfT = ((shelfRaw - 0.56) / (1.0 - 0.56)).coerceIn(0.0, 1.0)
      val shelfMask = smoothstep01(shelfT).pow(1.6)

      val pillarRaw = (pillarN.noise3D(wx * 1.03, wy * 1.22, wz * 1.03) * 0.5 + 0.5).coerceIn(0.0, 1.0)
      val pillarT = ((pillarRaw - 0.66) / (1.0 - 0.66)).coerceIn(0.0, 1.0)
      val pillarMask = smoothstep01(pillarT).pow(2.4)

      val detailRaw = (detailN.noise3D(wx - 2100.0, wy * 0.85 + 333.0, wz + 1700.0) * 0.5 + 0.5).coerceIn(0.0, 1.0)
      val detailMul = (1.0 + (detailRaw - 0.5) * 0.45).coerceIn(0.75, 1.25)

      // --- internal terrain masses ---
      val floorShelfMask = shelfMask * floorBias * (0.45 + 0.55 * (1.0 - open01))
      val ceilingShelfMask = shelfMask * ceilingBias * (0.35 + 0.65 * edge01)
      val pillarShapeMask = pillarMask * (0.30 + 0.70 * edge01)

      val realmBase =
        floorShelfMask * 22.0 +
          ceilingShelfMask * 15.0 +
          pillarShapeMask * 20.0

      // --- carve out the large voids ---
      val solidMask = smoothstep01(((terrainDensity + 3.0) / 8.0).coerceIn(0.0, 1.0))
      val preserveMask = (0.55 * floorShelfMask + 0.35 * ceilingShelfMask + 0.65 * pillarShapeMask).coerceIn(0.0, 1.0)

      val carve =
        open01 *
          biomeWorldMask *
          solidMask *
          (terrainDensity + 8.0).coerceAtLeast(0.0) *
          (1.0 - 0.70 * preserveMask) *
          detailMul

      // --- shell + icicle decoration ---
      var add = edge01 * 12.0 * biomeWorldMask

      val icicleRaw = (icicleN.noise3D(wx * 1.04, wy * 1.32, wz * 1.04) * 0.5 + 0.5).coerceIn(0.0, 1.0)
      val icicleT = ((icicleRaw - 0.69) / (1.0 - 0.69)).coerceIn(0.0, 1.0)
      val icicleMask = smoothstep01(icicleT).pow(2.8)

      add += icicleMask * (0.25 + 0.75 * edge01) * (0.30 + 0.70 * ceilingBias) * 16.0 * biomeWorldMask

      val replaceMask =
        smoothstep01(((biomeWorldMask - 0.65) / 0.25).coerceIn(0.0, 1.0)) *
          open01

      return VolDensityStack.volDensityStack(
        base = realmBase * biomeWorldMask,
        add = add,
        carve = carve,
        replaceMask = replaceMask
      )
    }
  }

  override fun toBukkitBiome(): Biome = Biome.ICE_SPIKES
}
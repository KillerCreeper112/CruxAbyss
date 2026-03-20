package killercreepr.cruxworldgen.standard.decor

import killercreepr.cruxworldgen.api.block.BlockPicker
import killercreepr.cruxworldgen.api.context.LimitedRegion
import killercreepr.cruxworldgen.api.decor.Decoration
import killercreepr.cruxworldgen.api.decor.DecorationPass
import killercreepr.cruxworldgen.api.decor.Placement
import killercreepr.cruxworldgen.api.decor.PropPoint
import killercreepr.cruxworldgen.api.generation.BiomeBlendSample
import killercreepr.cruxworldgen.api.util.HashUtil
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

open class HollowSlimeTreeDecor(
  override val pass: DecorationPass = DecorationPass.SURFACE,

  val chancePerPoint: Double = 0.06,
  val chanceSalt: Long,

  val minAirAbove: Int = 14,
  val maxSlope01: Double = 0.55,

  val minHeight: Int = 9,
  val maxHeight: Int = 16,

  /** Outer radius at the bottom */
  val minBaseRadius: Int = 3,
  val maxBaseRadius: Int = 5,

  /** Outer radius near the very top */
  val minTopRadius: Int = 1,
  val maxTopRadius: Int = 2,

  /** Thickness of the trunk shell */
  val shellThickness: Int = 1,

  val log: BlockPicker,
  val slime: BlockPicker
) : Decoration {

  override fun shouldTry(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Boolean {
    val s = HashUtil.mixSeed(
      seed = region.ctx.worldContext.seed,
      x = point.worldX, y = 0, z = point.worldZ,
      salt = chanceSalt
    )
    return HashUtil.chance(s, chancePerPoint)
  }

  override fun findPlacement(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Placement? {
    val worldX = point.worldX
    val worldZ = point.worldZ
    val terrain2D = region.terrainSnapshot.terrain2D
    val queries = region.terrainQueries
    val bounds = region.regionBounds

    val surfaceY = terrain2D.surfaceY(worldX, worldZ)
    val baseY = surfaceY + 1

    if (!region.isInRegion(worldX, baseY, worldZ)) return null
    if (!queries.isSolid(worldX, surfaceY, worldZ)) return null
    if (terrain2D.isOceanColumn(worldX, worldZ)) return null
    if (queries.slope01(worldX, worldZ) > maxSlope01) return null

    val height = HashUtil.chooseInt(point.seed xor 0x54A91B77L, minHeight, maxHeight)
    val baseRadius = HashUtil.chooseInt(point.seed xor 0x11C2E401L, minBaseRadius, maxBaseRadius)
    val topRadius = HashUtil.chooseInt(point.seed xor 0x7F2A98D4L, minTopRadius, maxTopRadius)
      .coerceAtMost(baseRadius - 1)
      .coerceAtLeast(1)

    val maxOuterRadius = max(baseRadius, topRadius)
    val topY = baseY + height - 1
    if (topY > bounds.maxY) return null

    val airAbove = queries.airBlocksAbove(worldX, surfaceY, worldZ, maxCount = minAirAbove)
    if (airAbove < minAirAbove) return null

    // Make sure the footprint area is mostly placeable
    for (dx in -maxOuterRadius..maxOuterRadius) {
      for (dz in -maxOuterRadius..maxOuterRadius) {
        val distSq = dx * dx + dz * dz
        if (distSq > maxOuterRadius * maxOuterRadius) continue

        val x = worldX + dx
        val z = worldZ + dz

        if (!region.isInRegion(x, baseY, z)) return null

        // Require the ground under the trunk footprint to be solid-ish
        if (!queries.isSolid(x, surfaceY, z) && distSq <= (maxOuterRadius - 1) * (maxOuterRadius - 1)) {
          return null
        }
      }
    }

    return Placed(
      worldX = worldX,
      worldZ = worldZ,
      baseY = baseY,
      height = height,
      baseRadius = baseRadius,
      topRadius = topRadius,
      seed = point.seed
    )
  }

  override fun place(
    region: LimitedRegion,
    placement: Placement,
    biomeBlend: BiomeBlendSample
  ) {
    val p = placement as Placed
    val queries = region.terrainQueries
    val bounds = region.regionBounds

    val topY = minOf(bounds.maxY, p.baseY + p.height - 1)

    for (y in p.baseY..topY) {
      val t = (y - p.baseY).toDouble() / max(1, p.height - 1).toDouble()

      // Smooth taper from wide base to thin top
      val outerRadius = lerpRadius(p.baseRadius, p.topRadius, smoothstep01(t))
      val innerRadius = max(0.0, outerRadius - shellThickness.toDouble() - 0.15)

      val ceilRadius = ceil(outerRadius + 1.25).toInt()

      // Small deterministic wobble so each layer isn't a perfect circle
      val layerSeed = HashUtil.mixSeed(p.seed, 0, y, 0, 0x2E8F1A4DL)
      val offsetX = ((layerSeed ushr 0).toInt() and 1) - ((layerSeed ushr 1).toInt() and 1)
      val offsetZ = ((layerSeed ushr 2).toInt() and 1) - ((layerSeed ushr 3).toInt() and 1)

      for (dx in -ceilRadius..ceilRadius) {
        for (dz in -ceilRadius..ceilRadius) {
          val x = p.worldX + dx + offsetX
          val z = p.worldZ + dz + offsetZ

          if (y < bounds.minY || y > bounds.maxY) continue
          if (!region.isInRegion(x, y, z)) continue

          val dist = radialDistance(dx.toDouble(), dz.toDouble(), p.seed, y)
          if (dist > outerRadius) continue

          val replaceable = queries.isReplaceable(x, y, z)
          if (!replaceable) continue

          if (dist >= innerRadius) {
            val logBlock = log.pickBlock(region, x, y, z)
            if (logBlock != null) {
              region.setBlock(x, y, z, logBlock)
            }
          } else {
            val slimeBlock = slime.pickBlock(region, x, y, z)
            if (slimeBlock != null) {
              region.setBlock(x, y, z, slimeBlock)
            }
          }
        }
      }
    }
  }

  protected fun lerpRadius(baseRadius: Int, topRadius: Int, t: Double): Double {
    return baseRadius + (topRadius - baseRadius) * t
  }

  protected fun smoothstep01(x: Double): Double {
    val t = x.coerceIn(0.0, 1.0)
    return t * t * (3.0 - 2.0 * t)
  }

  /**
   * Slight circle breakup so the trunk feels more organic.
   */
  protected fun radialDistance(dx: Double, dz: Double, seed: Long, y: Int): Double {
    val jitterSeed = HashUtil.mixSeed(seed, dx.roundToInt(), y, dz.roundToInt(), 0x6A09E667L)
    val jitter = (((jitterSeed ushr 8).toInt() and 1023) / 1023.0 - 0.5) * 0.35
    val base = kotlin.math.sqrt(dx * dx + dz * dz)
    return base + jitter
  }

  data class Placed(
    val worldX: Int,
    val worldZ: Int,
    val baseY: Int,
    val height: Int,
    val baseRadius: Int,
    val topRadius: Int,
    val seed: Long
  ) : Placement
}
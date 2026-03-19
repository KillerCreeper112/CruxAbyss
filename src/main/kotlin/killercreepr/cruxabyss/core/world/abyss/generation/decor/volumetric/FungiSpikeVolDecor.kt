package killercreepr.cruxabyss.core.world.abyss.generation.decor.volumetric

import killercreepr.crux.core.util.CruxMath
import killercreepr.cruxworldgen.api.block.BlockGetter
import killercreepr.cruxworldgen.api.context.LimitedRegion
import killercreepr.cruxworldgen.api.decor.Decoration
import killercreepr.cruxworldgen.api.decor.DecorationPass
import killercreepr.cruxworldgen.api.decor.Placement
import killercreepr.cruxworldgen.api.decor.PropPoint
import killercreepr.cruxworldgen.api.generation.BiomeBlendSample
import killercreepr.cruxworldgen.api.noise.NoiseKey
import killercreepr.cruxworldgen.api.util.HashUtil
import killercreepr.cruxworldgen.api.util.HashUtil.chance
import killercreepr.cruxworldgen.api.util.HashUtil.mixSeed
import kotlin.math.cos
import kotlin.math.sin

class FungiSpikeVolDecor(
  val chancePerPoint: Double = 0.5,

  val sizeMin: Float = 1f,
  val sizeMax: Float = 3f,
  val taperMin: Float = 0.1f,
  val taperMax: Float = 0.5f,

  val pitchMin: Float = 10f,
  val pitchMax: Float = 90f,

  /** How strongly noise warps the sphere surface. */
  val noiseStrength: Double = 0.7,
  val maxAirAbove: Int = 5,

  val blocks: List<BlockGetter>,

  val noise: NoiseKey,
  val chanceSalt: Long = CruxMath.random().nextLong(),
  override val pass: DecorationPass = DecorationPass.SURFACE
) : Decoration {
  override fun shouldTry(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Boolean {
    val s = mixSeed(region.ctx.worldContext.seed, point.worldX, point.worldZ, chanceSalt)
    return chance(s, chancePerPoint)
  }

  override fun findPlacement(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Placement? {
    val queries = region.terrainQueries
    val x = point.worldX
    val z = point.worldZ
    val y = region.terrainSnapshot.terrain2D.surfaceY(x, z)
    if (!region.isInRegion(x, y, z)) return null
    // Require solid ground to anchor from
    if (!queries.isSolid(x, y, z)) return null
    if (!queries.isEmpty(x, y + 1, z)) return null
    if(queries.airBlocksAbove(x, y, z) < maxAirAbove) return null

    return Placed(x, y + 1, z, point.seed)
  }

  override fun place(
    region: LimitedRegion,
    placement: Placement,
    biomeBlend: BiomeBlendSample
  ) {
    val p = placement as Placed

    var rng = mixSeed(p.seed, chanceSalt)

    val block = blocks[((rng and Long.MAX_VALUE) % blocks.size).toInt()]
    rng = mixSeed(rng, 3L)
    val size = HashUtil.chooseFloat(rng, sizeMin, sizeMax)
    rng = mixSeed(rng, 7L)
    val taper = HashUtil.chooseFloat(rng, taperMin, taperMax)
    rng = mixSeed(rng, 11L)
    val pitch = HashUtil.chooseFloat(rng, pitchMin, pitchMax).toDouble()
    rng = mixSeed(rng, 13L)
    val yaw = HashUtil.chooseFloat(rng, -180f, 180f).toDouble()

    // Direction vector from pitch/yaw
    val pitchRad = Math.toRadians(pitch)
    val yawRad = Math.toRadians(yaw)
    val cosPitch = cos(pitchRad)
    val dx = cos(yawRad) * cosPitch
    val dy = sin(pitchRad)
    val dz = sin(yawRad) * cosPitch

    val noiseSource = region.ctx.noise.get(noise)

    var currentSize = size
    var cx = p.worldX.toDouble()
    var cy = p.worldY.toDouble()
    var cz = p.worldZ.toDouble()

    while (currentSize > 0f) {
      // Step sphere center along direction
      cx += dx; cy += dy; cz += dz

      val iSize = currentSize.toInt() + 1

      // Iterate integer blocks in bounding box — far fewer iterations than
      // the original float-stepped nested loops
      for (ix in -iSize..iSize) {
        for (iy in -iSize..iSize) {
          for (iz in -iSize..iSize) {
            val bx = (cx + ix).toInt()
            val by = (cy + iy).toInt()
            val bz = (cz + iz).toInt()

            if (!region.isInRegion(bx, by, bz)) continue
            if (!region.terrainQueries.isEmpty(bx, by, bz)) continue

            // Local offset from sphere center
            val lx = bx - cx
            val ly = by - cy
            val lz = bz - cz

            val s2 = currentSize * currentSize
            val equationResult = (lx * lx + ly * ly + lz * lz) / s2

            val noiseVal = noiseSource.noise3D(bx, by, bz)
            if (equationResult <= 1.0 + noiseStrength * noiseVal) {
              val blockData = block.getBlock(region, region.ctx.random, bx, by, bz) ?: continue
              region.setBlock(bx, by, bz, blockData)
            }
          }
        }
      }

      currentSize -= taper
    }
  }

  data class Placed(
    val worldX: Int,
    val worldY: Int,
    val worldZ: Int,
    val seed: Long
  ) : Placement
}
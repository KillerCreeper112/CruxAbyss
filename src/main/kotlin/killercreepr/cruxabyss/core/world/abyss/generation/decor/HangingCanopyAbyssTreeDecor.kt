package killercreepr.cruxabyss.core.world.abyss.generation.decor

import killercreepr.crux.api.data.Holder
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.context.LimitedRegion
import killercreepr.cruxworldgen.api.decor.Decoration
import killercreepr.cruxworldgen.api.decor.DecorationPass
import killercreepr.cruxworldgen.api.decor.Placement
import killercreepr.cruxworldgen.api.decor.PropPoint
import killercreepr.cruxworldgen.api.generation.BiomeBlendSample
import killercreepr.cruxworldgen.api.util.HashUtil.chance
import killercreepr.cruxworldgen.api.util.HashUtil.chooseInt
import killercreepr.cruxworldgen.api.util.HashUtil.mixSeed
import killercreepr.cruxworldgen.bukkit.block.picker.AxisBlockPicker
import org.bukkit.Axis
import kotlin.math.abs
import kotlin.math.max

class HangingCanopyAbyssTreeDecor(
  override val pass: DecorationPass = DecorationPass.SURFACE,

  val chancePerPoint: Double = 0.06,
  val minAirAbove: Int = 13,
  val maxSlope01: Double = 0.8,

  val minHeight: Int = 7,
  val maxHeight: Int = 11,

  val minCurtains: Int = 4,
  val maxCurtains: Int = 8,

  val logPicker: AxisBlockPicker,
  val leafPicker: Holder<BlockData>,
) : Decoration {

  override fun shouldTry(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Boolean {
    val s = mixSeed(region.ctx.worldContext.seed, point.worldX, 0, point.worldZ, salt = 0x7711AA55)
    return chance(s, chancePerPoint)
  }

  override fun findPlacement(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Placement? {
    val x0 = point.worldX
    val z0 = point.worldZ
    val t2d = region.terrainSnapshot.terrain2D
    val q = region.terrainQueries

    val surfaceY = t2d.surfaceY(x0, z0)
    val baseY = surfaceY + 1

    if (!region.isInRegion(x0, baseY, z0)) return null
    if (!q.isSolid(x0, surfaceY, z0)) return null
    if (t2d.isOceanColumn(x0, z0)) return null
    if (q.slope01(x0, z0) > maxSlope01) return null
    if (q.airBlocksAbove(x0, surfaceY, z0, minAirAbove) < minAirAbove) return null

    val height = chooseInt(point.seed xor 0x1001L, minHeight, maxHeight)
    val topY = baseY + height - 1

    // Slight lean
    val leanSeed = mixSeed(region.ctx.worldContext.seed, x0, baseY, z0, salt = point.seed xor 0x5511)
    val leanDir = ((leanSeed ushr 9) and 3L).toInt()
    val (leanX, leanZ) = when (leanDir) {
      0 -> 1 to 0
      1 -> -1 to 0
      2 -> 0 to 1
      else -> 0 to -1
    }

    val trunk = ArrayList<Vec3i>()
    for (dy in 0 until height) {
      val shift = if (dy > height / 2) 1 else 0
      val x = x0 + leanX * shift
      val z = z0 + leanZ * shift
      val y = baseY + dy
      trunk += Vec3i(x, y, z)
    }

    // 2x2-ish buttressed base
    val baseColumns = listOf(
      Vec3i(x0, baseY, z0),
      Vec3i(x0 + 1, baseY, z0),
      Vec3i(x0, baseY, z0 + 1),
      Vec3i(x0 + 1, baseY, z0 + 1)
    )

    // Crown center slightly above and offset from trunk top
    val crownCenter = Vec3i(
      trunk.last().x,
      topY + 1,
      trunk.last().z
    )

    // Small upper forks like the screenshot supports
    val forks = buildForks(
      worldSeed = region.ctx.worldContext.seed,
      seed = point.seed,
      top = trunk.last()
    )

    val curtainCount = chooseInt(point.seed xor 0x2222L, minCurtains, maxCurtains)
    val curtains = ArrayList<CurtainPlan>()

    for (i in 0 until curtainCount) {
      val dir = i % 8
      val (dx, dz) = when (dir) {
        0 -> 1 to 0
        1 -> -1 to 0
        2 -> 0 to 1
        3 -> 0 to -1
        4 -> 1 to 1
        5 -> 1 to -1
        6 -> -1 to 1
        else -> -1 to -1
      }

      val startRadius = chooseInt(point.seed xor (0x3000L + i), 2, 4)
      val sx = crownCenter.x + dx * startRadius
      val sz = crownCenter.z + dz * startRadius
      val sy = crownCenter.y + chooseInt(point.seed xor (0x4000L + i), -1, 1)

      val drop = chooseInt(point.seed xor (0x5000L + i), 3, 7)
      val width = chooseInt(point.seed xor (0x6000L + i), 1, 2)

      curtains += CurtainPlan(
        startX = sx,
        startY = sy,
        startZ = sz,
        dx = dx,
        dz = dz,
        drop = drop,
        width = width
      )
    }

    // Validate trunk, base, forks, and main crown bounds
    for (p in trunk) {
      if (!region.isInRegion(p.x, p.y, p.z)) return null
      if (!q.isReplaceable(p.x, p.y, p.z)) return null
    }

    for (p in baseColumns) {
      if (!region.isInRegion(p.x, p.y, p.z)) return null
      if (!q.isReplaceable(p.x, p.y, p.z)) return null
    }

    for (fork in forks) {
      for (p in fork.path) {
        if (!region.isInRegion(p.x, p.y, p.z)) return null
        if (!q.isReplaceable(p.x, p.y, p.z)) return null
      }
    }

    if (!validateBlob(region, crownCenter.x, crownCenter.y, crownCenter.z, 4)) return null

    return Placed(
      baseX = x0,
      baseY = baseY,
      baseZ = z0,
      trunk = trunk,
      baseColumns = baseColumns,
      forks = forks,
      crownCenter = crownCenter,
      curtains = curtains,
      seed = point.seed
    )
  }

  override fun place(
    region: LimitedRegion,
    placement: Placement,
    biomeBlend: BiomeBlendSample
  ) {
    val p = placement as Placed
    val q = region.terrainQueries

    // Thick lower base
    for (b in p.baseColumns) {
      if (region.isInRegion(b.x, b.y, b.z) && q.isReplaceable(b.x, b.y, b.z)) {
        val block = logPicker.pickBlock(region, b.x, b.y, b.z, Axis.Y) ?: return
        region.setBlock(b.x, b.y, b.z, block)
      }
    }

    // Main trunk
    for (i in p.trunk.indices) {
      val cur = p.trunk[i]
      if (region.isInRegion(cur.x, cur.y, cur.z) && q.isReplaceable(cur.x, cur.y, cur.z)) {
        val block = logPicker.pickBlock(region, cur.x, cur.y, cur.z, Axis.Y) ?: return
        region.setBlock(cur.x, cur.y, cur.z, block)
      }

      // Add extra thickness low on the trunk
      if (i < max(2, p.trunk.size / 3)) {
        val extra = listOf(
          Vec3i(cur.x + 1, cur.y, cur.z),
          Vec3i(cur.x, cur.y, cur.z + 1)
        )
        for (e in extra) {
          if (region.isInRegion(e.x, e.y, e.z) && q.isReplaceable(e.x, e.y, e.z)) {
            val block = logPicker.pickBlock(region, e.x, e.y, e.z, Axis.Y) ?: return
            region.setBlock(e.x, e.y, e.z, block)
          }
        }
      }
    }

    // Upper forks
    for (fork in p.forks) {
      for (i in fork.path.indices) {
        val prev = if (i == 0) null else fork.path[i - 1]
        val cur = fork.path[i]
        val axis = when {
          prev == null -> Axis.Y
          cur.x != prev.x -> Axis.X
          cur.z != prev.z -> Axis.Z
          else -> Axis.Y
        }

        if (region.isInRegion(cur.x, cur.y, cur.z) && q.isReplaceable(cur.x, cur.y, cur.z)) {
          val block = logPicker.pickBlock(region, cur.x, cur.y, cur.z, axis) ?: return
          region.setBlock(cur.x, cur.y, cur.z, block)
        }
      }
    }

    // Dense crown
    placeHangingBlob(
      region = region,
      cx = p.crownCenter.x,
      cy = p.crownCenter.y,
      cz = p.crownCenter.z,
      radiusXZ = 4,
      radiusYUp = 2,
      radiusYDown = 3,
      seed = p.seed xor 0xABCDEF
    )

    // Secondary side masses so the top is uneven
    val sideOffsets = listOf(
      Triple(2, 0, 0),
      Triple(-2, 0, 1),
      Triple(1, -1, -2)
    )
    for ((ox, oy, oz) in sideOffsets) {
      val s = mixSeed(region.ctx.worldContext.seed, p.crownCenter.x + ox, p.crownCenter.y + oy, p.crownCenter.z + oz, salt = p.seed)
      if (chance(s, 0.7)) {
        placeHangingBlob(
          region = region,
          cx = p.crownCenter.x + ox,
          cy = p.crownCenter.y + oy,
          cz = p.crownCenter.z + oz,
          radiusXZ = 2,
          radiusYUp = 1,
          radiusYDown = 3,
          seed = s
        )
      }
    }

    // Leaf curtains
    for (curtain in p.curtains) {
      placeCurtain(region, curtain, p.seed)
    }

    // Hanging strands under crown
    for (ox in -3..3) {
      for (oz in -3..3) {
        if (abs(ox) + abs(oz) > 4) continue
        val sx = p.crownCenter.x + ox
        val sz = p.crownCenter.z + oz
        val sy = p.crownCenter.y - 1
        val s = mixSeed(region.ctx.worldContext.seed, sx, sy, sz, salt = p.seed xor 0x7777)
        if (chance(s, 0.28)) {
          val len = ((s ushr 8) and 3L).toInt() + 2
          for (i in 0 until len) {
            val y = sy - i
            if (!region.isInRegion(sx, y, sz)) break
            if (!q.isReplaceable(sx, y, sz)) break
            region.setBlock(sx, y, sz, leafPicker.value())
          }
        }
      }
    }
  }

  private fun buildForks(
    worldSeed: Long,
    seed: Long,
    top: Vec3i
  ): List<ForkPlan> {
    val out = ArrayList<ForkPlan>()
    val dirs = listOf(
      1 to 0,
      -1 to 0,
      0 to 1,
      0 to -1
    )

    for (i in 0 until 3) {
      val d = dirs[((seed ushr (i * 2)) and 3L).toInt()]
      val len = 2 + ((seed ushr (10 + i)) and 1L).toInt()
      val path = ArrayList<Vec3i>()

      var x = top.x
      var y = top.y
      var z = top.z

      for (j in 0 until len) {
        x += d.first
        z += d.second
        if (j > 0 || i % 2 == 0) y += 1
        path += Vec3i(x, y, z)
      }

      out += ForkPlan(path)
    }

    return out
  }

  private fun placeCurtain(
    region: LimitedRegion,
    curtain: CurtainPlan,
    seed: Long
  ) {
    val q = region.terrainQueries

    for (wx in -curtain.width..curtain.width) {
      for (wz in -curtain.width..curtain.width) {
        val edge = abs(wx) + abs(wz)
        if (edge > curtain.width + 1) continue

        val sx = curtain.startX + wx
        val sy = curtain.startY
        val sz = curtain.startZ + wz

        val s = mixSeed(region.ctx.worldContext.seed, sx, sy, sz, salt = seed xor 0x9191)
        val realDrop = curtain.drop - (((s ushr 6) and 1L).toInt())

        for (i in 0 until realDrop) {
          val x = sx + (curtain.dx * i) / 3
          val y = sy - i
          val z = sz + (curtain.dz * i) / 3

          if (!region.isInRegion(x, y, z)) break
          if (!q.isReplaceable(x, y, z)) break

          val placeChance = when {
            i < 2 -> 0.95
            edge == 0 -> 0.85
            else -> 0.62
          }

          val ps = mixSeed(region.ctx.worldContext.seed, x, y, z, salt = s)
          if (chance(ps, placeChance)) {
            region.setBlock(x, y, z, leafPicker.value())
          }
        }
      }
    }
  }

  private fun validateBlob(
    region: LimitedRegion,
    cx: Int,
    cy: Int,
    cz: Int,
    radius: Int
  ): Boolean {
    for (oy in -radius..radius) {
      for (ox in -radius..radius) {
        for (oz in -radius..radius) {
          if (abs(ox) + abs(oz) + abs(oy) > radius + 2) continue
          if (!region.isInRegion(cx + ox, cy + oy, cz + oz)) return false
        }
      }
    }
    return true
  }

  private fun placeHangingBlob(
    region: LimitedRegion,
    cx: Int,
    cy: Int,
    cz: Int,
    radiusXZ: Int,
    radiusYUp: Int,
    radiusYDown: Int,
    seed: Long
  ) {
    val q = region.terrainQueries

    for (oy in -radiusYDown..radiusYUp) {
      for (ox in -radiusXZ..radiusXZ) {
        for (oz in -radiusXZ..radiusXZ) {
          val x = cx + ox
          val y = cy + oy
          val z = cz + oz

          if (!region.isInRegion(x, y, z)) continue
          if (!q.isReplaceable(x, y, z)) continue

          val radial = abs(ox) + abs(oz)
          val verticalAllowance = if (oy <= 0) radiusXZ + 1 else radiusXZ - 1
          if (radial + abs(oy) > verticalAllowance + 1) continue

          val s = mixSeed(region.ctx.worldContext.seed, x, y, z, salt = seed)
          val p = when {
            oy > 0 -> 0.72
            oy == 0 -> 0.9
            oy >= -2 -> 0.84
            else -> 0.65
          }

          if (chance(s, p)) {
            region.setBlock(x, y, z, leafPicker.value())
          }
        }
      }
    }
  }

  data class Vec3i(val x: Int, val y: Int, val z: Int)

  data class ForkPlan(
    val path: List<Vec3i>
  )

  data class CurtainPlan(
    val startX: Int,
    val startY: Int,
    val startZ: Int,
    val dx: Int,
    val dz: Int,
    val drop: Int,
    val width: Int
  )

  data class Placed(
    val baseX: Int,
    val baseY: Int,
    val baseZ: Int,
    val trunk: List<Vec3i>,
    val baseColumns: List<Vec3i>,
    val forks: List<ForkPlan>,
    val crownCenter: Vec3i,
    val curtains: List<CurtainPlan>,
    val seed: Long
  ) : Placement
}
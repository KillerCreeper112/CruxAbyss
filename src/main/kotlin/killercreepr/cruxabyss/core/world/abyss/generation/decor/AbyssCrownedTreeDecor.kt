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

class AbyssCrownedTreeDecor(
  override val pass: DecorationPass = DecorationPass.SURFACE,

  val chancePerPoint: Double = 0.085,
  val minAirAbove: Int = 12,
  val maxSlope01: Double = 0.82,

  val minHeight: Int = 8,
  val maxHeight: Int = 14,

  val minBendEvery: Int = 2,
  val maxBendEvery: Int = 4,
  val maxTotalOffset: Int = 3,

  val minBranches: Int = 2,
  val maxBranches: Int = 4,

  val logPicker: AxisBlockPicker,
  val leafPicker: Holder<BlockData>,
) : Decoration {

  override fun shouldTry(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Boolean {
    val s = mixSeed(region.ctx.worldContext.seed, point.worldX, 0, point.worldZ, salt = 32894892L)
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

    val airAbove = q.airBlocksAbove(x0, surfaceY, z0, maxCount = minAirAbove)
    if (airAbove < minAirAbove) return null

    val height = chooseInt(point.seed xor 0x51A6E1L, minHeight, maxHeight)
    val bendEvery = chooseInt(point.seed xor 0x22BE, minBendEvery, maxBendEvery)
    val thickBaseHeight = max(2, height / 3)

    val trunk = simulateTrunk(
      worldSeed = region.ctx.worldContext.seed,
      seed = point.seed,
      x0 = x0,
      z0 = z0,
      baseY = baseY,
      height = height,
      bendEvery = bendEvery,
      maxTotalOffset = maxTotalOffset
    )

    if (trunk.isEmpty()) return null

    // Validate trunk space
    for (i in trunk.indices) {
      val p = trunk[i]
      if (!region.isInRegion(p.x, p.y, p.z)) return null
      if (!q.isReplaceable(p.x, p.y, p.z)) return null

      // Slightly thicker lower trunk, like the screenshot
      if (i < thickBaseHeight) {
        for ((ox, oz) in listOf(1 to 0, 0 to 1)) {
          val tx = p.x + ox
          val tz = p.z + oz
          if (!region.isInRegion(tx, p.y, tz)) return null
          if (!q.isReplaceable(tx, p.y, tz)) return null
        }
      }
    }

    val branchCount = chooseInt(point.seed xor 0xB24, minBranches, maxBranches)
    val branches = ArrayList<BranchPlan>(branchCount)

    for (i in 0 until branchCount) {
      val trunkIndex = chooseInt(
        point.seed xor (0x7000L + i.toLong()),
        max(2, height / 2),
        max(2, height - 2)
      )
      val trunkPoint = trunk[trunkIndex]

      val dirSeed = mixSeed(region.ctx.worldContext.seed, trunkPoint.x, trunkPoint.y, trunkPoint.z, salt = point.seed xor i.toLong())
      val dir = ((dirSeed ushr 28) and 3L).toInt()

      val (dx, dz) = when (dir) {
        0 -> 1 to 0
        1 -> -1 to 0
        2 -> 0 to 1
        else -> 0 to -1
      }

      val length = chooseInt(point.seed xor (0x3300L + i.toLong()), 2, 4)

      val path = simulateBranch(
        worldSeed = region.ctx.worldContext.seed,
        seed = point.seed xor i.toLong(),
        start = trunkPoint,
        dx = dx,
        dz = dz,
        length = length
      )

      var ok = true
      for (bp in path) {
        if (!region.isInRegion(bp.x, bp.y, bp.z) || !q.isReplaceable(bp.x, bp.y, bp.z)) {
          ok = false
          break
        }
      }
      if (!ok) continue

      val tip = path.last()
      if (!validateLeafBlob(region, tip.x, tip.y, tip.z, radius = 2)) continue

      branches += BranchPlan(path, tip.x, tip.y, tip.z, 2)
    }

    val top = trunk.last()
    if (!validateLeafBlob(region, top.x, top.y + 1, top.z, radius = 3)) return null

    return Placed(
      baseX = x0,
      baseZ = z0,
      baseY = baseY,
      height = height,
      thickBaseHeight = thickBaseHeight,
      trunk = trunk,
      branches = branches,
      crownX = top.x,
      crownY = top.y + 1,
      crownZ = top.z,
      crownRadius = 3,
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

    // Trunk
    for (i in p.trunk.indices) {
      val at = p.trunk[i]
      if (region.isInRegion(at.x, at.y, at.z) && q.isReplaceable(at.x, at.y, at.z)) {
        val block = logPicker.pickBlock(region, at.x, at.y, at.z, Axis.Y) ?: return
        region.setBlock(at.x, at.y, at.z, block)
      }

      // Thicker lower section
      if (i < p.thickBaseHeight) {
        for ((ox, oz) in listOf(1 to 0, 0 to 1)) {
          val tx = at.x + ox
          val tz = at.z + oz
          if (region.isInRegion(tx, at.y, tz) && q.isReplaceable(tx, at.y, tz)) {
            val block = logPicker.pickBlock(region, tx, at.y, tz, Axis.Y) ?: return
            region.setBlock(tx, at.y, tz, block)
          }
        }
      }
    }

    // Branches
    for (branch in p.branches) {
      for (i in branch.path.indices) {
        val prev = if (i == 0) null else branch.path[i - 1]
        val cur = branch.path[i]
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

      placeLeafBlob(
        region = region,
        cx = branch.tipX,
        cy = branch.tipY,
        cz = branch.tipZ,
        radius = branch.leafRadius,
        seed = p.seed xor (branch.tipX.toLong() shl 16) xor branch.tipZ.toLong()
      )
    }

    // Main crown
    placeLeafBlob(
      region = region,
      cx = p.crownX,
      cy = p.crownY,
      cz = p.crownZ,
      radius = p.crownRadius,
      seed = p.seed xor 0xC
    )

    // Extra offset blob so the canopy feels irregular instead of spherical
    val sideSeed = mixSeed(region.ctx.worldContext.seed, p.crownX, p.crownY, p.crownZ, salt = p.seed xor 0x51DE)
    val sideDir = ((sideSeed ushr 19) and 3L).toInt()
    val (sx, sz) = when (sideDir) {
      0 -> 1 to 0
      1 -> -1 to 0
      2 -> 0 to 1
      else -> 0 to -1
    }

    placeLeafBlob(
      region = region,
      cx = p.crownX + sx,
      cy = p.crownY,
      cz = p.crownZ + sz,
      radius = 2,
      seed = p.seed xor 0x51DE51DE
    )

    // Sparse leaves clinging to upper trunk
    for (i in max(0, p.height / 2) until p.trunk.size) {
      val t = p.trunk[i]
      for ((ox, oy, oz) in listOf(
        Triple(1, 0, 0), Triple(-1, 0, 0),
        Triple(0, 0, 1), Triple(0, 0, -1),
        Triple(0, 1, 0)
      )) {
        val lx = t.x + ox
        val ly = t.y + oy
        val lz = t.z + oz
        val s = mixSeed(region.ctx.worldContext.seed, lx, ly, lz, salt = p.seed xor i.toLong())
        if (chance(s, 0.18) && region.isInRegion(lx, ly, lz) && q.isReplaceable(lx, ly, lz)) {
          region.setBlock(lx, ly, lz, leafPicker.value())
        }
      }
    }
  }

  private fun simulateTrunk(
    worldSeed: Long,
    seed: Long,
    x0: Int,
    z0: Int,
    baseY: Int,
    height: Int,
    bendEvery: Int,
    maxTotalOffset: Int
  ): List<Vec3i> {
    val out = ArrayList<Vec3i>(height)

    var x = x0
    var z = z0
    var offX = 0
    var offZ = 0

    // Prefer one dominant bend direction so the tree has a silhouette
    val dirSeed = mixSeed(worldSeed, x0, baseY, z0, salt = seed xor 0xD1)
    val primaryDir = ((dirSeed ushr 7) and 3L).toInt()
    val (pdx, pdz) = when (primaryDir) {
      0 -> 1 to 0
      1 -> -1 to 0
      2 -> 0 to 1
      else -> 0 to -1
    }

    for (dy in 0 until height) {
      val y = baseY + dy
      out += Vec3i(x, y, z)

      if (dy > 0 && dy < height - 2 && dy % bendEvery == 0) {
        val r = mixSeed(worldSeed, x, y, z, salt = seed xor dy.toLong())
        val usePrimary = ((r ushr 11) and 3L) != 0L // 75% primary direction
        val (ddx, ddz) = if (usePrimary) {
          pdx to pdz
        } else {
          when (((r ushr 14) and 3L).toInt()) {
            0 -> 1 to 0
            1 -> -1 to 0
            2 -> 0 to 1
            else -> 0 to -1
          }
        }

        if (abs(offX + ddx) <= maxTotalOffset && abs(offZ + ddz) <= maxTotalOffset) {
          offX += ddx
          offZ += ddz
          x = x0 + offX
          z = z0 + offZ
        }
      }
    }

    return out
  }

  private fun simulateBranch(
    worldSeed: Long,
    seed: Long,
    start: Vec3i,
    dx: Int,
    dz: Int,
    length: Int
  ): List<Vec3i> {
    val out = ArrayList<Vec3i>(length)
    var x = start.x
    var y = start.y
    var z = start.z

    for (i in 0 until length) {
      x += dx
      z += dz

      val r = mixSeed(worldSeed, x, y, z, salt = seed xor i.toLong())
      if (((r ushr 9) and 1L) == 0L) y += 1 // gently rises sometimes

      out += Vec3i(x, y, z)
    }

    return out
  }

  private fun validateLeafBlob(
    region: LimitedRegion,
    cx: Int,
    cy: Int,
    cz: Int,
    radius: Int
  ): Boolean {
    for (oy in -radius..radius) {
      for (ox in -radius..radius) {
        for (oz in -radius..radius) {
          val dist = abs(ox) + abs(oy) + abs(oz)
          if (dist > radius + 1) continue
          val x = cx + ox
          val y = cy + oy
          val z = cz + oz
          if (!region.isInRegion(x, y, z)) return false
        }
      }
    }
    return true
  }

  private fun placeLeafBlob(
    region: LimitedRegion,
    cx: Int,
    cy: Int,
    cz: Int,
    radius: Int,
    seed: Long
  ) {
    val q = region.terrainQueries

    for (oy in -radius..radius) {
      for (ox in -radius..radius) {
        for (oz in -radius..radius) {
          val ax = abs(ox)
          val ay = abs(oy)
          val az = abs(oz)

          // Rounded diamond-ish blob with noise, looks more organic in voxel form
          val shell = ax + az + ay
          if (shell > radius + 1) continue
          if (ay == radius && ax + az > 1) continue

          val x = cx + ox
          val y = cy + oy
          val z = cz + oz
          if (!region.isInRegion(x, y, z)) continue
          if (!q.isReplaceable(x, y, z)) continue

          val s = mixSeed(region.ctx.worldContext.seed, x, y, z, salt = seed)
          val placeChance = when {
            shell <= radius - 1 -> 0.95
            shell <= radius -> 0.72
            else -> 0.42
          }

          if (chance(s, placeChance)) {
            region.setBlock(x, y, z, leafPicker.value())
          }
        }
      }
    }
  }

  data class Vec3i(val x: Int, val y: Int, val z: Int)

  data class BranchPlan(
    val path: List<Vec3i>,
    val tipX: Int,
    val tipY: Int,
    val tipZ: Int,
    val leafRadius: Int
  )

  data class Placed(
    val baseX: Int,
    val baseZ: Int,
    val baseY: Int,
    val height: Int,
    val thickBaseHeight: Int,
    val trunk: List<Vec3i>,
    val branches: List<BranchPlan>,
    val crownX: Int,
    val crownY: Int,
    val crownZ: Int,
    val crownRadius: Int,
    val seed: Long
  ) : Placement
}
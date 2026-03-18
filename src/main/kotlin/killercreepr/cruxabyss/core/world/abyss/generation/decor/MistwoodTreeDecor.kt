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

class MistwoodTreeDecor(
  override val pass: DecorationPass = DecorationPass.SURFACE,

  val chancePerPoint: Double = 0.05,
  val minAirAbove: Int = 12,
  val maxSlope01: Double = 0.82,

  val minTrunkHeight: Int = 5,
  val maxTrunkHeight: Int = 8,

  val minGroundArm: Int = 3,
  val maxGroundArm: Int = 6,

  val minTopArm: Int = 4,
  val maxTopArm: Int = 7,

  val minCurtains: Int = 6,
  val maxCurtains: Int = 11,

  val logPicker: AxisBlockPicker,
  val leafPicker: Holder<BlockData>,
  val chanceSalt: Long
) : Decoration {

  override fun shouldTry(
    region: LimitedRegion,
    point: PropPoint,
    biomeBlend: BiomeBlendSample
  ): Boolean {
    val s = mixSeed(region.ctx.worldContext.seed, point.worldX, 0, point.worldZ, chanceSalt)
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

    val dirSeed = mixSeed(region.ctx.worldContext.seed, x0, baseY, z0, salt = point.seed xor 0x4411)
    val dir = ((dirSeed ushr 8) and 3L).toInt()

    val (fx, fz) = when (dir) {
      0 -> 1 to 0
      1 -> -1 to 0
      2 -> 0 to 1
      else -> 0 to -1
    }
    val (sx, sz) = -fz to fx

    val trunkHeight = chooseInt(point.seed xor 0x100L, minTrunkHeight, maxTrunkHeight)
    val groundArmLen = chooseInt(point.seed xor 0x200L, minGroundArm, maxGroundArm)
    val topArmLen = chooseInt(point.seed xor 0x300L, minTopArm, maxTopArm)
    val sideArmLen = chooseInt(point.seed xor 0x400L, 2, 4)
    val crownRadius = chooseInt(point.seed xor 0x500L, 2, 3)

    val base = Vec3i(x0, baseY, z0)

    val groundArm = buildGroundArm(base, fx, fz, groundArmLen)
    val trunk = buildVerticalTrunk(base, trunkHeight)
    val topAnchor = trunk.last()

    val topArm = buildArchedTopArm(topAnchor, fx, fz, topArmLen)
    val hasSideArm = chance(point.seed xor 0xCA11L, 0.7)
    val sideArm = if (hasSideArm) {
      buildSideArm(topAnchor, sx, sz, sideArmLen)
    } else emptyList()

    val buttress = buildButtress(base, -fx, -fz, 2)

    val allLogs = LinkedHashSet<Vec3i>()
    allLogs.addAll(groundArm)
    allLogs.addAll(trunk)
    allLogs.addAll(topArm)
    allLogs.addAll(sideArm)
    allLogs.addAll(buttress)

    for (p in allLogs) {
      if (!region.isInRegion(p.x, p.y, p.z)) return null
      if (!q.isReplaceable(p.x, p.y, p.z)) return null
    }

    val crownCenter = topArm[max(0, topArm.size - 2)]
    if (!validateLeafVolume(region, crownCenter.x, crownCenter.y, crownCenter.z, 4)) return null

    val curtainCount = chooseInt(point.seed xor 0x900L, minCurtains, maxCurtains)
    val curtains = ArrayList<CurtainPlan>(curtainCount)

    val hangPoints = ArrayList<Vec3i>()
    hangPoints += topArm.takeLast(minOf(4, topArm.size))
    if (sideArm.isNotEmpty()) hangPoints += sideArm
    hangPoints += listOf(crownCenter, topAnchor)

    for (i in 0 until curtainCount) {
      val anchor = hangPoints[(i + ((point.seed ushr 4) and 7L).toInt()) % hangPoints.size]
      val offsetSeed = mixSeed(region.ctx.worldContext.seed, anchor.x, anchor.y, anchor.z, salt = point.seed xor i.toLong())

      val ox = (((offsetSeed ushr 10) and 3L).toInt()) - 1
      val oz = (((offsetSeed ushr 14) and 3L).toInt()) - 1
      val drop = chooseInt(point.seed xor (0xA00L + i.toLong()), 3, 7)
      val width = chooseInt(point.seed xor (0xB00L + i.toLong()), 1, 2)

      curtains += CurtainPlan(
        startX = anchor.x + ox,
        startY = anchor.y - 1,
        startZ = anchor.z + oz,
        drop = drop,
        width = width
      )
    }

    return Placed(
      logs = allLogs.toList(),
      trunk = trunk,
      topArm = topArm,
      sideArm = sideArm,
      groundArm = groundArm,
      buttress = buttress,
      crownCenter = crownCenter,
      crownRadius = crownRadius,
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

    placeLogPath(region, p.groundArm)
    placeLogPath(region, p.buttress)
    placeLogPath(region, p.trunk)
    placeLogPath(region, p.topArm)
    placeLogPath(region, p.sideArm)

    // Thicken the lower pillar a bit so it feels like the screenshot
    for (i in 0 until minOf(3, p.trunk.size)) {
      val t = p.trunk[i]
      val side = listOf(
        Vec3i(t.x + 1, t.y, t.z),
        Vec3i(t.x, t.y, t.z + 1)
      )
      for (s in side) {
        if (region.isInRegion(s.x, s.y, s.z) && q.isReplaceable(s.x, s.y, s.z)) {
          val block = logPicker.pickBlock(region, s.x, s.y, s.z, Axis.Y) ?: break
          region.setBlock(s.x, s.y, s.z, block)
        }
      }
    }

    // Main crown
    placeHangingCrown(
      region = region,
      cx = p.crownCenter.x,
      cy = p.crownCenter.y,
      cz = p.crownCenter.z,
      radiusXZ = p.crownRadius + 1,
      radiusYUp = 1,
      radiusYDown = 2,
      seed = p.seed xor 0xC001L
    )

    // Canopy extensions under the top arm
    for (i in p.topArm.indices) {
      if (i < p.topArm.size / 2) continue
      val a = p.topArm[i]
      val s = mixSeed(region.ctx.worldContext.seed, a.x, a.y, a.z, salt = p.seed xor i.toLong())
      if (chance(s, 0.6)) {
        placeHangingCrown(
          region = region,
          cx = a.x,
          cy = a.y - 1,
          cz = a.z,
          radiusXZ = 2,
          radiusYUp = 0,
          radiusYDown = 2,
          seed = s
        )
      }
    }

    // Side arm leaf mass
    if (p.sideArm.isNotEmpty()) {
      val tip = p.sideArm.last()
      placeHangingCrown(
        region = region,
        cx = tip.x,
        cy = tip.y - 1,
        cz = tip.z,
        radiusXZ = 2,
        radiusYUp = 0,
        radiusYDown = 2,
        seed = p.seed xor 0x5151L
      )
    }

    // Hanging curtains
    for (curtain in p.curtains) {
      placeCurtain(region, curtain, p.seed)
    }

    // A few long strands for the deep hanging silhouette
    val anchors = p.topArm.takeLast(minOf(4, p.topArm.size)) + listOf(p.crownCenter)
    for ((idx, a) in anchors.withIndex()) {
      val s = mixSeed(region.ctx.worldContext.seed, a.x, a.y, a.z, salt = p.seed xor (0xDD00L + idx))
      if (!chance(s, 0.7)) continue

      val len = 4 + (((s ushr 7) and 3L).toInt())
      for (dy in 1..len) {
        val y = a.y - dy
        if (!region.isInRegion(a.x, y, a.z)) break
        if (!q.isReplaceable(a.x, y, a.z)) break
        region.setBlock(a.x, y, a.z, leafPicker.value())
      }
    }
  }

  private fun placeLogPath(region: LimitedRegion, path: List<Vec3i>) {
    if (path.isEmpty()) return
    val q = region.terrainQueries

    for (i in path.indices) {
      val cur = path[i]
      val prev = if (i == 0) null else path[i - 1]

      val axis = when {
        prev == null -> Axis.Y
        cur.x != prev.x -> Axis.X
        cur.z != prev.z -> Axis.Z
        else -> Axis.Y
      }

      if (region.isInRegion(cur.x, cur.y, cur.z) && q.isReplaceable(cur.x, cur.y, cur.z)) {
        val block = logPicker.pickBlock(region, cur.x, cur.y, cur.z, axis) ?: break
        region.setBlock(cur.x, cur.y, cur.z, block)
      }
    }
  }

  private fun buildGroundArm(base: Vec3i, dx: Int, dz: Int, len: Int): List<Vec3i> {
    val out = ArrayList<Vec3i>(len + 1)
    var x = base.x
    var y = base.y
    var z = base.z

    out += Vec3i(x, y, z)
    for (i in 1..len) {
      x += dx
      z += dz
      if (i >= len / 2 && i % 2 == 0) y += 1
      out += Vec3i(x, y, z)
    }
    return out
  }

  private fun buildVerticalTrunk(base: Vec3i, height: Int): List<Vec3i> {
    val out = ArrayList<Vec3i>(height)
    for (i in 0 until height) {
      out += Vec3i(base.x, base.y + i, base.z)
    }
    return out
  }

  private fun buildArchedTopArm(anchor: Vec3i, dx: Int, dz: Int, len: Int): List<Vec3i> {
    val out = ArrayList<Vec3i>(len)
    var x = anchor.x
    var y = anchor.y
    var z = anchor.z

    for (i in 1..len) {
      x += dx
      z += dz

      when {
        i <= 2 -> y += 1
        i < len -> if (i % 2 == 0) y += 0 else y -= 0
        else -> y -= 1
      }

      out += Vec3i(x, y, z)
    }
    return out
  }

  private fun buildSideArm(anchor: Vec3i, dx: Int, dz: Int, len: Int): List<Vec3i> {
    val out = ArrayList<Vec3i>(len)
    var x = anchor.x
    var y = anchor.y - 1
    var z = anchor.z

    for (i in 1..len) {
      x += dx
      z += dz
      if (i == 1 || i == len) y += 1
      out += Vec3i(x, y, z)
    }
    return out
  }

  private fun buildButtress(base: Vec3i, dx: Int, dz: Int, len: Int): List<Vec3i> {
    val out = ArrayList<Vec3i>(len)
    var x = base.x
    var y = base.y
    var z = base.z

    for (i in 1..len) {
      x += dx
      z += dz
      if (i == len) y -= 1
      out += Vec3i(x, y, z)
    }
    return out
  }

  private fun validateLeafVolume(
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

  private fun placeHangingCrown(
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
          val allowed = if (oy <= 0) radiusXZ + 1 else radiusXZ - 1
          if (radial + abs(oy) > allowed + 1) continue

          val s = mixSeed(region.ctx.worldContext.seed, x, y, z, salt = seed)
          val p = when {
            oy > 0 -> 0.65
            oy == 0 -> 0.90
            oy == -1 -> 0.88
            else -> 0.72
          }

          if (chance(s, p)) {
            region.setBlock(x, y, z, leafPicker.value())
          }
        }
      }
    }
  }

  private fun placeCurtain(
    region: LimitedRegion,
    curtain: CurtainPlan,
    seed: Long
  ) {
    val q = region.terrainQueries

    for (ox in -curtain.width..curtain.width) {
      for (oz in -curtain.width..curtain.width) {
        if (abs(ox) + abs(oz) > curtain.width + 1) continue

        val sx = curtain.startX + ox
        val sy = curtain.startY
        val sz = curtain.startZ + oz

        val s = mixSeed(region.ctx.worldContext.seed, sx, sy, sz, salt = seed xor 0xDADA)
        val realDrop = curtain.drop - (((s ushr 9) and 1L).toInt())

        for (i in 0 until realDrop) {
          val y = sy - i
          if (!region.isInRegion(sx, y, sz)) break
          if (!q.isReplaceable(sx, y, sz)) break

          val p = when {
            i == 0 -> 0.92
            ox == 0 && oz == 0 -> 0.84
            else -> 0.68
          }

          val ps = mixSeed(region.ctx.worldContext.seed, sx, y, sz, salt = s xor i.toLong())
          if (chance(ps, p)) {
            region.setBlock(sx, y, sz, leafPicker.value())
          }
        }
      }
    }
  }

  data class Vec3i(val x: Int, val y: Int, val z: Int)

  data class CurtainPlan(
    val startX: Int,
    val startY: Int,
    val startZ: Int,
    val drop: Int,
    val width: Int
  )

  data class Placed(
    val logs: List<Vec3i>,
    val trunk: List<Vec3i>,
    val topArm: List<Vec3i>,
    val sideArm: List<Vec3i>,
    val groundArm: List<Vec3i>,
    val buttress: List<Vec3i>,
    val crownCenter: Vec3i,
    val crownRadius: Int,
    val curtains: List<CurtainPlan>,
    val seed: Long
  ) : Placement
}
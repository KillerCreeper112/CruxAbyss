package killercreepr.cruxabyss.core.world.abyss.generation.util

import killercreepr.crux.api.data.Holder
import killercreepr.crux.api.loot.WeightedObject
import killercreepr.cruxworldgen.api.block.BlockData
import killercreepr.cruxworldgen.api.util.MathUtil.pseudoRandomInfluence
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockResolver
import org.bukkit.Material
import org.bukkit.block.BlockType
import org.bukkit.block.data.Waterlogged
import kotlin.math.pow

object GenUtil {
  fun <T : Waterlogged> waterLoggedBlockData(type: BlockType.Typed<T>, waterLogged: Boolean = false): Holder<BlockData> =
    Holder.direct(BukkitBlockResolver.INSTANCE.resolve(type.createBlockData { it.isWaterlogged = waterLogged }))

  fun <T : WeightedObject> pickWeightedFromNoise(n: Double, materials: List<T>): T {
    val totalWeight = materials.sumOf { it.weight.toDouble() }
    var cumulative = 0.0

    for (wm in materials) {
      cumulative += wm.weight.toDouble()
      if (n * totalWeight <= cumulative) return wm
    }
    return materials.last()
  }

  fun <T : WeightedObject> generateBlock(x: Int, y: Int, z: Int, nBase: Double, materials: List<T>): T {
    val n = (nBase + pseudoRandomInfluence(x, y, z) * 0.2).coerceIn(0.0, 1.0)

    val type = pickWeightedFromNoise(n, materials)
    return type
  }

  fun <T : WeightedObject> generateBlock(nPatch: Double, nHigh: Double, materials: List<T>): T {
    val nCombined = (nPatch*0.7 + nHigh*0.3).coerceIn(0.0, 1.0)
    return pickWeightedFromNoise(nCombined, materials)
  }

  fun <T : WeightedObject> generateBlockPatchy(
    nPatch: Double,
    nHigh: Double,
    materials: List<T>
  ): T {
    // More contribution from higher-frequency structure
    val combined = (nPatch * 0.45 + nHigh * 0.55).coerceIn(0.0, 1.0)

    // Slight downward bias so lower buckets show up a bit more
    val shaped = combined.pow(1.2)

    return pickWeightedFromNoise(shaped, materials)
  }

  fun <T : WeightedObject> generateBlockChunky(
    nPatch: Double,
    nHigh: Double,
    materials: List<T>
  ): T {
    // Main large-scale patch driver
    val base = nPatch

    // Small boundary wobble only
    val detail = (nHigh - 0.5) * 0.12

    // Keep the overall distribution similar, but help lower buckets slightly
    val shifted = (base + detail).coerceIn(0.0, 1.0)
    val shaped = shifted.pow(1.25)

    return pickWeightedFromNoise(shaped, materials)
  }

  data class WeightedMaterial(val material: Material, val w: Int) : WeightedObject {
    override fun getWeight(): Int = w

    override fun getQuality(): Float = 0.0f
  }
}
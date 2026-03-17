package killercreepr.cruxabyss.core.world.abyss.generation.feature

import killercreepr.crux.core.Crux
import killercreepr.cruxworldgen.api.context.LimitedRegion
import killercreepr.cruxworldgen.api.feature.Feature
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockAdapter
import killercreepr.cruxworldgen.core.feature.BlockPos
import org.bukkit.Material
import java.util.*

class RemoveBottomLayerFeature : Feature<RemoveBottomLayerFeature.Config> {
  override fun place(
    region: LimitedRegion,
    rng: Random,
    origin: BlockPos,
    cfg: Config
  ) {
    val air = BukkitBlockAdapter.resolver().resolve(Material.AIR)

    val eatMinY = region.centerBounds.minY + cfg.eatMin
    val eatMaxY = region.centerBounds.minY + cfg.eatMax
    val height = (eatMaxY - eatMinY).coerceAtLeast(0)

    for (x in region.centerBounds.minX..region.centerBounds.maxX) {
      for (z in region.centerBounds.minZ..region.centerBounds.maxZ) {
        for (y in eatMinY..eatMaxY) {
          val distFromBottom = y - eatMinY+1
          val chance = 1.0 - (distFromBottom.toDouble() / (height + 1).toDouble())

          if (rng.nextDouble() < chance) {
            region.setBlock(x, y, z, air)
          }
        }
      }
    }
  }

  data class Config(
    val eatMin: Int = 0,
    val eatMax: Int = 3
  )
}
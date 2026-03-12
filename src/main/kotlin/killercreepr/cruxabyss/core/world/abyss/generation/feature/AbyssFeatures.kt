package killercreepr.cruxabyss.core.world.abyss.generation.feature

import com.destroystokyo.paper.MaterialSetTag
import killercreepr.cruxabyss.core.block.AbyssBlocks
import killercreepr.cruxworldgen.api.feature.*
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockAdapter
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockSection
import killercreepr.cruxworldgen.core.feature.CoreFeatures
import killercreepr.cruxworldgen.core.feature.ore.OreConfig
import killercreepr.cruxworldgen.crux.block.CruxBlockSection
import org.bukkit.Material

object AbyssFeatures {
  object Ores{
    val canOreReplace: OreConfig.CanReplace = OreConfig.CanReplace { region, rng, x, y, z ->
      val block = region.getBlock(x, y, z)
      if (block is BukkitBlockSection){
        val data = block.blockData()
        val type = data.data.material
        if(MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(type)) return@CanReplace true
        if(MaterialSetTag.DEEPSLATE_ORE_REPLACEABLES.isTagged(type)) return@CanReplace true
        when(type){//todo
          else ->{}
        }
        return@CanReplace false
      }
      if(block is CruxBlockSection){
        if(AbyssBlocks.PLAGUE_STONE.containsBlock(block.blockData.block)) return@CanReplace true
        if(AbyssBlocks.SEEPING_PLAGUE_STONE.containsBlock(block.blockData.block)) return@CanReplace true
      }

      return@CanReplace false
    }

    val FUNGIRE = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = OreConfig.BlockGetter.constant(
          BukkitBlockAdapter.resolver().resolve(AbyssBlocks.FUNGIRE_ORE)
        ),
        minSize = 2,
        maxSize = 8,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.3
      ),
      modifiers = listOf(
        Repeat(
          7, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 0.208),
            order = 2
          )
        )
        )
      )
    )

    val EMERALD = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = OreConfig.BlockGetter.constant(
          BukkitBlockAdapter.resolver().resolve(Material.EMERALD_ORE)
        ),
        minSize = 1,
        maxSize = 3,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.5
      ),
      modifiers = listOf(
        Repeat(100, XZHeight(
          SkewedHeight(
            baseHeight = UniformHeightSampler.relative(0.125, 1.0), // Y -16 to Y 320
            order = 4,
          )
        ))
      )
    )

    val IRON_LOW = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = OreConfig.BlockGetter.constant(
          BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_IRON_ORE)
        ),
        minSize = 2,
        maxSize = 6,
        canReplace = canOreReplace,
        sizeOrder = 0,
        discardChanceOnAirExposure = 0.3
      ),
      modifiers = listOf(
        Repeat(10, XZHeight(
          TriangleHeight(                               // triangle, not trapezoid
            baseHeight = UniformHeightSampler.relative(0.104, 0.313)
          )
        ))
      )
    )

    val IRON_HIGH = PlacedFeature(                       // second pass for upper iron
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = OreConfig.BlockGetter.constant(
          BukkitBlockAdapter.resolver().resolve(Material.IRON_ORE)
        ),
        minSize = 2,
        maxSize = 8,
        canReplace = canOreReplace,
        sizeOrder = 0,
        discardChanceOnAirExposure = 0.4
      ),
      modifiers = listOf(
        Repeat(90, XZHeight(                            // vanilla uses ~90 for the upper burst
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.375, 1.0)
          )
        ))
      )
    )
  }


}
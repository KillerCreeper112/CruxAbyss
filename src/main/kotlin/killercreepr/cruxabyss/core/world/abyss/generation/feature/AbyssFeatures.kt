package killercreepr.cruxabyss.core.world.abyss.generation.feature

import com.destroystokyo.paper.MaterialSetTag
import killercreepr.cruxabyss.core.block.AbyssBlocks
import killercreepr.cruxworldgen.api.block.BlockPicker
import killercreepr.cruxworldgen.api.block.CanReplaceBlock
import killercreepr.cruxworldgen.api.context.LimitedRegion
import killercreepr.cruxworldgen.api.feature.*
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockAdapter
import killercreepr.cruxworldgen.bukkit.block.BukkitBlockSection
import killercreepr.cruxworldgen.core.feature.BlockPos
import killercreepr.cruxworldgen.core.feature.CoreFeatures
import killercreepr.cruxworldgen.core.feature.blob.BlobConfig
import killercreepr.cruxworldgen.core.feature.ore.OreConfig
import killercreepr.cruxworldgen.crux.block.CruxBlockSection
import org.bukkit.Material
import java.util.*

object AbyssFeatures {
  object Blobs {
    val canRockReplace = CanReplaceBlock { region, rng, x, y, z ->
      val block = region.getBlock(x, y, z)
      if (block is BukkitBlockSection) {
        val data = block.blockData()
        val type = data.data.material
        when (type) {
          Material.STONE -> return@CanReplaceBlock true
          else -> {}
        }
        return@CanReplaceBlock false
      }
      return@CanReplaceBlock false
    }
    val canDeepRockReplace = CanReplaceBlock { region, rng, x, y, z ->
      val block = region.getBlock(x, y, z)
      if (block is BukkitBlockSection) {
        val data = block.blockData()
        val type = data.data.material
        when (type) {
          Material.DEEPSLATE
            -> return@CanReplaceBlock true
          else -> {}
        }
        return@CanReplaceBlock false
      }
      return@CanReplaceBlock false
    }

    val GRANITE = PlacedFeature(
      feature = CoreFeatures.BLOB,
      cfg = BlobConfig(
        material = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.GRANITE)),
        canReplace = canRockReplace,
        minRadius = 2,
        maxRadius = 8,
        maxRadiusY = 8
      ),
      modifiers = listOf(
        Repeat(
          7, XZHeight(
          UniformHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 1.0)
          )
        )
        )
      )
    )

    val DIORITE = PlacedFeature(
      feature = CoreFeatures.BLOB,
      cfg = BlobConfig(
        material = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.DIORITE)),
        canReplace = canRockReplace,
        minRadius = 2,
        maxRadius = 8,
        maxRadiusY = 8
      ),
      modifiers = listOf(
        Repeat(
          7, XZHeight(
          UniformHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 1.0)
          )
        )
        )
      )
    )

    val ANDESITE = PlacedFeature(
      feature = CoreFeatures.BLOB,
      cfg = BlobConfig(
        material = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.ANDESITE)),
        canReplace = canRockReplace,
        minRadius = 2,
        maxRadius = 8,
        maxRadiusY = 8
      ),
      modifiers = listOf(
        Repeat(
          7, XZHeight(
          UniformHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 1.0)
          )
        )
        )
      )
    )

    val DEEP_TUFF = PlacedFeature(
      feature = CoreFeatures.BLOB,
      cfg = BlobConfig(
        material = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.TUFF)),
        canReplace = canDeepRockReplace,
        minRadius = 2,
        maxRadius = 8,
        maxRadiusY = 8
      ),
      modifiers = listOf(
        Repeat(
          7, XZHeight(
          UniformHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 0.65)
          )
        )
        )
      )
    )

    val GRAVEL = PlacedFeature(
      feature = CoreFeatures.BLOB,
      cfg = BlobConfig(
        material = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.GRAVEL)),
        canReplace = canRockReplace,
        minRadius = 2,
        maxRadius = 6,
        maxRadiusY = 7
      ),
      modifiers = listOf(
        Repeat(
          3, XZHeight(
          SkewedHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 1.0),
            order = -2
          )
        )
        )
      )
    )

    val DIRT = PlacedFeature(
      feature = CoreFeatures.BLOB,
      cfg = BlobConfig(
        material = BlockPicker.constant(BukkitBlockAdapter.resolver().resolve(Material.DIRT)),
        canReplace = canRockReplace,
        minRadius = 2,
        maxRadius = 4,
        maxRadiusY = 3
      ),
      modifiers = listOf(
        Repeat(
          3, XZHeight(
          SkewedHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 1.0),
            order = -2
          )
        )
        )
      )
    )
  }

  object Ores {
    val canOreReplace: CanReplaceBlock = CanReplaceBlock { region, rng, x, y, z ->
      if(!region.terrainQueries.isSolid(x, y, z)) return@CanReplaceBlock false
      val block = region.getBlock(x, y, z)
      if (block is BukkitBlockSection) {
        val data = block.blockData()
        val type = data.data.material
        if (MaterialSetTag.STONE_ORE_REPLACEABLES.isTagged(type)) return@CanReplaceBlock true
        if (MaterialSetTag.DEEPSLATE_ORE_REPLACEABLES.isTagged(type)) return@CanReplaceBlock true
        when (type) {
          Material.BASALT,
          Material.GRAVEL,
          Material.TUFF,
          Material.COBBLED_DEEPSLATE,
          Material.BLACKSTONE,
          Material.GRANITE,
          Material.DIORITE,
          Material.ANDESITE
             -> return@CanReplaceBlock true
          else -> {}
        }
        return@CanReplaceBlock false
      }
      if (block is CruxBlockSection) {
        if (AbyssBlocks.PLAGUE_STONE.containsBlock(block.blockData.block)) return@CanReplaceBlock true
        if (AbyssBlocks.SEEPING_PLAGUE_STONE.containsBlock(block.blockData.block)) return@CanReplaceBlock true
      }

      return@CanReplaceBlock false
    }

    val MOULDITE_CRUST = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = BlockPicker.constant(
          BukkitBlockAdapter.resolver().resolve(AbyssBlocks.MOULDITE_CRUST)
        ),
        minSize = 0,
        maxSize = 1,
        canReplace = canOreReplace,
        sizeOrder = -9,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          1, XZHeight(
          UniformHeight(
            baseHeight = AddToMinYCenterUniformHeightSampler(0, 4)
          )
        )
        )
      )
    )

    val FUNGIRE = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          when (val block = region.getBlock(x, y, z)) {
            is CruxBlockSection -> {
              if (AbyssBlocks.PLAGUE_STONE.containsBlock(block.blockData.block))
                return@OreConfig BukkitBlockAdapter.resolver().resolve(AbyssBlocks.PLAGUE_STONE_FUNGIRE_ORE)
            }

            is BukkitBlockSection -> {
              if(block.data.data.material == Material.DEEPSLATE){
                return@OreConfig BukkitBlockAdapter.resolver().resolve(AbyssBlocks.DEEPSLATE_FUNGIRE_ORE)
              }
            }
          }
          return@OreConfig BukkitBlockAdapter.resolver().resolve(AbyssBlocks.FUNGIRE_ORE)
        },
        minSize = 0,
        maxSize = 5,
        canReplace = canOreReplace,
        sizeOrder = -3,
        discardChanceOnAirExposure = 0.3
      ),
      modifiers = listOf(
        Repeat(
          3, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 0.25),
            order = 2
          )
        )
        )
      )
    )

    val RED_ABYSS_CRYSTAL = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = BlockPicker.constant(
          BukkitBlockAdapter.resolver().resolve(AbyssBlocks.PLAGUE_STONE_RED_ABYSS_CRYSTAL_ORE)
        ),
        minSize = 0,
        maxSize = 1,
        canReplace = canOreReplace,
        sizeOrder = -2,
        discardChanceOnAirExposure = 0.15
      ),
      modifiers = listOf(
        Repeat(
          4, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 1.0),
            order = 3
          )
        )
        )
      )
    )

    val EMERALD_HIGH = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = BlockPicker.constant(
          BukkitBlockAdapter.resolver().resolve(Material.EMERALD_ORE)
        ),
        minSize = 0,
        maxSize = 1,
        canReplace = canOreReplace,
        sizeOrder = -2,
        discardChanceOnAirExposure = 0.35
      ),
      modifiers = listOf(
        Repeat(
          3, XZHeight(
          SkewedHeight(
            baseHeight = UniformHeightSampler.relative(0.5, 1.0),
            order = 4,
          )
        )
        )
      )
    )

    val EMERALD_LOW = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_EMERALD_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.EMERALD_ORE)
        },
        minSize = 0,
        maxSize = 1,
        canReplace = canOreReplace,
        sizeOrder = -3,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          1, XZHeight(
          SkewedHeight(
            baseHeight = UniformHeightSampler.relative(0.125, 0.4),
            order = 2,
          )
        )
        )
      )
    )

    val GOLD_LOW = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_GOLD_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.GOLD_ORE)
        },
        minSize = 0,
        maxSize = 4,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          10, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.05, 0.45)
          )
        )
        )
      )
    )

    val REDSTONE_LOW = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_REDSTONE_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.REDSTONE_ORE)
        },
        minSize = 0,
        maxSize = 4,
        canReplace = canOreReplace,
        sizeOrder = 0,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          4, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 0.3)
          )
        )
        )
      )
    )

    val LAPIS_LOW = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_LAPIS_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.LAPIS_ORE)
        },
        minSize = 0,
        maxSize = 5,
        canReplace = canOreReplace,
        sizeOrder = 0,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          3, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.0, 0.6)
          )
        )
        )
      )
    )

    val COPPER = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_COPPER_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.COPPER_ORE)
        },
        minSize = 1,
        maxSize = 8,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          12, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.22, 0.67)
          )
        )
        )
      )
    )

    val COAL = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_COAL_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.COAL_ORE)
        },
        minSize = 0,
        maxSize = 8,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          9, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.3, 0.7)
          )
        )
        )
      )
    )

    val COAL_HIGH = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = { region, rng, x, y, z ->
          val block = region.getBlock(x, y, z)
          if (block is BukkitBlockSection && block.data.data.material == Material.DEEPSLATE)
            return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_COAL_ORE)
          return@OreConfig BukkitBlockAdapter.resolver().resolve(Material.COAL_ORE)
        },
        minSize = 1,
        maxSize = 6,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.3
      ),
      modifiers = listOf(
        Repeat(
          8, XZHeight(
          UniformHeight(
            baseHeight = UniformHeightSampler.relative(0.5, 1.0)
          )
        )
        )
      )
    )

    val IRON_LOW = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = BlockPicker.constant(
          BukkitBlockAdapter.resolver().resolve(Material.DEEPSLATE_IRON_ORE)
        ),
        minSize = 0,
        maxSize = 6,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.0
      ),
      modifiers = listOf(
        Repeat(
          8, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.104, 0.313)
          )
        )
        )
      )
    )

    val IRON_HIGH = PlacedFeature(
      feature = CoreFeatures.ORE_VEIN,
      cfg = OreConfig(
        ore = BlockPicker.constant(
          BukkitBlockAdapter.resolver().resolve(Material.IRON_ORE)
        ),
        minSize = 0,
        maxSize = 8,
        canReplace = canOreReplace,
        sizeOrder = -1,
        discardChanceOnAirExposure = 0.4
      ),
      modifiers = listOf(
        Repeat(
          18, XZHeight(
          TriangleHeight(
            baseHeight = UniformHeightSampler.relative(0.375, 1.0)
          )
        )
        )
      )
    )
  }

  object Misc{
    val REMOVE_BOTTOM_LATER = PlacedFeature(
      feature = RemoveBottomLayerFeature(),
      cfg = RemoveBottomLayerFeature.Config(),
      modifiers = listOf(object: PlacementModifier{
        override fun emitPositions(
          region: LimitedRegion,
          rng: Random,
          chunkX: Int,
          chunkZ: Int,
          out: MutableList<BlockPos>
        ) {
          out.add(BlockPos(0,0,0))
        }
      })
    )
  }

}
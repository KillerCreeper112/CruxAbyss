package killercreepr.cruxabyss.item;

import killercreepr.crux.tags.container.MergedTagContainer;
import killercreepr.cruxabyss.block.AbyssBlocks;
import killercreepr.cruxblocks.persistence.CruxBlocksPersistTags;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import killercreepr.cruxitems.item.CruxedItem;
import killercreepr.cruxitems.item.plugin.GenericPluginItem;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockPluginItem extends GenericPluginItem {
    protected final Integer customModelData;
    public BlockPluginItem(@NotNull Key key, Integer customModelData) {
        super(key);
        this.customModelData = customModelData;
    }

    @Override
    public @NotNull CruxedItem build(@Nullable Entity entity, @Nullable MergedTagContainer mergedTagContainer) {
        CruxedItem item = new CruxedItem(Material.PAPER);
        item.customModelData(customModelData)
            .edit(i -> CruxBlocksPersistTags.CRUX_BLOCK_GROUP.set(i, CruxBlocksRegistries.BLOCKS.getGroup(key)));
        return item;
    }
}

package killercreepr.cruxabyss.core.block;


import killercreepr.crux.api.component.DataComponentHandler;
import killercreepr.crux.api.component.TypedDataComponent;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.cruxabyss.core.block.active.ActiveAncientDebris;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.core.block.SimpleBlock;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import killercreepr.cruxblocks.core.block.group.SingularBlockGroup;
import killercreepr.cruxblocks.core.block.texture.MaterialTextureData;
import killercreepr.cruxblocks.core.registries.CruxBlocksRegistries;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AbyssBlocks {
    public static void register(){
    }

    public static final CruxBlockGroup ANCIENT_DEBRIS = CruxBlocksRegistries.BLOCK.registerGroup(new SingularBlockGroup(
        new SimpleBlock(Key.key("ancient_debris"), new MaterialTextureData(Material.ANCIENT_DEBRIS), null){
            @Override
            public @NotNull ActiveCruxBlock createActive(@NotNull Block block) {
                return new ActiveAncientDebris(block, this);
            }
        },
        DataComponentHandler.simple(Set.of(
            TypedDataComponent.create(CruxBlockComponents.REQUIRES_CORRECT_TOOL_FOR_DROPS, true),
            TypedDataComponent.create(CruxBlockComponents.EXPLOSION_RESISTANCE, 1200f),
            TypedDataComponent.create(CruxComponents.HARDNESS, 5f)
        ))
    ));
    public static final CruxBlockGroup PLAGUE_MOSS = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_moss"));
    public static final CruxBlockGroup PLAGUE_DIRT = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_dirt"));
    public static final CruxBlockGroup PLAGUE_STONE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_stone"));
    public static final CruxBlockGroup SEEPING_PLAGUE_STONE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("seeping_plague_stone"));
    public static final CruxBlockGroup PLAGUE_STEM = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_stem"));
    public static final CruxBlockGroup PLAGUE_WART =  CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_wart"));
    public static final CruxBlockGroup PLAGUE_ROOTS = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_roots"));
    public static final CruxBlockGroup PLAGUE_SHROOM = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_shroom"));
    public static final CruxBlockGroup TALL_PLAGUE_SHROOM = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("tall_plague_shroom"));
    public static final CruxBlockGroup MIREHORN = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("mirehorn"));
    public static final CruxBlockGroup PLAGUE_STONE_RED_ABYSS_CRYSTAL_ORE =  CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_stone_red_abyss_crystal_ore"));
    public static final CruxBlockGroup ABYSS_CONQUEST_NODE =  CruxBlocksRegistries.BLOCK.getGroup(Crux.key("abyss_conquest_node"));
    public static final CruxBlockGroup CHARRED_PLANKS =  CruxBlocksRegistries.BLOCK.getGroup(Crux.key("charred_planks"));
    public static final CruxBlockGroup PLAGUE_PLANKS =  CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_planks"));

    public static final CruxBlockGroup FUNGIRE_ORE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("fungire_ore"));
    public static final CruxBlockGroup DEEPSLATE_FUNGIRE_ORE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("deepslate_fungire_ore"));
    public static final CruxBlockGroup PLAGUE_STONE_FUNGIRE_ORE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("plague_stone_fungire_ore"));
    public static final CruxBlockGroup TOXSPORE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("toxspore"));
    public static final CruxBlockGroup EMBERWEED = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("emberweed"));


    public static final CruxBlockGroup WISPTHISTLE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("wispthistle"));
    public static final CruxBlockGroup SPORECAP = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("sporecap"));
    public static final CruxBlockGroup VEILSTARE = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("veilstare"));
    public static final CruxBlockGroup EYEWITHER = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("eyewither"));
    public static final CruxBlockGroup MOULDITE_CRUST = CruxBlocksRegistries.BLOCK.getGroup(Crux.key("mouldite_crust"));
}

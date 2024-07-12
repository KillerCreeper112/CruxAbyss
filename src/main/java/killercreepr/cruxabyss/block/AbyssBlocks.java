package killercreepr.cruxabyss.block;

import killercreepr.crux.Crux;
import killercreepr.cruxblocks.block.GenericBlock;
import killercreepr.cruxblocks.block.GenericDirectionalBlock;
import killercreepr.cruxblocks.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.block.group.CruxDirectionalBlockGroup;
import killercreepr.cruxblocks.block.group.GenericDirectionalBlockGroup;
import killercreepr.cruxblocks.block.group.SingularBlockGroup;
import killercreepr.cruxblocks.block.texture.NoteTextureData;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import org.bukkit.Axis;
import org.bukkit.Instrument;
import org.bukkit.Note;

public class AbyssBlocks {
    public static void register(){}
    public static final CruxBlockGroup PLAGUE_MOSS = CruxBlocksRegistries.BLOCKS.registerGroup(new SingularBlockGroup(
        new GenericBlock(Crux.key("plague_moss"), new NoteTextureData.Builder(new Note(3), Instrument.BANJO).powered(false).build())
    ) {
        @Override
        public float getHardness() {
            return 1.5f;
        }
    });

    public static final CruxBlockGroup PLAGUE_MOSS_DIRT = CruxBlocksRegistries.BLOCKS.registerGroup(new SingularBlockGroup(
        new GenericBlock(Crux.key("plague_moss_dirt"), new NoteTextureData.Builder(new Note(4), Instrument.BANJO).powered(false).build())
    ) {
        @Override
        public float getHardness() {
            return 1.2f;
        }
    });

    public static final CruxBlockGroup PLAGUE_STONE = CruxBlocksRegistries.BLOCKS.registerGroup(new SingularBlockGroup(
        new GenericBlock(Crux.key("plague_stone"), new NoteTextureData.Builder(new Note(5), Instrument.BANJO).powered(false).build())
    ) {
        @Override
        public float getHardness() {
            return 2f;
        }
    });

    public static final CruxDirectionalBlockGroup PLAGUE_STEM = CruxBlocksRegistries.BLOCKS.registerGroup(new GenericDirectionalBlockGroup(
        Crux.key("plague_stem"), true,
        new GenericDirectionalBlock(Crux.key("plague_stem_x"),
            new NoteTextureData.Builder(new Note(6), Instrument.BANJO).powered(false).build(),
            CruxDirectionalBlockGroup.getFaceFromAxis(Axis.X)),
        new GenericDirectionalBlock(Crux.key("plague_stem_y"),
            new NoteTextureData.Builder(new Note(7), Instrument.BANJO).powered(false).build(),
            CruxDirectionalBlockGroup.getFaceFromAxis(Axis.Y)),
        new GenericDirectionalBlock(Crux.key("plague_stem_z"),
            new NoteTextureData.Builder(new Note(8), Instrument.BANJO).powered(false).build(),
            CruxDirectionalBlockGroup.getFaceFromAxis(Axis.Z))
    ) {
        @Override
        public float getHardness() {
            return 2f;
        }
    });

    public static final CruxBlockGroup PLAGUE_WART = CruxBlocksRegistries.BLOCKS.registerGroup(new SingularBlockGroup(
        new GenericBlock(Crux.key("plague_wart"), new NoteTextureData.Builder(new Note(9), Instrument.BANJO).powered(false).build())
    ) {
        @Override
        public float getHardness() {
            return 1.2f;
        }
    });
}

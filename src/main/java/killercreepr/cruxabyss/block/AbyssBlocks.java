package killercreepr.cruxabyss.block;

import killercreepr.crux.Crux;
import killercreepr.cruxblocks.block.GenericBlock;
import killercreepr.cruxblocks.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.block.group.SingularBlockGroup;
import killercreepr.cruxblocks.block.texture.NoteTextureData;
import killercreepr.cruxblocks.registeries.CruxBlockRegistry;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
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
}

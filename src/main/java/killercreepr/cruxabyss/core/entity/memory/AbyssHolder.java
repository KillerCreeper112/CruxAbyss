package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.data.Loadable;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerDataHolder;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbyssHolder extends PlayerDataHolder implements Loadable {
    public static AbyssHolder abyssHolder(Player p){
        return EntityMemory.getOrCreateDataHolder(p, AbyssHolder.class, mem ->{
            if(!(mem instanceof PlayerMemory d)) return null;
            return new AbyssHolder(d);
        });
    }

    public static final Key KEY = Crux.key("abyss");
    public AbyssHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
        load();
    }
    public AbyssHolder(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    protected long longestAbyssOutpostControlDuration;

    public long getLongestAbyssOutpostControlDuration() {
        return longestAbyssOutpostControlDuration;
    }

    public void setLongestAbyssOutpostControlDuration(long longestAbyssOutpostControlDuration) {
        this.longestAbyssOutpostControlDuration = longestAbyssOutpostControlDuration;
    }

    public DataFile getSaveFile(boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(
            CruxFolder.file(Crux.getMainPlugin(), "data/cruxabyss/player/" + parent.getUUID() + ".json"),
            createIfNeeded
        );
    }

    @Override
    public void save() {
        DataFile file = getSaveFile(longestAbyssOutpostControlDuration != 0L);
        if(file == null) return;
        file.serialize("longest_abyss_outpost_control_duration", longestAbyssOutpostControlDuration);
        file.save();
    }

    @Override
    public void load() {
        DataFile file = getSaveFile(false);
        if(file == null) return;
        Long x = file.deserialize("longest_abyss_outpost_control_duration", Long.class);
        file.close();
        if(x != null) setLongestAbyssOutpostControlDuration(x);
    }
}

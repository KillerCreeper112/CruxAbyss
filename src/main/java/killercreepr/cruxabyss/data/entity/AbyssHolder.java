package killercreepr.cruxabyss.data.entity;

import killercreepr.crux.Crux;
import killercreepr.crux.data.Loadable;
import killercreepr.crux.data.entity.PlayerDataHolder;
import killercreepr.crux.data.entity.PlayerMemory;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssHolder extends PlayerDataHolder implements Loadable {
    protected final Plugin plugin;
    public static final Key KEY = Crux.key("abyss");
    public AbyssHolder(@NotNull Key key, @NotNull PlayerMemory parent, Plugin plugin) {
        super(key, parent);
        this.plugin = plugin;
        load();
    }

    public AbyssHolder(@NotNull PlayerMemory parent, Plugin plugin) {
        this(KEY, parent, plugin);
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        save();
    }

    public @NotNull CruxJson buildSaveFile(){
        return new CruxJson(plugin, "data/cruxabyss/player/" + parent.getUUID());
    }

    protected int abyssTravelAmount = 0;

    public int getAbyssTravelAmount() {
        return abyssTravelAmount;
    }

    public void setAbyssTravelAmount(int abyssTravelAmount) {
        this.abyssTravelAmount = abyssTravelAmount;
    }

    @Override
    public void save() {
        CruxJson json = buildSaveFile();
        json.serialize("abyss_travel_amount", abyssTravelAmount);
        json.save();
    }

    @Override
    public void load() {
        CruxJson json = buildSaveFile();
        Number x = json.deserialize("abyss_travel_amount", Number.class);
        json.close();
        if(x != null) setAbyssTravelAmount(x.intValue());
    }
}

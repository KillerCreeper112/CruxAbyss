package killercreepr.cruxabyss.core.config.loader;

import killercreepr.crux.api.loot.LootPool;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.loot.SimpleLootTable;
import killercreepr.crux.core.loot.key.SimpleKeyLootTable;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxabyss.core.challenge.ChallengeRoll;
import killercreepr.cruxconfig.config.bukkit.handler.impl.loot.FileSimpleLootTable;
import killercreepr.cruxconfig.config.bukkit.loader.CfgLoader;
import killercreepr.cruxconfig.config.bukkit.standard.CommonLootTableHandlers;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.element.FileElement;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.common.file.DataFile;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChallengeRollLootTableLoader extends CfgLoader {
    public static final FileSimpleLootTable<ChallengeRoll> CHALLENGE_ROLL_LOOT_TABLE = new FileSimpleLootTable<>(ChallengeRoll.class) {
        public @Nullable SimpleLootTable<ChallengeRoll> createLootTable(@NotNull Key key, @NotNull NumberProvider rolls, @NotNull List<LootPool<ChallengeRoll>> lootPools) {
            return new SimpleLootTable<>(key, rolls, lootPools);
        }
    };

    protected final @NotNull FileSimpleLootTable<ChallengeRoll> fileSimpleLootTable;

    public ChallengeRollLootTableLoader(@NotNull FileSimpleLootTable<ChallengeRoll> fileSimpleLootTable) {
        this.fileSimpleLootTable = fileSimpleLootTable;
    }

    public ChallengeRollLootTableLoader() {
        this(CHALLENGE_ROLL_LOOT_TABLE);
    }

    public void loadConfiguration(@NotNull DataFile cfg, @Nullable String path) {
        LootTable<ChallengeRoll> table;
        if (path == null) {
            FileElement var5 = cfg.getRoot();
            if (!(var5 instanceof FileObject root)) {
                return;
            }

            table = this.fileSimpleLootTable.deserializeFromFile(new FileContext<>(cfg.fileRegistry()), root);
        } else {
            FileElement var7 = cfg.getRoot();
            if (!(var7 instanceof FileObject root)) {
                return;
            }

            table = this.fileSimpleLootTable.deserializeFromFile(new FileContext<>(cfg.fileRegistry()), root, Crux.key(path));
        }

        if (table != null) {
            register(table);
        }
    }

    public void register(LootTable<ChallengeRoll> lootTable){

    }
}
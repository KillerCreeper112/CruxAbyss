package killercreepr.cruxabyss.core.loot;

import killercreepr.crux.api.loot.LootPool;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.loot.SimpleLootTable;
import killercreepr.cruxabyss.api.loot.MobWaveGroupLootTable;
import killercreepr.cruxabyss.core.game.entity.MobWaveGroup;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleMobWaveGroupLootTable extends SimpleLootTable<MobWaveGroup> implements MobWaveGroupLootTable {
    public SimpleMobWaveGroupLootTable(@NotNull Key key, @NotNull NumberProvider rolls, @NotNull List<LootPool<MobWaveGroup>> lootPools) {
        super(key, rolls, lootPools);
    }
}

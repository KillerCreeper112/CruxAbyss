package killercreepr.cruxabyss.world.entity;

import killercreepr.cruxabyss.game.GameManager;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnInfo {
    private final Block block;
    private final GameManager game;

    public SpawnInfo(@NotNull Block block, @Nullable GameManager game) {
        this.block = block;
        this.game = game;
    }

    public @NotNull Block getBlock() {
        return block;
    }

    public @Nullable GameManager getGame() {
        return game;
    }
}

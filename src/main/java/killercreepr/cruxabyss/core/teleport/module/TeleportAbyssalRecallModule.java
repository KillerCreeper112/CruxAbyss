package killercreepr.cruxabyss.core.teleport.module;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.active.ActiveAbyssalRecallUpgrade;
import killercreepr.cruxteleport.api.teleport.holder.EntityTeleportHolder;
import killercreepr.cruxteleport.api.teleport.module.TeleportModule;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class TeleportAbyssalRecallModule implements TeleportModule {
    protected final ActiveAbyssalRecallUpgrade upgrade;
    protected final Block block;

    public TeleportAbyssalRecallModule(ActiveAbyssalRecallUpgrade upgrade, Block block) {
        this.upgrade = upgrade;
        this.block = block;
    }

    @Override
    public void load(@NotNull EntityTeleportHolder entityTeleportHolder, @NotNull Entity entity) {

    }

    @Override
    public void unload(@NotNull EntityTeleportHolder entityTeleportHolder) {

    }

    @Override
    public boolean onSuccess(@NotNull EntityTeleportHolder holder, @NotNull Entity e, @NotNull Location spawn) {
        Crux.scheduler().runTask(() ->{
            upgrade.onTeleportToAnchor(block);
        });
        return TeleportModule.super.onSuccess(holder, e, spawn);
    }
}

package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.event.SuccessfulEntityTravelThroughRiftEvent;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import killercreepr.cruxteleport.api.teleport.TeleportBuildContext;
import killercreepr.cruxteleport.api.teleport.holder.EntityTeleportHolder;
import killercreepr.cruxteleport.api.teleport.module.TeleportModule;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TeleportAbyssWorldModule implements TeleporterComponent, TeleportModule {
    protected final UUID originalUser;
    protected final Mob rift;

    public TeleportAbyssWorldModule(UUID originalUser, Mob rift) {
        this.originalUser = originalUser;
        this.rift = rift;
    }

    @Override
    public void load(@NotNull EntityTeleportHolder entityTeleportHolder, @NotNull Entity entity) {

    }

    @Override
    public void unload(@NotNull EntityTeleportHolder entityTeleportHolder) {

    }

    @Override
    public boolean onSuccess(@NotNull EntityTeleportHolder holder, @NotNull Entity e, @NotNull Location spawn) {
        if(!e.getUniqueId().equals(originalUser)) return true;
        Crux.scheduler().runTask(() ->{
            Entity returnPortal = AbyssMob.RETURN_PORTAL.spawn(spawn, rift.getLocation());
            SuccessfulEntityTravelThroughRiftEvent successEvent = new SuccessfulEntityTravelThroughRiftEvent(e, returnPortal);
            successEvent.callEvent();
        });
        return TeleportModule.super.onSuccess(holder, e, spawn);
    }

    @Nullable
    @Override
    public TeleportModule buildTeleportModule(@NotNull TeleportBuildContext ctx) {
        return this;
    }
}

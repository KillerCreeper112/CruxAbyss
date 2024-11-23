package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.holder.LocationHolder;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.core.block.active.ActiveAbyssPortalGateway;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssPortalGateway implements CruxBlockComponent {
    protected final NumberProvider tickPeriod;
    protected final NumberProvider checkRange;
    protected final NumberProvider cooldown;
    protected final LocationHolder destination;
    protected final CreateSound spawnSound;
    protected final CreateSound despawnSound;
    public NumberProvider getCooldown() {
        return cooldown;
    }

    public AbyssPortalGateway(NumberProvider tickPeriod,
                              NumberProvider checkRange,
                              NumberProvider cooldown,
                              LocationHolder destination,
                              CreateSound spawnSound, CreateSound despawnSound) {
        this.tickPeriod = tickPeriod;
        this.checkRange = checkRange;
        this.cooldown = cooldown;
        this.destination = destination;
        this.spawnSound = spawnSound;
        this.despawnSound = despawnSound;
    }

    public LocationHolder getDestination() {
        return destination;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveAbyssPortalGateway(block, crux, this);
    }

    public CreateSound getSpawnSound() {
        return spawnSound;
    }

    public CreateSound getDespawnSound() {
        return despawnSound;
    }

    public NumberProvider getTickPeriod() {
        return tickPeriod;
    }

    public NumberProvider getCheckRange() {
        return checkRange;
    }
}

package killercreepr.cruxabyss.component.impl;

import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.valueproviders.number.NumberProvider;
import killercreepr.cruxabyss.block.active.ActiveAbyssConquestNode;
import killercreepr.cruxblocks.block.CruxBlock;
import killercreepr.cruxblocks.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.block.component.CruxBlockComponent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssConquestNode implements CruxBlockComponent {
    protected final NumberProvider takeOverTime;
    protected final NumberProvider requiredExperience;
    protected final NumberProvider deactivateTime;
    protected final NumberProvider fireworksRange;
    protected final NumberProvider fireworksRangeY;
    protected final CreateSound takeOverSound;

    public AbyssConquestNode(NumberProvider takeOverTime, NumberProvider requiredExperience, NumberProvider deactivateTime, NumberProvider fireworksRange, NumberProvider fireworksRangeY, CreateSound takeOverSound) {
        this.takeOverTime = takeOverTime;
        this.requiredExperience = requiredExperience;
        this.deactivateTime = deactivateTime;
        this.fireworksRange = fireworksRange;
        this.fireworksRangeY = fireworksRangeY;
        this.takeOverSound = takeOverSound;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveAbyssConquestNode(block, crux, this);
    }

    public NumberProvider getFireworksRangeY() {
        return fireworksRangeY;
    }

    public NumberProvider getDeactivateTime() {
        return deactivateTime;
    }

    public NumberProvider getFireworksRange() {
        return fireworksRange;
    }

    public NumberProvider getTakeOverTime() {
        return takeOverTime;
    }

    public NumberProvider getRequiredExperience() {
        return requiredExperience;
    }

    public CreateSound getTakeOverSound() {
        return takeOverSound;
    }
}

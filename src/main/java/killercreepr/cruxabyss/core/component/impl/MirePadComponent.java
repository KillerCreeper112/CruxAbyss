package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.valueproviders.vector.NumberVector;
import killercreepr.cruxabyss.core.block.active.ActiveMirePad;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MirePadComponent implements CruxBlockComponent {
    public final NumberVector launchForce;
    public final Collection<PotionEffect> launchPotions;
    public final CreateSound launchSound;
    public final boolean useEntityRotation;
    public final boolean useEntityPitch;

    public MirePadComponent(NumberVector launchForce, Collection<PotionEffect> launchPotions, CreateSound launchSound) {
        this.launchForce = launchForce;
        this.launchPotions = launchPotions;
        this.launchSound = launchSound;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveMirePad(block, crux, this);
    }
}

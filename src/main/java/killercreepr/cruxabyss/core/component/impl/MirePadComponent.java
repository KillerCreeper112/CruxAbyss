package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.valueproviders.vector.NumberVector;
import killercreepr.cruxabyss.core.block.active.ActiveMirePad;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.StoredPotion;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MirePadComponent implements CruxBlockComponent {
    public final NumberVector launchForce;
    public final Collection<PotionEffect> launchPotions;
    public final Collection<StoredPotion> launchCustomPotions;
    public final CreateSound launchSound;
    public final float directionBoost;
    public final boolean useEntityPitch;
    public final int launchCooldown;

    public MirePadComponent(NumberVector launchForce, Collection<PotionEffect> launchPotions, Collection<StoredPotion> launchCustomPotions, CreateSound launchSound, float directionBoost, boolean useEntityPitch, int launchCooldown) {
        this.launchForce = launchForce;
        this.launchPotions = launchPotions;
        this.launchCustomPotions = launchCustomPotions;
        this.launchSound = launchSound;
        this.directionBoost = directionBoost;
        this.useEntityPitch = useEntityPitch;
        this.launchCooldown = launchCooldown;
    }

    @Override
    public @Nullable ActiveCruxBlock createActive(@NotNull Block block, @NotNull CruxBlock crux) {
        return new ActiveMirePad(block, crux, this);
    }
}

package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SporeburstChargeComponent implements InteractableComponent {
    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        CruxItem crux = ctx.getItem();
        ItemStack item = crux.item();
        ItemStack copy = item.clone();
        item.setAmount(item.getAmount()-1);

        Player p = ctx.getPlayer();
        AbyssMob.SPOREBURST.throwBurst(this, CruxItem.wrap(copy), p.getEyeLocation(), e ->{
            e.setShooter(p);
            e.setItem(copy);
            Vector vel = p.getEyeLocation().getDirection().multiply(1.2);
            e.setVelocity(vel);
        });
        CreateSound.sound(Sound.ENTITY_SNOWBALL_THROW, .8f).playAt(p);
        return ItemUseResult.cancelled();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }
}

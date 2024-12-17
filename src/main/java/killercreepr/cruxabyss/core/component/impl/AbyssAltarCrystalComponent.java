package killercreepr.cruxabyss.core.component.impl;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.component.CruxComponents;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.component.AbyssAltarCrystal;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.world.AbyssWorldTypes;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorldType;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.component.CruxWorldsComponents;
import killercreepr.usurvive.world.WorldUtil;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbyssAltarCrystalComponent implements AbyssAltarCrystal {
    protected final @Nullable String teleportType;
    protected final @Nullable Color portalColor;
    public AbyssAltarCrystalComponent(@Nullable String teleportType,
                                      @Nullable Color portalColor) {
        this.teleportType = teleportType;
        this.portalColor = portalColor;
    }

    @Override
    public boolean canPlaceOn(@NotNull AbyssAltar altar) {
        return altar.selectedEntities().isEmpty();
    }

    @Override
    public void place(@NotNull AbyssAltar altar, @NotNull Player user, @NotNull CruxItem cruxItem) {
        ItemStack item = cruxItem.item();
        ItemStack clonedItem = item.clone();
        if(user.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);

        Block b = altar.center();
        CruxWorld world = CruxCore.inst().worldManager().getWorld(b.getWorld().getUID());
        if(world != null && AbyssWorldTypes.ABYSS.compare(world.get(CruxWorldsComponents.WORLD_TYPE))){
            b.getWorld().createExplosion(b.getLocation().toCenterLocation(), 4f, true, true);
            return;
        }

        Location spawn = altar.generalPlaceLocation();
        Entity crystalMob = AbyssMob.ABYSS_CRYSTAL.place(spawn, altar, clonedItem);
        CreateSound.sound(Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1.5f).playAt(crystalMob);
    }

    @Nullable
    @Override
    public Color portalColor() {
        return portalColor;
    }

    @Nullable
    @Override
    public String teleportType() {
        return teleportType;
    }
}

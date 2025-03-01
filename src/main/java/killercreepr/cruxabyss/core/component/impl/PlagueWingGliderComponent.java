package killercreepr.cruxabyss.core.component.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlagueWingGliderComponent implements InteractableComponent {
    protected final float moveSpeed;
    protected final List<PotionEffect> gliderPotions;
    protected final float minFallDistance;
    protected final int minEmptyBlockDistance;
    protected final int itemDamagePerSecond;

    public PlagueWingGliderComponent(float moveSpeed, List<PotionEffect> gliderPotions, float minFallDistance, int minEmptyBlockDistance, int itemDamagePerSecond) {
        this.moveSpeed = moveSpeed;
        this.gliderPotions = gliderPotions;
        this.minFallDistance = minFallDistance;
        this.minEmptyBlockDistance = minEmptyBlockDistance;
        this.itemDamagePerSecond = itemDamagePerSecond;
    }

    public int getItemDamagePerSecond() {
        return itemDamagePerSecond;
    }

    @Override
    public @NotNull ItemUseResult onUse(@NotNull ItemUseContext ctx) {
        Player p = ctx.getPlayer();

        if(!hasEnoughFallDistance(p)){
            Lang.PLAGUE_WING_GLIDER_FALL_DISTANCE_INSUFFICIENT.use(p);
            return ItemUseResult.empty();
        }
        if(!hasEnoughEmptyBlocksBelow(p)){
            Lang.PLAGUE_WING_GLIDER_BLOCK_DISTANCE_INSUFFICIENT.use(p);
            return ItemUseResult.empty();
        }
        ItemStack item = ctx.getItem().item();

        //needs at least 10 durability to deploy
        int maxDamage = CruxItem.getMaxDurability(item);
        if(maxDamage > 0){
            Integer dmg = item.getData(DataComponentTypes.DAMAGE);
            if(dmg != null){
                int durability = maxDamage - dmg;
                if(durability < 10) return ItemUseResult.empty();
            }
        }

        ItemStack copy = item.clone();
        copy.setAmount(1);
        if(p.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount()-1);
        Crux.handlers().item().damageItem(copy, itemDamagePerSecond, p);

        AbyssMob.PLAGUE_WING_GLIDER.createAndMountGlider(p, this, copy);
        CreateSound.sound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f).playAt(p);
        CreateSound.sound(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.3f).playAt(p);

        return ItemUseResult.empty();
    }

    public float getMinFallDistance() {
        return minFallDistance;
    }

    public int getMinEmptyBlockDistance() {
        return minEmptyBlockDistance;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public List<PotionEffect> getGliderPotions() {
        return gliderPotions;
    }

    public boolean isEmpty(Block b){
        if(b.isLiquid()) return false;
        return b.isEmpty() || b.isPassable();
    }

    public boolean hasEnoughFallDistance(Player p){
        return p.getFallDistance() >= minFallDistance;
    }

    public boolean hasEnoughEmptyBlocksBelow(Player p){
        Block b = p.getLocation().getBlock();
        for(int i = 1; i <= minEmptyBlockDistance; i++){
            Block check = b.getRelative(0, -i, 0);
            if(!isEmpty(check)) return false;
        }
        return true;
    }

    @Override
    public boolean isUsable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick() && ctx.getPlayer().getVehicle() == null;
        /*if(!ctx.getAction().isRightClick()) return false;
        Player p = ctx.getPlayer();
        if(!hasEnoughEmptyBlocksBelow(p)) return false;
        return hasEnoughFallDistance(p);*/
    }
}

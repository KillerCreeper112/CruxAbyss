package killercreepr.cruxabyss.core.entity.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.entity.AbyssAltarItemEntity;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbyssAltarPlacedItemGoal extends CruxMobModeledGoal implements Listener {
    protected AbyssAltar altar;
    protected int checkIsValidTime = 5;
    public AbyssAltarPlacedItemGoal(@NotNull Mob mob) {
        this(defaultKey(), mob);
    }

    public AbyssAltarPlacedItemGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
        this.altarItem = AbyssAltarItemEntity.wrap(mob);
    }
    protected final AbyssAltarItemEntity altarItem;

    public void onLeftClick(Player p){

    }

    public void onRightClick(Player p){
        takeItem(p);
    }

    public void takeItem(Player p){
        ItemStack item = altarItem.display();
        if(item == null) return;

        if(p.getInventory().firstEmpty() == -1) return;
        CruxEntityUtil.giveOrDrop(p, item.clone());
        mob.remove();

        p.swingMainHand();
        CreateSound.sound(Sound.ENTITY_ITEM_PICKUP).playAt(mob.getLocation());
    }

    public AbyssAltar getAltar() {
        return altar;
    }

    public void setAltar(AbyssAltar altar) {
        this.altar = altar;
    }

    public int getCheckIsValidTime() {
        return checkIsValidTime;
    }

    public void setCheckIsValidTime(int checkIsValidTime) {
        this.checkIsValidTime = checkIsValidTime;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    protected int tick = 0;
    @Override
    public void tick() {
        tick++;
        if(checkIsValidTime != 0 && tick % checkIsValidTime == 0){
            tick = 0;
            if(!isValid()) destroy();
        }
    }

    public void destroy(){
        mob.remove();
        destroyEffect();
    }

    public AbyssAltarItemEntity altarItem(){
        return altarItem;
    }

    public void destroyEffect(){
        CreateSound.sound(Sound.ENTITY_ITEM_BREAK).playAt(mob);
        ItemStack item = altarItem.display();
        if(item != null){
            new ParticleBuilder(Particle.ITEM)
                .location(mob.getLocation())
                .count(16)
                .offset(.1, .1, .1)
                .extra(.7)
                .data(item)
                .spawn()
            ;
        }
    }

    public boolean isValid(){
        return altar == null || altar.isValidCache();
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(!event.getRightClicked().equals(mob)) return;
        onRightClick(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!event.getEntity().equals(mob)) return;
        if(!(event.getDamager() instanceof Player p)) return;
        onLeftClick(p);
    }

}

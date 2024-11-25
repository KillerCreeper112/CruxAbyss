package killercreepr.cruxabyss.core.entity.goal;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxGoalUtil;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.altar.AbyssAltar;
import killercreepr.cruxabyss.api.component.AbyssAltarCrystal;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxentities.modelengine.entity.mob.goal.CruxMobModeledGoal;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Candle;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbyssCrystalGoal extends AbyssAltarPlacedItemGoal {
    public static final GoalKey<Mob> KEY = GoalKey.of(Mob.class, Crux.key("abyss_crystal"));
    public AbyssCrystalGoal(@NotNull Mob mob) {
        super(KEY, mob);
    }

    public AbyssCrystalGoal(@NotNull GoalKey<Mob> key, @NotNull Mob mob) {
        super(key, mob);
    }

    public void setItem(@NotNull ItemStack item){
        applyModel(model ->{
            model.getBone("base").orElseThrow().setModel(item);
        });
    }

    @Override
    public void onRightClick(Player p) {

    }

    @Override
    public void onLeftClick(Player p) {
        explode();
    }

    protected AbyssAltar altar;
    public void setAltar(@NotNull AbyssAltar altar){
        this.altar = altar;
    }

    protected final int lifeSpan = 100;
    protected double speed = .7D;
    protected final double speedIncrease = .2;

    protected boolean explode = false;
    protected int checkValidCooldown = 0;
    @Override
    public void tick() {
        if(altar != null){
            if(checkValidCooldown > 0){
                checkValidCooldown--;
            }else{
                checkValidCooldown = 10;
                if(!altar.isValidCache()){
                    explode();
                    return;
                }
            }
        }

        playAnimation("size", false);
        if(explode){
            if(isPlayingAnimation("portal_spawn")) return;
            explode();
            spawnPortal();
            return;
        }
        setSpinSpeed(speed);
        playAnimation("float", false);

        speed += speedIncrease;
        tick++;
        if(tick >= lifeSpan){
            explode = true;
            stopAnimation("spin");
            playAnimation("portal_spawn", true);
        }
    }

    public void explode(){
        mob.remove();
        CreateSound.sound(Sound.ENTITY_ITEM_BREAK, 1.2f).playAt(mob.getLocation());
        CreateSound.sound(Sound.BLOCK_GLASS_BREAK, 1.1f).playAt(mob.getLocation());
        CreateSound.sound(Sound.ENTITY_GENERIC_EXPLODE, 2f).playAt(mob.getLocation());
        CreateSound.sound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.2f).playAt(mob.getLocation());
        applyModel(model ->{
            new ParticleBuilder(Particle.ITEM)
                .location(mob.getLocation())
                .count(20)
                .offset(.1, .1, .1)
                .extra(1.5)
                .data(model.getBone("base").orElseThrow().getModel())
                .spawn()
            ;
        });

        CruxLoc.getNearbyBlocks(mob.getLocation().getBlock(), 3).forEach(b ->{
            if(b.getBlockData() instanceof Candle c){
                c.setLit(false);
                b.setBlockData(c);
                new ParticleBuilder(Particle.SMOKE)
                    .location(b.getLocation().toCenterLocation())
                    .count(CruxMath.random(2, 4))
                    .offset(.05, .05, .05)
                    .extra(.3)
                    .spawn()
                ;
            }
        });
    }

    public void spawnPortal(){
        if(altar==null) return;

        BlockFace direction = altar.getDirection();
        Location portalSpawn = altar.center().getLocation().toCenterLocation();
        portalSpawn.setDirection(direction.getDirection());
        portalSpawn.add(0, 1, 0);

        if(!(AbyssMob.ALTAR_PORTAL.spawn(portalSpawn) instanceof Mob mob)) return;
        AbyssAltarPortalGoal goal = CruxGoalUtil.getGoal(mob, AbyssAltarPortalGoal.class);
        if(goal == null) return;
        applyModel(model ->{
            ItemStack item = model.getBone("base").orElseThrow().getModel();
            goal.setCrystal(item);

            if(item == null) return;
            CruxItem cruxItem = CruxItem.wrap(item);
            AbyssAltarCrystal crystal = cruxItem.get(AbyssComponents.ABYSS_ALTAR_CRYSTAL);
            if(crystal == null) return;
            new java.awt.Color(0x6DE96D);
            goal.setTeleportType(crystal.teleportType());
            Color color = crystal.portalColor();
            if(color != null) goal.setColor(color);

            /*if(AbyssItemTags.ABYSS_GEMS_DEATH.isTagged(item)) goal.setColor(Color.fromRGB(0x719BE7));
            if(AbyssItemTags.ABYSS_GEMS_SAFEZONE.isTagged(item)) goal.setColor(Color.fromRGB(0x6DE96D));*/
        });
    }

    public void setSpinSpeed(double speed){
        applyModel(model ->{
            IAnimationProperty property = model.getAnimationHandler().getAnimation("spin");
            if(property == null){
                property = model.getAnimationHandler().playAnimation(
                    "spin", 0D, 0D, speed, true
                );
                return;
            }
            property.setSpeed(speed);
        });
    }
}

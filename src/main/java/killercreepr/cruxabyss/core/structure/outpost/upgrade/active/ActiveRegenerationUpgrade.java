package killercreepr.cruxabyss.core.structure.outpost.upgrade.active;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.values.AbyssOutpostUpgradesCfg;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

public class ActiveRegenerationUpgrade extends SimpleActiveOutpostUpgrade {
    protected final ActiveAbyssOutpost outpost;
    public ActiveRegenerationUpgrade(int level, ActiveAbyssOutpost outpost) {
        super(level);
        this.outpost = outpost;
    }

    public void tick(AbyssOutpostData data, UPlayer owner, World world, BoundingBox box){
        world.getNearbyEntities(box, e ->{
            if(!(e instanceof Player)) return false;
            if(e.getUniqueId().equals(data.owner)) return true;
            return owner.hasFriend(e.getUniqueId()) || owner.isApartOfParty(e.getUniqueId());
        }).forEach(e ->{
            if(!(e instanceof LivingEntity le)) return;
            le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0));
        });
    }

    protected UPlayer cachedUPlayer;
    @Override
    public void tick(int tick, int rate) {
        //if(tick % 100 != 0) return;
        AbyssOutpostData data = outpost.getData();
        if(data.owner == null) return;

        UPlayer uPlayer = cachedUPlayer == null ? USurvivePlugin.inst().getPlayerManager().getPlayer(data.owner) : cachedUPlayer;
        if(uPlayer == null) return;
        if(cachedUPlayer == null) cachedUPlayer = uPlayer;

        World world = outpost.getActive().getChunk().getWorld();
        BoundingBox box = expand(outpost.getActive().getData().getBoundingBox().clone(), level);

        Crux.scheduler().runTask(() -> tick(data, uPlayer, world, box));
        double widthX = box.getWidthX()/2;
        double height = box.getHeight()/2;
        double widthZ = box.getWidthZ()/2;

        int particleCount = (int) ((widthX * height * widthZ) * .02);
        new ParticleBuilder(Particle.HAPPY_VILLAGER)
            .location(outpost.getActive().getData().getPosition().toLocation(world)
                .add(0, box.getHeight()/2, 0))
            .count(particleCount)
            .offset(widthX/2, height/2, widthZ/2)
            .extra(0)
            .spawn()
        ;
    }

    public BoundingBox expand(BoundingBox box, int level){
        AbyssOutpostUpgradesCfg cfg = (AbyssOutpostUpgradesCfg) CruxAbyss.inst().values();
        String equation = cfg.ABYSS_OUTPOST_UPGRADE_REGENERATION_RANGE().value();
        if(equation == null) return box;
        double expansion = CruxMath.evaluate(Crux.format().deserializeString(equation,
            TagContainer.string(Tag.parsed("level", level + ""))));
        //<level> * 16.5
        return box.expand(expansion);
    }
}

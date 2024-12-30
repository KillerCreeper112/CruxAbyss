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
import org.bukkit.Particle;
import org.bukkit.World;
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

    public void tick(AbyssOutpostData data, Player owner, World world, BoundingBox box){
        world.getNearbyEntities(box, e -> e.equals(owner)).forEach(e ->{
            owner.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0));
        });
    }

    @Override
    public void tick(int tick, int rate) {
        if(tick % 100 != 0) return;
        AbyssOutpostData data = outpost.getData();
        if(data.owner == null) return;
        //todo make parties and stuff
        Player p = Crux.getServer().getPlayer(data.owner);
        if(p == null) return;
        World world = outpost.getActive().getChunk().getWorld();
        if(!p.getWorld().equals(world)) return;
        BoundingBox box = expand(outpost.getActive().getData().getBoundingBox().clone(), level);

        Crux.scheduler().runTask(() -> tick(data, p, world, box));

        new ParticleBuilder(Particle.HAPPY_VILLAGER)
            .location(outpost.getActive().getData().getPosition().toLocation(world))
            .count(CruxMath.random(100, 200))
            .offset(box.getWidthX(), box.getHeight(), box.getWidthZ())
            .extra(.2)
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

package killercreepr.cruxabyss.core.component.impl;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.cruxblocks.api.event.CruxBlockBreakEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class FungireOreComponent implements CruxBlockComponent {
    @Override
    public void onBroken(@NotNull CruxBlockBreakEvent event) {
        Block b = event.getContext().getBlock();
        Location center = b.getLocation().toCenterLocation();
        new ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
            .location(center)
            .count(CruxMath.random(15, 25))
            .offset(1, 1, 1)
            .extra(.3)
            .colorTransition(org.bukkit.Color.fromRGB(0xAFCA2E),
                org.bukkit.Color.fromRGB(0x13C900))
            .spawn()
        ;

        if(CruxMath.testChance(25)){
            Location loc = b.getLocation().toCenterLocation().subtract(0, .3, 0);
            AbyssMob.FUNGALMORPH.spawn(loc);
        }
    }
}

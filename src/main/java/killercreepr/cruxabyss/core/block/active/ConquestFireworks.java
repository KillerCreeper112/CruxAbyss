package killercreepr.cruxabyss.core.block.active;

import killercreepr.crux.api.text.context.InputContext;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ConquestFireworks {
    protected final ActiveAbyssConquestNode node;
    protected final ActiveAbyssOutpost outpost;

    public ConquestFireworks(ActiveAbyssConquestNode node, ActiveAbyssOutpost outpost) {
        this.node = node;
        this.outpost = outpost;
    }

    private void onTakeOrDeactivation(){

    }

    public void onTakeOver(){
        onTakeOrDeactivation();
    }

    public void onDeactivate(){
        onTakeOrDeactivation();
    }

    protected int tick = 0;
    protected int spawnFireworks = CruxMath.random(10, 30);
    private void takingOrDeactivatingTick(boolean takingOver){
        tick++;
        if(tick < spawnFireworks) return;
        tick = 0;
        spawnFireworks = CruxMath.random(10, 30);
        spawnFireworks(CruxMath.random(1, 4), takingOver);
    }

    public void deactivatingTick(){
        takingOrDeactivatingTick(false);
    }

    public void takingOverTick(){
        takingOrDeactivatingTick(true);
    }

    public void spawnFireworks(int amount, boolean takingOver){
        while(amount > 0){
            amount--;
            spawnFirework(takingOver);
        }
    }

    public void spawnFirework(boolean takingOver){
        spawnFirework(findLocation(), takingOver);
    }

    public Location findLocation(){
        BoundingBox box = outpost.getData().getBoundingBox();
        NumberProvider range = node.getNode().getFireworksRange();
        NumberProvider rangeY = node.getNode().getFireworksRangeY();
        double paddingX = ((box.getWidthX() - 5));
        double paddingZ = ((box.getWidthZ() - 5));
        InputContext ctx = InputContext.simple(TagContainer.string(
            Tag.parsed("padding_x", paddingX + ""),
            Tag.parsed("padding_z", paddingZ + "")
        ));
        Location l = node.getBlock().getLocation().toCenterLocation()
            .add(
                (range.sample(ctx).doubleValue()) * (CruxMath.random().nextBoolean() ? -1 : 1),
                rangeY.value().doubleValue(),
                (range.sample(ctx).doubleValue()) * (CruxMath.random().nextBoolean() ? -1 : 1)
            );
        return l;
    }

    public static final List<Color> colors = List.of(
        Color.WHITE,
        Color.fromRGB(0xF5F367), //yellow
        Color.fromRGB(0xF56B6B), //red
        Color.fromRGB(0xF59146) //orange
    );
    public Color buildColor(boolean takingOver){
        if(takingOver) return buildColor();
        return colors.get(CruxMath.random(0, 1)); //only return white and yellow
    }

    public Color buildColor(){
        return colors.get(CruxMath.random(0, colors.size()-1));
    }

    public Color[] buildColors(boolean takingOver){
        return buildColors(CruxMath.random(1, colors.size()), takingOver);
    }


    public Color[] buildColors(int amount, boolean takingOver){
        List<Color> list = new ArrayList<>();
        while(amount > 0){
            amount--;
            list.add(buildColor(takingOver));
        }
        return list.toArray(new Color[0]);
    }

    public FireworkEffect buildFireworkEffect(boolean takingOver){
        FireworkEffect.Builder builder = FireworkEffect.builder()
            .trail(true)
            .with(FireworkEffect.Type.values()[CruxMath.random(0, FireworkEffect.Type.values().length-1)])
            .flicker(CruxMath.random().nextBoolean())
            .withColor(buildColors(takingOver));
        if(CruxMath.random().nextBoolean()){
            builder.withFade(buildColors(takingOver));
        }
        return builder.build();
    }

    public void spawnFirework(Location l, boolean takingOver){
        l.getWorld().spawn(l, Firework.class, e ->{
            int ticksToDetonate = 10 + (CruxMath.random(0, 5) + 1) + CruxMath.random(0, 5) + CruxMath.random(0, 6);
            e.setTicksToDetonate(ticksToDetonate);
            e.setVelocity(new Vector(0, 1, 0));
            FireworkMeta meta = e.getFireworkMeta();
            meta.addEffect(buildFireworkEffect(takingOver));
            e.setFireworkMeta(meta);
        });
    }
}

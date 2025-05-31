package killercreepr.cruxabyss.core.component.impl;

import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.component.CruxBlockComponent;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RequireOutpostComponent implements CruxBlockComponent {
    protected final boolean friendly;

    public RequireOutpostComponent(boolean friendly) {
        this.friendly = friendly;
    }

    @Nullable
    @Override
    public Boolean canPlace(@NotNull BlockContext ctx, @NotNull CruxBlock block) {
        if(!(ctx.getMiner() instanceof EntityMiner miner)) return null;
        Entity e = miner.getEntity();

        var world = CruxCore.core().worldManager().getWorld(ctx.getBlock().getWorld().key());
        if(world == null){
            msg(e);
            return false;
        }
        var structures = world.getModule(StructureWorldModule.class);
        if(structures == null){
            msg(e);
            return false;
        }
        Vector vec = ctx.getBlock().getLocation().toVector();
        UUID uuid = e.getUniqueId();
        var stored = structures.getStored(
            StoredStructure.class, check ->{
                if(!check.has(AbyssComponents.ABYSS_OUTPOST_DATA)) return false;
                BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
                if(!box.contains(vec)) return false;
                var data = check.get(AbyssComponents.ABYSS_OUTPOST_DATA);
                if(friendly && !data.isMemberOrOwner(uuid)) return false;
                return true;
            }
        );
        if(stored.isEmpty()){
            msg(e);
            return false;
        }
        return null;
    }

    public void msg(Entity e){
        if(friendly){
            Lang.ABYSS_REQUIRE_OUTPOST_PLACE_BLOCK_FRIENDLY.use(e);
        }else Lang.ABYSS_REQUIRE_OUTPOST_PLACE_BLOCK.use(e);
    }
}

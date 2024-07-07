package killercreepr.cruxabyss.entity.mob;

import killercreepr.crux.registry.KeyedRegistry;
import killercreepr.cruxabyss.entity.mob.type.*;
import killercreepr.cruxabyss.game.GameManager;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbyssMob extends CruxMob {
    KeyedRegistry<CruxMob> REGISTRY = CruxEntityRegistries.ENTITIES;
    AbyssCrimsonEye CRIMSON_EYE = REGISTRY.register(new AbyssCrimsonEye());
    AbyssMoose MOOSE = REGISTRY.register(new AbyssMoose());
    AbyssGroundDweller GROUND_DWELLER = REGISTRY.register(new AbyssGroundDweller());
    AbyssCharredBones CHARRED_BONES = REGISTRY.register(new AbyssCharredBones());
    AbyssCapgras CAPGRAS = REGISTRY.register(new AbyssCapgras());

    @NotNull
    default Entity spawn(@NotNull Location at){
        return spawn(null, at);
    }
    @NotNull Entity spawn(@Nullable GameManager game, @NotNull Location at);
}

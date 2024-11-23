package killercreepr.cruxabyss.core.entity.mob;

import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.cruxabyss.core.entity.mob.type.*;
import killercreepr.cruxabyss.core.entity.type.AbyssAltarPortal;
import killercreepr.cruxabyss.core.entity.type.AbyssCrystal;
import killercreepr.cruxabyss.core.entity.type.AbyssReturnPortal;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbyssMob extends CruxMob {
    KeyedRegistry<CruxMob> REGISTRY = CruxEntityRegistries.ENTITIES;
    AbyssalEyeVine ABYSSAL_EYE_VINE = REGISTRY.register(new AbyssalEyeVine());
    AbyssMoose MOOSE = REGISTRY.register(new AbyssMoose());
    AbyssGroundDweller GROUND_DWELLER = REGISTRY.register(new AbyssGroundDweller());
    AbyssCharredBones CHARRED_BONES = REGISTRY.register(new AbyssCharredBones());
    AbyssCapgras CAPGRAS = REGISTRY.register(new AbyssCapgras());

    AbyssAltarPortal ALTAR_PORTAL = REGISTRY.register(new AbyssAltarPortal());
    AbyssReturnPortal RETURN_PORTAL = REGISTRY.register(new AbyssReturnPortal());
    AbyssCrystal ABYSS_CRYSTAL = REGISTRY.register(new AbyssCrystal());
    @NotNull Entity spawn(@Nullable AbyssWorld world, @NotNull Location at);
}

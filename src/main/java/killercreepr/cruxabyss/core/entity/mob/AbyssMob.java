package killercreepr.cruxabyss.core.entity.mob;

import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.core.Crux;
import killercreepr.cruxabyss.api.entity.type.AltarPlacedItem;
import killercreepr.cruxabyss.core.entity.mob.type.*;
import killercreepr.cruxabyss.core.entity.type.*;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxentities.registries.CruxEntityRegistries;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbyssMob extends CruxMob {
    static void register(){

    }
    KeyedRegistry<CruxMob> REGISTRY = CruxEntityRegistries.ENTITIES;
    CruxMob ABYSSAL_EYE_VINE = REGISTRY.register(new AbyssalEyeVine());
    CruxMob MOOSE = REGISTRY.register(new AbyssMoose());
    CruxMob GROUND_DWELLER = REGISTRY.register(new AbyssGroundDweller());
    CruxMob CHARRED_BONES = REGISTRY.register(new AbyssCharredBones());
    CruxMob CAPGRAS = REGISTRY.register(new AbyssCapgras());
    CruxMob PLAGUE_STALKER = REGISTRY.register(new PlagueStalker());
    CruxMob SCOURGER = REGISTRY.register(new Scourger());
    CruxMob TOXICATOR = REGISTRY.register(new Toxicator());
    CruxMob PLAGUE_TYRANT = REGISTRY.register(new PlagueTyrant());
    CruxMob PLAGUEWING = REGISTRY.register(new Plaguewing());
    CruxMob EMBER_LEAPER = REGISTRY.register(new EmberLeaper());
    CruxMob TOXINTRAWL = REGISTRY.register(new Toxintrawl());

    CruxMob SCOURGER_BULLET = REGISTRY.register(new ScourgerBullet());
    CruxMob SCOURGER_BULLET_LARGE = REGISTRY.register(new ScourgerBulletLarge());

    AbyssAltarPortal ALTAR_PORTAL = REGISTRY.register(new AbyssAltarPortal());
    AbyssReturnPortal RETURN_PORTAL = REGISTRY.register(new AbyssReturnPortal());
    AbyssCrystal ABYSS_CRYSTAL = REGISTRY.register(new AbyssCrystal());
    AltarPlacedItem ALTAR_PLACED_ITEM = REGISTRY.register(new AbyssAltarPlacedItem(Crux.key("altar_placed_item")));
    @NotNull Entity spawn(@Nullable AbyssWorld world, @NotNull Location at);
}

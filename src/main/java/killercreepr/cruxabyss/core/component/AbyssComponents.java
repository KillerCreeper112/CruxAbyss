package killercreepr.cruxabyss.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxabyss.api.component.AbyssAltarCrystal;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.core.component.impl.*;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.loot.AbyssOutpostLootHolder;
import killercreepr.cruxabyss.core.structure.outpost.loot.AbyssOutpostLootHolderData;
import killercreepr.cruxabyss.core.structure.outpost.loot.ActiveAbyssOutpostLootHolder;
import killercreepr.cruxabyss.core.structure.safezone.AbyssSafeZone;
import killercreepr.cruxabyss.core.structure.safezone.AbyssSafeZoneData;
import killercreepr.cruxblocks.core.structure.modules.PlaceCustomBlocksModule;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.UnaryOperator;


public class AbyssComponents {
    public static void register(){}
    public static final DataComponentType<AbyssConquestNode> ABYSS_CONQUEST_NODE = register("abyss_conquest_node", builder ->
        builder);
    public static final DataComponentType<AbyssEntitySpawner> ABYSS_ENTITY_SPAWNER = register("abyss_entity_spawner", builder ->
        builder);
    public static final DataComponentType<AbyssPortalGateway> ABYSS_PORTAL_GATEWAY = register("abyss_portal_gateway", builder ->
        builder);
    public static final DataComponentType<AbyssAltarCrystal> ABYSS_ALTAR_CRYSTAL = register("abyss_altar_crystal", builder ->
        builder.persistTextParser(PersistTextParser.mapBuilder(AbyssAltarCrystal.class)
            .field("teleport_type", TextInputField.field(PersistTextParser.STRING, AbyssAltarCrystal::teleportType))
            .field("portal_color", TextInputField.field(PersistTextParser.COLOR, AbyssAltarCrystal::portalColor))
            .apply(ctx ->{
                String teleportType = ctx.getOptional("teleport_type");
                Color portalColor = ctx.getOptional("portal_color");
                return new AbyssAltarCrystalComponent(
                    teleportType,
                    portalColor
                );
            }).createInput(Crux.key("abyss_altar_crystal")))
    );
    public static final DataComponentType<AbyssOutpostData> ABYSS_OUTPOST_DATA = register("abyss_outpost_data", builder -> builder);
    public static final DataComponentType<AbyssOutpost> ABYSS_OUTPOST = register("abyss_outpost", builder -> builder);
    public static final DataComponentType<ActiveAbyssOutpost> ACTIVE_ABYSS_OUTPOST = register("active_abyss_outpost", builder -> builder);


    public static final DataComponentType<AbyssSafeZone> ABYSS_SAFE_ZONE = register("abyss_safe_zone", builder -> builder);
    public static final DataComponentType<AbyssSafeZoneData> ABYSS_SAFE_ZONE_DATA = register("abyss_safe_zone_data", builder -> builder);

    public static final DataComponentType<AbyssOutpostLootHolderData> ABYSS_OUTPOST_LOOT_HOLDER_DATA = register("abyss_outpost_loot_holder_data", builder -> builder);
    public static final DataComponentType<AbyssOutpostLootHolder> ABYSS_OUTPOST_LOOT_HOLDER = register("abyss_outpost_loot_holder", builder -> builder);
    public static final DataComponentType<ActiveAbyssOutpostLootHolder> ACTIVE_ABYSS_OUTPOST_LOOT_HOLDER = register("active_abyss_outpost_loot_holder", builder -> builder);
    public static final DataComponentType<Vector> ABYSS_HOLOGRAM_OFFSET = register("abyss_hologram_offset", builder -> builder);
    public static final DataComponentType<String> ABYSS_HOLOGRAM_FORMAT = register("abyss_hologram_format", builder -> builder);
    public static final DataComponentType<PlagueWingGliderComponent> PLAGUE_WING_GLIDER = register("plague_wing_glider", builder -> builder
        .persistTextParser(PersistTextParser.mapBuilder(PlagueWingGliderComponent.class)
            .field("move_speed", TextInputField.field(PersistTextParser.FLOAT, PlagueWingGliderComponent::getMoveSpeed))
            .field("min_fall_distance", TextInputField.field(PersistTextParser.FLOAT, PlagueWingGliderComponent::getMinFallDistance))
            .field("min_empty_block_distance", TextInputField.field(PersistTextParser.INTEGER, PlagueWingGliderComponent::getMinEmptyBlockDistance))
            .field("glider_potions", TextInputField.field(ComponentInputParsers.LIST.POTION_EFFECT, PlagueWingGliderComponent::getGliderPotions))
            .field("item_damage", TextInputField.field(PersistTextParser.INTEGER, PlagueWingGliderComponent::getItemDamagePerSecond))
            .apply(ctx ->{
                float moveSpeed = ctx.getOptional("move_speed", .1f);
                List<PotionEffect> gliderPotions = ctx.getOptional("glider_potions");
                return new PlagueWingGliderComponent(
                    moveSpeed, gliderPotions,
                    ctx.getOptional("min_fall_distance", 8f),
                    ctx.getOptional("min_empty_block_distance", 4),
                    ctx.getOptional("item_damage", 1)
                );
            }).createInput(Crux.key("plague_wing_glider")))
    );
    public static final DataComponentType<SporeburstChargeComponent> SPOREBURST_CHARGE = register("sporeburst_charge", builder ->
        builder.persistTextParser(PersistTextParser.mapBuilder(SporeburstChargeComponent.class)
            .field("cooldown", TextInputField.field(PersistTextParser.INTEGER, SporeburstChargeComponent::getCooldown))
            .apply(ctx ->{
                return new SporeburstChargeComponent(ctx.getOptional("cooldown", 10));
            }).createInput(Crux.key("sporeburst_charge")))
    );

    public static final DataComponentType<TeleporterComponent> TELEPORT_ABYSS_WORLD = register("teleport/abyss_world", builder ->
        builder);
    public static final DataComponentType<ToxsporeComponent> BLOCK_TOXSPORE = register("block/toxspore", builder ->
        builder);
    public static final DataComponentType<FungireOreComponent> BLOCK_FUNGIRE_ORE = register("block/fungire_ore", builder ->
        builder);
    public static final DataComponentType<PlaceCustomBlocksModule> STRUCTURE_REPLACEABLE_CUSTOM_BLOCKS = register("structure/place_custom_blocks/replaceable", builder ->
        builder);
    public static final DataComponentType<TeleporterComponent> TELEPORT_OUTPOST_ABYSSAL_RECALL = register("teleport/outpost_abyssal_recall", builder ->
        builder);

    public static final DataComponentType<AbyssOutpostCaptureEvent> LOOT_CAPTURED_ABYSS_OUTPOST = register("loot/abyss_outpost_capture", builder -> builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}

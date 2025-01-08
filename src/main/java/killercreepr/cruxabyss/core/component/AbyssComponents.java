package killercreepr.cruxabyss.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxabyss.api.component.AbyssAltarCrystal;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.core.component.impl.AbyssAltarCrystalComponent;
import killercreepr.cruxabyss.core.component.impl.AbyssConquestNode;
import killercreepr.cruxabyss.core.component.impl.AbyssEntitySpawner;
import killercreepr.cruxabyss.core.component.impl.AbyssPortalGateway;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.structure.outpost.loot.AbyssOutpostLootHolder;
import killercreepr.cruxabyss.core.structure.outpost.loot.AbyssOutpostLootHolderData;
import killercreepr.cruxabyss.core.structure.outpost.loot.ActiveAbyssOutpostLootHolder;
import killercreepr.cruxteleport.api.component.TeleporterComponent;
import org.bukkit.Color;
import org.bukkit.util.Vector;

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

    public static final DataComponentType<AbyssOutpostLootHolderData> ABYSS_OUTPOST_LOOT_HOLDER_DATA = register("abyss_outpost_loot_holder_data", builder -> builder);
    public static final DataComponentType<AbyssOutpostLootHolder> ABYSS_OUTPOST_LOOT_HOLDER = register("abyss_outpost_loot_holder", builder -> builder);
    public static final DataComponentType<ActiveAbyssOutpostLootHolder> ACTIVE_ABYSS_OUTPOST_LOOT_HOLDER = register("active_abyss_outpost_loot_holder", builder -> builder);
    public static final DataComponentType<Vector> ABYSS_HOLOGRAM_OFFSET = register("abyss_hologram_offset", builder -> builder);
    public static final DataComponentType<String> ABYSS_HOLOGRAM_FORMAT = register("abyss_hologram_format", builder -> builder);

    public static final DataComponentType<TeleporterComponent> TELEPORT_ABYSS_WORLD = register("teleport/abyss_world", builder ->
        builder);

    public static final DataComponentType<AbyssOutpostCaptureEvent> LOOT_CAPTURED_ABYSS_OUTPOST = register("loot/abyss_outpost_capture", builder -> builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}

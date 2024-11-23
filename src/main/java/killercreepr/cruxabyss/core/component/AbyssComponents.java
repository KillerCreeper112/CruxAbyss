package killercreepr.cruxabyss.core.component;

import killercreepr.crux.api.component.DataComponentType;
import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.registries.CruxRegistries;
import killercreepr.cruxabyss.api.component.AbyssAltarCrystal;
import killercreepr.cruxabyss.core.component.impl.AbyssAltarCrystalComponent;
import killercreepr.cruxabyss.core.component.impl.AbyssConquestNode;
import killercreepr.cruxabyss.core.component.impl.AbyssEntitySpawner;
import killercreepr.cruxabyss.core.component.impl.AbyssPortalGateway;
import org.bukkit.Color;

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
            }).createInput(Crux.key("abyss_altar_item")))
    );
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}

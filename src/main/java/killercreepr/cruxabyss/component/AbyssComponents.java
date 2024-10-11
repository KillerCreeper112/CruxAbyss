package killercreepr.cruxabyss.component;

import killercreepr.crux.Crux;
import killercreepr.crux.component.DataComponentType;
import killercreepr.crux.registries.CruxRegistries;
import killercreepr.cruxabyss.component.impl.AbyssConquestNode;

import java.util.function.UnaryOperator;

public class AbyssComponents {
    public static final DataComponentType<AbyssConquestNode> ABYSS_CONQUEST_NODE = register("abyss_conquest_node", builder ->
        builder);
    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return CruxRegistries.DATA_COMPONENT_TYPE.register(Crux.key(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}

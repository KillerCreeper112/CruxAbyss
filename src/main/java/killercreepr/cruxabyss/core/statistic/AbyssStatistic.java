package killercreepr.cruxabyss.core.statistic;

import killercreepr.crux.core.Crux;
import killercreepr.cruxstatistics.api.statistic.CruxStatisticType;
import killercreepr.cruxstatistics.core.registries.CruxStatisticRegistries;

public class AbyssStatistic {
    public static void register(){}

    public static final CruxStatisticType<?> ABYSS_TRAVEL = register(CruxStatisticType.statisticType(Crux.key("abyss_travel")));

    private static CruxStatisticType<?> register(CruxStatisticType<?> type){
        return CruxStatisticRegistries.STATISTIC_TYPE.register(type);
    }
}

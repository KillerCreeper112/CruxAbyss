package killercreepr.cruxabyss.core.statistic;

import killercreepr.crux.core.Crux;
import killercreepr.cruxstatistics.api.statistic.CruxStatisticType;
import killercreepr.cruxstatistics.core.registries.CruxStatisticRegistries;

public class AbyssStatistic {
    public static void register(){}

    public static final CruxStatisticType<?> ABYSS_TRAVEL = register(CruxStatisticType.statisticType(Crux.key("abyss_travel")));
    public static final CruxStatisticType<?> ABYSS_OUTPOSTS_CAPTURED = register(CruxStatisticType.statisticType(Crux.key("abyss_outposts_captured")));
    public static final CruxStatisticType<?> ABYSS_SURVIVE_SECONDS = register(CruxStatisticType.statisticType(Crux.key("abyss_survive_seconds")));
    public static final CruxStatisticType<?> CURRENT_ABYSS_SURVIVE_SECONDS = register(CruxStatisticType.statisticType(Crux.key("current_abyss_survive_seconds")));

    private static CruxStatisticType<?> register(CruxStatisticType<?> type){
        return CruxStatisticRegistries.STATISTIC_TYPE.register(type);
    }
}

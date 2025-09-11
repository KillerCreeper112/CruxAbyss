package killercreepr.cruxabyss.core.challenge;

import killercreepr.crux.api.data.PluginLoadable;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxchallenges.api.challenge.CruxChallenge;
import killercreepr.cruxchallenges.api.challenge.CruxChallengeType;
import killercreepr.cruxchallenges.api.challenge.manager.ScheduledChallenge;
import killercreepr.cruxchallenges.core.CruxChallengesPlugin;
import killercreepr.cruxchallenges.core.time.RelativeTimeBuilder;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.common.file.DataFile;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

public class AbyssChallengeManager implements PluginLoadable {
    private static AbyssChallengeManager MANAGER;
    public static void setManager(AbyssChallengeManager manager){
        MANAGER = manager;
    }

    public static AbyssChallengeManager getMain(){
        return MANAGER;
    }

    protected LootTable<ChallengeRoll> availableChallenges;
    private static final long TIME_PERIOD = 1 * 1728000L;

    public AbyssChallengeManager(LootTable<ChallengeRoll> availableChallenges) {
        this.availableChallenges = availableChallenges;
    }

    public void setAvailableChallenges(LootTable<ChallengeRoll> availableChallenges) {
        this.availableChallenges = availableChallenges;
    }

    public LootTable<ChallengeRoll> getAvailableChallenges() {
        return availableChallenges;
    }

    protected long lastRolled;
    public void tick(){
        if(!shouldRoll()){
            return;
        }
        lastRolled = System.currentTimeMillis();
        roll();
    }

    public boolean shouldRoll(){
        return !CruxMath.hasOccurredWithin(lastRolled, TIME_PERIOD);
    }

    public void roll(){
        long time = System.currentTimeMillis();
        generateFullSchedule(time).forEach(challenge ->{
            CruxChallengesPlugin.inst().getChallengeManager().getScheduler().scheduleChallenge(challenge);
        });
    }

    private long randomGap() {
        return CruxMath.randomSkewed(0, 72000 * 2, 0.2) * 50L;
    }

    public Collection<ScheduledChallenge> generateFullSchedule(long time) {
        List<ScheduledChallenge> schedule = new ArrayList<>();
        long end = time + (TIME_PERIOD * 50L);

        while (time < end) {
            Collection<ChallengeRoll> rolls = generateRandomRoll();
            Set<CruxChallengeType> seenTypes = new HashSet<>();
            List<ChallengeRoll> finalRolls = new ArrayList<>();

            for (ChallengeRoll roll : rolls) {
                CruxChallengeType type = roll.challenge().getType();
                if (type == null || seenTypes.add(type)) {
                    finalRolls.add(roll);
                }
            }

            long nextStartTime = time;
            long maxEndThisBatch = time;

            long longestDuration = 0;
            for (ChallengeRoll roll : finalRolls) {
                long duration = randomDuration();
                if(duration > longestDuration){
                    longestDuration = duration;
                }

                // find latest end time of any similar challenge
                long latestEnd = findLatestSimilarEndTime(schedule, roll.challenge());

                long startTime = Math.max(nextStartTime, latestEnd);
                long challengeEnd = startTime + duration;

                var built = ScheduledChallenge.scheduledChallenge(
                    roll.challenge(),
                    Instant.ofEpochMilli(startTime),
                    new RelativeTimeBuilder(NumberProvider.constant(duration/50L))
                );

                schedule.add(built);

                // track how far this batch stretches
                if (challengeEnd > maxEndThisBatch) {
                    maxEndThisBatch = challengeEnd;
                }
            }

            // advance the cursor for next batch
            if (CruxMath.random().nextBoolean()) {
                long overlapPoint = (long) (longestDuration * (0.25 + CruxMath.random().nextDouble() * 0.5));
                time += overlapPoint;
            } else {
                time = maxEndThisBatch + (CruxMath.random().nextBoolean() ? (randomGap()) : 0);
            }
        }
        return schedule;
    }

    private long findLatestSimilarEndTime(Collection<ScheduledChallenge> schedule, CruxChallenge newChallenge) {
        long latest = 0;
        for (ScheduledChallenge sc : schedule) {
            if (!isSimilar(sc.getChallenge(), newChallenge)) continue;
            long endTime = sc.getExpireTimeOrChallengeExpire()
                .createTime(sc.getTime())
                .toEpochMilli();
            if (endTime > latest) {
                latest = endTime;
            }
        }
        return latest;
    }


    public ScheduledChallenge getLongest(Collection<ScheduledChallenge> lastScheduled, Predicate<ScheduledChallenge> filter){
        long time = 0;
        ScheduledChallenge max = null;
        for (ScheduledChallenge check : lastScheduled) {
            if(!filter.test(check)) continue;
            long checkTime = check.getExpireTimeOrChallengeExpire().createTime(check.getTime()).toEpochMilli() - check.getTime().toEpochMilli();
            if(checkTime > time){
                time = checkTime;
                max = check;
            }
        }
        return max;
    }

    public Collection<ChallengeRoll> generateRandomRoll(){
        LootContext ctx = LootContext.empty();
        return availableChallenges.populateLoot(ctx);
    }

    public Collection<ScheduledChallenge> generateSchedule(long time){
        Collection<ScheduledChallenge> list = new ArrayList<>();
        LootContext ctx = LootContext.empty();
        for (ChallengeRoll roll : availableChallenges.populateLoot(ctx)) {
        }
        return list;
    }

    private long randomDuration() {
        return CruxMath.randomSkewed(1200, 72000 * 12, 0.35D) * 50L;
    }

    public boolean isSimilar(CruxChallenge first, CruxChallenge second){
        if(first.getType() == null && second.getType() == null) return false;
        return Objects.equals(first.getType(), second.getType());
    }

    public DataFile saveFile(Plugin plugin, boolean createIfNeeded){
        return BukkitDataFile.parseFromGeneralPath(CruxFolder.file(plugin, "data/abyss_challenges_manager.json"), createIfNeeded);
    }

    @Override
    public void save(@NotNull Plugin plugin) {
        DataFile file = saveFile(plugin, true);
        if(file == null) return;
        file.serialize("last_rolled", lastRolled);
        file.save();
    }

    @Override
    public void load(@NotNull Plugin plugin) {
        DataFile file = saveFile(plugin, false);
        if(file == null) return;
        file.deserializeOrDefault("last_rolled", Long.class, 0L);
        file.close();
    }
}

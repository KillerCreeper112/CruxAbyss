package killercreepr.cruxabyss.core.challenge;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.LootTable;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxchallenges.api.challenge.CruxChallenge;
import killercreepr.cruxchallenges.api.challenge.manager.ScheduledChallenge;
import killercreepr.cruxchallenges.core.CruxChallengesPlugin;
import killercreepr.cruxchallenges.core.time.RelativeTimeBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AbyssChallengeManager {
    private static AbyssChallengeManager MANAGER;
    public static void setManager(AbyssChallengeManager manager){
        MANAGER = manager;
    }

    public static AbyssChallengeManager getMain(){
        return MANAGER;
    }

    protected LootTable<ChallengeRoll> availableChallenges;
    private static final long TWO_WEEKS = 14 * 24000L;

    public AbyssChallengeManager(LootTable<ChallengeRoll> availableChallenges) {
        this.availableChallenges = availableChallenges;
    }

    public void setAvailableChallenges(LootTable<ChallengeRoll> availableChallenges) {
        this.availableChallenges = availableChallenges;
    }

    public LootTable<ChallengeRoll> getAvailableChallenges() {
        return availableChallenges;
    }

    public void tick(){
    }

    public void roll(){
        long time = System.currentTimeMillis();
        generateFullSchedule(time).forEach(challenge ->{
            CruxChallengesPlugin.inst().getChallengeManager().getScheduler().scheduleChallenge(challenge);
        });
    }

    private long randomGap() {
        return CruxMath.random(0, 72000 * 2);
    }

    public Collection<ScheduledChallenge> generateFullSchedule(long time){
        List<ScheduledChallenge> schedule = new ArrayList<>();
        long end = time + TWO_WEEKS;

        while (time < end) {

            Collection<ChallengeRoll> rolls = generateRandomRoll();
            Collection<ChallengeRoll> finalRolls = rolls.stream().filter(ch -> rolls.stream()
                .noneMatch(sched -> isSimilar(ch.challenge(), sched.challenge()))).toList();

            long duration = randomDuration();
            long challengeEnd = time + duration;

            for (ChallengeRoll roll : finalRolls) {
                schedule.add(ScheduledChallenge.scheduledChallenge(roll.challenge(), Instant.ofEpochMilli(time),
                    new RelativeTimeBuilder(NumberProvider.constant(challengeEnd))));
            }

            // move forward by event duration + random gap, but allow overlaps randomly
            if (CruxMath.random().nextBoolean()) {
                // Overlap: start next event before current ends
                time += duration / 2;
            } else {
                // Sequential: wait until event finishes, then add small gap
                time = challengeEnd + randomGap();
            }
        }

        return schedule;
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
        return CruxMath.random(72000 * 2, 72000 * 24);
    }

    public boolean isSimilar(CruxChallenge first, CruxChallenge second){
        if(first.getType() == null && second.getType() == null) return false;
        return Objects.equals(first.getType(), second.getType());
    }
}

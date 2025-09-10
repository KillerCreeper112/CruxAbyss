package killercreepr.cruxabyss.core.challenge;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxchallenges.api.challenge.CruxChallenge;
import killercreepr.cruxchallenges.core.registries.ChallengeRegistries;
import net.kyori.adventure.key.Key;

public class ChallengeRoll {
    public final Key challenge;
    public final NumberProvider duration;
    public final NumberProvider startTimeOffset;

    public ChallengeRoll(Key challenge, NumberProvider duration, NumberProvider startTimeOffset) {
        this.challenge = challenge;
        this.duration = duration;
        this.startTimeOffset = startTimeOffset;
    }

    public CruxChallenge challenge(){
        return ChallengeRegistries.CHALLENGE.get(challenge);
    }
}

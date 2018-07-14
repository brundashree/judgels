package judgels.uriel.contest.scoreboard;

import javax.inject.Inject;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.contest.scoreboard.icpc.IcpcScoreboardProcessor;
import judgels.uriel.contest.scoreboard.ioi.IoiScoreboardProcessor;

public class ScoreboardProcessorRegistry {
    @Inject
    public ScoreboardProcessorRegistry() {}

    public ScoreboardProcessor get(ContestStyle style) {
        if (style == ContestStyle.ICPC) {
            return new IcpcScoreboardProcessor();
        } else if (style == ContestStyle.IOI) {
            return new IoiScoreboardProcessor();
        }
        throw new IllegalArgumentException();
    }
}

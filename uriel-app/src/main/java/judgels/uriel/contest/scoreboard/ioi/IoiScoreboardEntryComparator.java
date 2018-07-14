package judgels.uriel.contest.scoreboard.ioi;

import java.util.Optional;
import judgels.uriel.api.contest.scoreboard.IoiScoreboard.IoiScoreboardEntry;
import judgels.uriel.contest.scoreboard.ScoreboardEntryComparator;

public interface IoiScoreboardEntryComparator extends ScoreboardEntryComparator<IoiScoreboardEntry> {
    @Override
    default int compareWithTieBreakerForEqualRanks(IoiScoreboardEntry entry1, IoiScoreboardEntry entry2) {
        if (entry1.getLastAffectingPenalty() != entry2.getLastAffectingPenalty()) {
            return Long.compare(entry1.getLastAffectingPenalty(), entry2.getLastAffectingPenalty());
        }

        int submittedProblems1 = (int) entry1.getScores().stream().filter(Optional::isPresent).count();
        int submittedProblems2 = (int) entry2.getScores().stream().filter(Optional::isPresent).count();

        // prioritize entry which has more number of problems that have at least one submission
        if (submittedProblems1 != submittedProblems2) {
            return Integer.compare(submittedProblems2, submittedProblems1);
        }

        return entry1.getContestantJid().compareTo(entry2.getContestantJid());
    }
}

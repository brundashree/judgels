package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.Verdict;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGrading.class)
public interface Grading {
    long getId();
    String getJid();
    Verdict getVerdict();
    int getScore();
    GradingResultDetails getDetails();

    class Builder extends ImmutableGrading.Builder {}
}

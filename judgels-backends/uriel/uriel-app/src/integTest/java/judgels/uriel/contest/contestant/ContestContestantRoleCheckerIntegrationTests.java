package judgels.uriel.contest.contestant;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.persistence.TestClock.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.api.user.rating.UserRating;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import judgels.uriel.contest.AbstractContestRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantRoleCheckerIntegrationTests extends AbstractContestRoleCheckerIntegrationTests {
    private ContestContestantRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestContestantRoleChecker();
    }

    @Test
    void register() {
        Contest contestAFinished = contestStore.createContest(
                new ContestCreateData.Builder().slug("contest-a-finished").build());
        contestAFinished = contestStore.updateContest(
                contestAFinished.getJid(),
                new ContestUpdateData.Builder().beginTime(NOW.minus(10, HOURS)).build()).get();

        Contest contestADiv1 = contestStore.createContest(
                new ContestCreateData.Builder().slug("contest-a-div-1").build());

        moduleStore.upsertRegistrationModule(contestAFinished.getJid());
        moduleStore.upsertRegistrationModule(contestADiv1.getJid());
        moduleStore.upsertDivisionModule(contestADiv1.getJid(), new DivisionModuleConfig.Builder().division(1).build());

        assertThat(checker.canRegister(ADMIN, empty(), contestA)).isFalse();
        assertThat(checker.canRegister(ADMIN, empty(), contestAStarted)).isFalse();
        assertThat(checker.canRegister(ADMIN, empty(), contestAFinished)).isFalse();
        assertThat(checker.canRegister(ADMIN, empty(), contestB)).isFalse();
        assertThat(checker.canRegister(ADMIN, empty(), contestBStarted)).isFalse();
        assertThat(checker.canRegister(ADMIN, empty(), contestC)).isFalse();

        UserRating rating1500 = new UserRating.Builder().publicRating(1500).hiddenRating(1500).build();
        UserRating rating2000 = new UserRating.Builder().publicRating(2000).hiddenRating(2000).build();
        UserRating rating2100 = new UserRating.Builder().publicRating(2100).hiddenRating(2100).build();

        assertThat(checker.canRegister(USER, empty(), contestA)).isTrue();
        assertThat(checker.canRegister(USER, empty(), contestAStarted)).isTrue();
        assertThat(checker.canRegister(USER, empty(), contestAFinished)).isFalse();
        assertThat(checker.canRegister(USER, empty(), contestADiv1)).isFalse();
        assertThat(checker.canRegister(USER, of(rating1500), contestADiv1)).isFalse();
        assertThat(checker.canRegister(USER, of(rating2000), contestADiv1)).isTrue();
        assertThat(checker.canRegister(USER, of(rating2100), contestADiv1)).isTrue();
        assertThat(checker.canRegister(USER, empty(), contestB)).isFalse();
        assertThat(checker.canRegister(USER, empty(), contestBStarted)).isFalse();
        assertThat(checker.canRegister(USER, empty(), contestC)).isFalse();

        assertThat(checker.canRegister(CONTESTANT, empty(), contestA)).isTrue();
        assertThat(checker.canRegister(CONTESTANT, empty(), contestAStarted)).isTrue();
        assertThat(checker.canRegister(CONTESTANT, empty(), contestAFinished)).isFalse();
        assertThat(checker.canRegister(CONTESTANT, empty(), contestB)).isFalse();
        assertThat(checker.canRegister(CONTESTANT, empty(), contestBStarted)).isFalse();
        assertThat(checker.canRegister(CONTESTANT, empty(), contestC)).isFalse();

        assertThat(checker.canRegister(SUPERVISOR, empty(), contestA)).isTrue();
        assertThat(checker.canRegister(SUPERVISOR, empty(), contestAStarted)).isTrue();
        assertThat(checker.canRegister(SUPERVISOR, empty(), contestAFinished)).isFalse();
        assertThat(checker.canRegister(SUPERVISOR, empty(), contestB)).isFalse();
        assertThat(checker.canRegister(SUPERVISOR, empty(), contestBStarted)).isFalse();
        assertThat(checker.canRegister(SUPERVISOR, empty(), contestC)).isFalse();

        assertThat(checker.canRegister(MANAGER, empty(), contestA)).isTrue();
        assertThat(checker.canRegister(MANAGER, empty(), contestAStarted)).isTrue();
        assertThat(checker.canRegister(MANAGER, empty(), contestAFinished)).isFalse();
        assertThat(checker.canRegister(MANAGER, empty(), contestB)).isFalse();
        assertThat(checker.canRegister(MANAGER, empty(), contestBStarted)).isFalse();
        assertThat(checker.canRegister(MANAGER, empty(), contestC)).isFalse();
    }

    @Test
    void unregister() {
        assertThat(checker.canUnregister(ADMIN, contestA)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestB)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(ADMIN, contestC)).isFalse();

        assertThat(checker.canUnregister(USER, contestA)).isFalse();
        assertThat(checker.canUnregister(USER, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(USER, contestB)).isFalse();
        assertThat(checker.canUnregister(USER, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(USER, contestC)).isFalse();

        assertThat(checker.canUnregister(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestBStarted)).isFalse();
        moduleStore.upsertRegistrationModule(contestB.getJid());
        assertThat(checker.canUnregister(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canUnregister(CONTESTANT, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canUnregister(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestB)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canUnregister(MANAGER, contestA)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestAStarted)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestB)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestBStarted)).isFalse();
        assertThat(checker.canUnregister(MANAGER, contestC)).isFalse();
    }

    @Test
    void view_approved() {
        assertThat(checker.canViewApproved(ADMIN, contestA)).isTrue();
        assertThat(checker.canViewApproved(ADMIN, contestB)).isTrue();
        assertThat(checker.canViewApproved(ADMIN, contestC)).isTrue();

        assertThat(checker.canViewApproved(USER, contestA)).isTrue();
        assertThat(checker.canViewApproved(USER, contestB)).isFalse();
        assertThat(checker.canViewApproved(USER, contestC)).isFalse();

        assertThat(checker.canViewApproved(CONTESTANT, contestA)).isTrue();
        assertThat(checker.canViewApproved(CONTESTANT, contestB)).isTrue();
        assertThat(checker.canViewApproved(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canViewApproved(SUPERVISOR, contestA)).isTrue();
        assertThat(checker.canViewApproved(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canViewApproved(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canViewApproved(MANAGER, contestA)).isTrue();
        assertThat(checker.canViewApproved(MANAGER, contestB)).isTrue();
        assertThat(checker.canViewApproved(MANAGER, contestC)).isFalse();
    }

    @Test
    void supervise() {
        assertThat(checker.canSupervise(ADMIN, contestA)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestB)).isTrue();
        assertThat(checker.canSupervise(ADMIN, contestC)).isTrue();

        assertThat(checker.canSupervise(USER, contestA)).isFalse();
        assertThat(checker.canSupervise(USER, contestB)).isFalse();
        assertThat(checker.canSupervise(USER, contestC)).isFalse();

        assertThat(checker.canSupervise(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canSupervise(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canSupervise(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canSupervise(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canSupervise(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canSupervise(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canSupervise(MANAGER, contestA)).isFalse();
        assertThat(checker.canSupervise(MANAGER, contestB)).isTrue();
        assertThat(checker.canSupervise(MANAGER, contestC)).isFalse();
    }

    @Test
    void manage() {
        assertThat(checker.canManage(ADMIN, contestA)).isTrue();
        assertThat(checker.canManage(ADMIN, contestB)).isTrue();
        assertThat(checker.canManage(ADMIN, contestC)).isTrue();

        assertThat(checker.canManage(USER, contestA)).isFalse();
        assertThat(checker.canManage(USER, contestB)).isFalse();
        assertThat(checker.canManage(USER, contestC)).isFalse();

        assertThat(checker.canManage(CONTESTANT, contestA)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestB)).isFalse();
        assertThat(checker.canManage(CONTESTANT, contestC)).isFalse();

        assertThat(checker.canManage(SUPERVISOR, contestA)).isFalse();
        assertThat(checker.canManage(SUPERVISOR, contestB)).isFalse();
        addSupervisorToContestBWithPermission(SupervisorManagementPermission.CONTESTANT);
        assertThat(checker.canManage(SUPERVISOR, contestB)).isTrue();
        assertThat(checker.canManage(SUPERVISOR, contestC)).isFalse();

        assertThat(checker.canManage(MANAGER, contestA)).isFalse();
        assertThat(checker.canManage(MANAGER, contestB)).isTrue();
        assertThat(checker.canManage(MANAGER, contestC)).isFalse();
    }
}

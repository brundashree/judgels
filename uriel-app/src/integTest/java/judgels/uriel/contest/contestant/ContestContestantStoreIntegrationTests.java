package judgels.uriel.contest.contestant;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.hibernate.ContestHibernateDao;
import judgels.uriel.hibernate.ContestRoleHibernateDao;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.role.RoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestContestantModel.class})
class ContestContestantStoreIntegrationTests {
    private ContestContestantStore store;
    private ContestStore contestStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        ContestDao contestDao = new ContestHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ContestRoleDao contestRoleDao = new ContestRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        RoleStore roleStore = new RoleStore(adminRoleDao, contestRoleDao);

        contestStore = new ContestStore(roleStore, contestDao);
        store = new ContestContestantStore(contestantDao);
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder().name("contestA").build());

        store.addContestants(contest.getJid(), ImmutableList.of("A", "B"));

        Page<String> contestantJids = store.getContestantJids(contest.getJid(), SelectionOptions.DEFAULT);
        assertThat(contestantJids.getData()).containsOnly("A", "B");
    }

    @Test
    void can_add_without_duplication() {
        Contest contest = contestStore.createContest(new ContestData.Builder().name("contestA").build());

        store.addContestants(contest.getJid(), ImmutableList.of("A", "B"));
        store.addContestants(contest.getJid(), ImmutableList.of("A", "B", "B", "C", "D"));

        Page<String> contestantJids = store.getContestantJids(contest.getJid(), SelectionOptions.DEFAULT);
        assertThat(contestantJids.getData()).containsOnly("A", "B", "C", "D");
    }
}

import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestRegistrationCard from './ContestRegistrationCard';
import sessionReducer, { PutToken, PutUser } from '../../../../../../modules/session/sessionReducer';
import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestWebActions from '../../modules/contestWebActions';
import * as contestContestantActions from '../../modules/contestContestantActions';

jest.mock('../../modules/contestWebActions');
jest.mock('../../modules/contestContestantActions');

describe('ContestRegistrationCard', () => {
  let wrapper;

  const render = async () => {
    contestWebActions.getWebConfig.mockReturnValue(() => Promise.resolve());
    contestContestantActions.getApprovedContestantsCount.mockReturnValue(() => Promise.resolve(10));

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));
    store.dispatch(PutToken('token'));
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <ContestRegistrationCard />
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe.each`
    contestantState                                    | stateText              | actionText
    ${ContestContestantState.RegistrableWrongDivision} | ${[]}                  | ${['Your rating is not allowed for this contest division']}
    ${ContestContestantState.Registrable}              | ${[]}                  | ${['Register']}
    ${ContestContestantState.Registrant}               | ${['tick Registered']} | ${['Unregister']}
    ${ContestContestantState.Contestant}               | ${['tick Registered']} | ${[]}
  `('text', ({ contestantState, stateText, actionText }) => {
    beforeEach(async () => {
      contestContestantActions.getMyContestantState.mockReturnValue(() => Promise.resolve(contestantState));
      await render();
    });

    it(`shows correct texts when contestant state is ${contestantState}`, () => {
      expect(wrapper.find('span.contest-registration-card__state').map(n => n.text())).toEqual(stateText);
      expect(wrapper.find('button.contest-registration-card__action').map(n => n.text())).toEqual(actionText);
    });
  });
});

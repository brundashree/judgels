import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { ContestManagerRemoveDialog } from './ContestManagerRemoveDialog';

describe('ContestManagerRemoveDialog', () => {
  let onDeleteManagers;
  let wrapper;

  beforeEach(() => {
    onDeleteManagers = jest.fn().mockReturnValue(Promise.resolve({ deletedManagerProfilesMap: {} }));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest,
      onDeleteManagers,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestManagerRemoveDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('remove managers dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const usernames = wrapper.find('textarea[name="usernames"]');
    usernames.simulate('change', { target: { value: 'andi\n\nbudi\n caca  \n' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onDeleteManagers).toHaveBeenCalledWith(contestJid, ['andi', 'budi', 'caca']);
  });
});
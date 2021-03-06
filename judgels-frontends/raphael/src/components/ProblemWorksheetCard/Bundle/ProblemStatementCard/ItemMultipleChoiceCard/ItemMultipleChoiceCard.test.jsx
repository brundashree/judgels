import { mount } from 'enzyme';
import * as React from 'react';

import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard';
import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';

describe('ItemMultipleChoiceCard', () => {
  let wrapper;
  const onChoiceChangeFn = jest.fn();
  const itemConfig = {
    statement: 'Statement',
    choices: [
      {
        alias: 'A',
        content: 'A',
      },
      {
        alias: 'B',
        content: 'B',
      },
      {
        alias: 'C',
        content: 'C',
      },
    ],
  };
  const multipleChoiceCardProps = {
    jid: 'jid',
    type: ItemType.MultipleChoice,
    meta: 'meta',
    config: itemConfig,
    disabled: false,
    onChoiceChange: onChoiceChangeFn,
    itemNumber: 1,
  };

  beforeEach(() => {
    const props = multipleChoiceCardProps;
    wrapper = mount(<ItemMultipleChoiceCard {...props} />);
  });

  test('Answer the question by clicking a radio button', () => {
    const radioButton = wrapper
      .find('label')
      .children()
      .find('input')
      .first();
    radioButton.simulate('change', { target: { checked: true } });
    expect(onChoiceChangeFn).toBeCalled();
  });

  test('Answer the question and change the answer', () => {
    const prevAnswer = wrapper
      .find('label')
      .children()
      .find('input')
      .first();
    prevAnswer.simulate('click', { target: { checked: true } });
    const currentAnswer = wrapper
      .find('label')
      .children()
      .find('input')
      .last();
    currentAnswer.simulate('change', { target: { checked: true } });
    expect(onChoiceChangeFn).toHaveBeenCalled();
  });
});

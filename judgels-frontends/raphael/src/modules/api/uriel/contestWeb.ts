import { APP_CONFIG } from 'conf';
import { get } from 'modules/api/http';

import { Contest } from './contest';

export enum ContestTab {
  Announcements = 'ANNOUNCEMENTS',
  Problems = 'PROBLEMS',
  Submissions = 'SUBMISSIONS',
  Clarifications = 'CLARIFICATIONS',
  Scoreboard = 'SCOREBOARD',
}

export interface ContestWithWebConfig {
  contest: Contest;
  config: ContestWebConfig;
}

export interface ContestWebConfig {
  visibleTabs: ContestTab[];
  contestState: ContestState;
  remainingContestStateDuration?: number;
  announcementsCount: number;
  answeredClarificationsCount: number;
}

export enum ContestState {
  NotBegun = 'NOT_BEGUN',
  Begun = 'BEGUN',
  Started = 'STARTED',
  Finished = 'FINISHED',
}

export function createContestWebAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getContestBySlugWithWebConfig: (token: string, contestSlug: string): Promise<ContestWithWebConfig> => {
      return get(`${baseURL}/web/slug/${contestSlug}/with-config`, token);
    },

    getWebConfig: (token: string, contestJid: string): Promise<ContestWebConfig> => {
      return get(`${baseURL}/web/${contestJid}/config`, token);
    },
  };
}

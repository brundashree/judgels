import { AccountRoutes } from './jophiel/accounts/AccountRoutes';
import JophielRoutes from './jophiel/JophielRoutes';
import UrielRoutes from './uriel/UrielRoutes';
import { JophielRole } from '../modules/api/jophiel/my';

function shouldShowRoute(id: string, role: JophielRole) {
  if (id === 'account' && role !== JophielRole.Superadmin && role !== JophielRole.Admin) {
    return false;
  }
  if (id === 'competition' && role === JophielRole.Guest) {
    return false;
  }
  return true;
}

const appRoutes = [
  {
    id: 'account',
    title: 'Account Gate',
    route: {
      path: '/accounts',
      component: AccountRoutes,
    },
  },
  {
    id: 'competition',
    title: 'Competition (BETA)',
    route: {
      path: '/competition',
      component: UrielRoutes,
    },
  },
];

const homeRoute = {
  id: 'home',
  title: 'Home',
  route: {
    component: JophielRoutes,
  },
};

export function getAppRoutes(role: JophielRole) {
  return appRoutes.filter(route => shouldShowRoute(route.id, role));
}

export function getHomeRoute() {
  return homeRoute;
}

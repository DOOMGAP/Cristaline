import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'games',
  },
  {
    path: 'games',
    loadComponent: () =>
      import('./features/games/pages/games-list/games-list.page').then(
        (m) => m.GamesListPage,
      ),
  },
  {
    path: 'games/:id',
    loadComponent: () =>
      import('./features/games/pages/game-detail/game-detail.page').then(
        (m) => m.GameDetailPage,
      ),
  },
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./features/auth/pages/login/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'auth/register',
    loadComponent: () =>
      import('./features/auth/pages/register/register.page').then(
        (m) => m.RegisterPage,
      ),
  },
  {
    path: 'favorites',
    loadComponent: () =>
      import('./features/favorites/pages/favorites-list/favorites-list.page').then(
        (m) => m.FavoritesListPage,
      ),
  },
  {
    path: 'ratings',
    loadComponent: () =>
      import('./features/ratings/pages/ratings-page/ratings.page').then(
        (m) => m.RatingsPage,
      ),
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./features/admin/pages/admin-dashboard/admin-dashboard.page').then(
        (m) => m.AdminDashboardPage,
      ),
  },
  {
    path: 'admin/games',
    loadComponent: () =>
      import('./features/admin/pages/admin-games/admin-games.page').then(
        (m) => m.AdminGamesPage,
      ),
  },
  {
    path: 'admin/import',
    loadComponent: () =>
      import('./features/admin/pages/admin-import/admin-import.page').then(
        (m) => m.AdminImportPage,
      ),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./features/shared/pages/not-found/not-found.page').then(
        (m) => m.NotFoundPage,
      ),
  },
];

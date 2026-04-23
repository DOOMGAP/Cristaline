import { expect, test } from '@playwright/test';

const registeredUsers = new Set<string>();

async function registerAndPersistSession(page: import('@playwright/test').Page, username: string) {
  await page.goto('/games');
  await page.getByTestId('login-button').click();
  await page.getByTestId('auth-toggle').click();
  await page.getByTestId('auth-username').fill(username);
  await page.getByTestId('auth-email').fill(`${username}@example.test`);
  await page.getByTestId('auth-password').fill('password123');
  await page.getByTestId('auth-submit').click();
  await expect(page.getByTestId('current-user')).toContainText(username);
}

async function ensureAuthenticatedUser(page: import('@playwright/test').Page, username: string) {
  await page.goto('/games');

  const currentUser = page.getByTestId('current-user');
  if (await currentUser.isVisible().catch(() => false)) {
    const label = await currentUser.textContent();
    if (label?.includes(username)) {
      return;
    }
    await page.getByTestId('logout-button').click();
  }

  if (!registeredUsers.has(username)) {
    await registerAndPersistSession(page, username);
    registeredUsers.add(username);
    return;
  }

  await page.getByTestId('login-button').click();
  await page.getByTestId('auth-username').fill(username);
  await page.getByTestId('auth-password').fill('password123');
  await page.getByTestId('auth-submit').click();
  await expect(page.getByTestId('current-user')).toContainText(username);
}

async function createGameFromApi(page: import('@playwright/test').Page, suffix: string) {
  const token = await page.evaluate(() => localStorage.getItem('token'));
  expect(token).toBeTruthy();

  const title = `E2E Game ${suffix}`;
  const response = await page.request.post('http://127.0.0.1:8080/admin/games', {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    data: {
      title,
      genre: 'RPG',
      releaseYear: 2024,
      description: `Scenario ${suffix}`,
      coverUrl: '',
    },
  });

  expect(response.ok()).toBeTruthy();
  const game = await response.json();
  return { id: game.id as number, title };
}

test('authentification persists the session across reloads', async ({ page }) => {
  await registerAndPersistSession(page, 'e2e-auth-user');
  registeredUsers.add('e2e-auth-user');

  await page.reload();

  await expect(page.getByTestId('current-user')).toContainText('e2e-auth-user');
});

test('catalog page shows created games and opens the detail page', async ({ page }) => {
  await ensureAuthenticatedUser(page, 'e2e-catalog-user');
  const game = await createGameFromApi(page, 'catalog');

  await page.goto('/games');
  await expect(page.getByText(game.title)).toBeVisible();
  await page.getByRole('link', { name: 'Voir le detail' }).first().click();
  await expect(page.getByTestId('game-title')).toContainText(game.title);
});

test('authenticated user can add a favorite and submit a rating', async ({ page }) => {
  await ensureAuthenticatedUser(page, 'e2e-favorite-user');
  const game = await createGameFromApi(page, 'favorite');

  await page.goto('/games');
  await page.locator('[data-testid="game-card"]').filter({ hasText: game.title }).getByTestId('game-detail-link').click();
  await expect(page.getByTestId('game-title')).toContainText(game.title);
  await page.getByTestId('favorite-toggle').click();
  await expect(page.getByTestId('favorite-toggle')).toHaveClass(/favorited/);

  await page.getByTestId('rating-input').fill('8.5');
  await page.getByTestId('rating-submit').click();
  await expect(page.getByText(/Note moyenne:/)).toContainText('8.5');
});

test('admin import page triggers the import request', async ({ page }) => {
  await ensureAuthenticatedUser(page, 'e2e-admin-user');

  await page.goto('/games');
  await page.getByTestId('nav-admin-import').click();
  await page.getByTestId('admin-import-trigger').click();

  await expect(page.getByTestId('admin-import-message')).toContainText("Demande d'import envoyee.");
});

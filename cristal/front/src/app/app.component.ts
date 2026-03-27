import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <header class="topbar">
        <a class="brand" routerLink="/games">Cristal</a>
        <nav>
          <a routerLink="/games" routerLinkActive="active">Catalogue</a>
          <a routerLink="/favorites" routerLinkActive="active">Favoris</a>
          <a routerLink="/admin/games" routerLinkActive="active">Admin</a>
        </nav>
      </header>

      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .shell {
      padding: 24px;
    }

    .topbar {
      max-width: 1100px;
      margin: 0 auto 24px;
      padding: 18px 22px;
      border: 1px solid var(--border);
      border-radius: 22px;
      background: var(--surface);
      box-shadow: var(--shadow);
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
    }

    .brand {
      font-size: 1.4rem;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    nav {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }

    nav a {
      padding: 10px 14px;
      border-radius: 999px;
      color: var(--muted);
    }

    nav a.active {
      background: var(--accent);
      color: white;
    }

    .content {
      max-width: 1100px;
      margin: 0 auto;
    }
  `],
})
export class AppComponent {}

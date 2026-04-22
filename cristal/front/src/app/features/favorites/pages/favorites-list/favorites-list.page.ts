import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FavoritesApi } from '../../data/favorites.api';
import { Game } from '../../../games/data/game.model';

@Component({
  selector: 'app-favorites-list-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="hero">
      <div>
        <p class="eyebrow">Mes Favoris</p>
        <h1>Jeux Favoris</h1>
        <p class="lead">Tous les jeux que vous avez mis en favoris.</p>
      </div>
    </section>

    <p *ngIf="error" class="error">{{ error }}</p>

    <p *ngIf="games.length === 0 && !error && !isLoading" class="empty">
      Aucun favori pour le moment. <a routerLink="/games">Découvrez des jeux</a>
    </p>

    <section *ngIf="games.length > 0" class="grid">
      <article *ngFor="let game of games" class="card">
        <div class="cover" [style.background-image]="'url(' + (game.coverUrl || '') + ')'"></div>
        <div class="meta">
          <span>{{ game.genre }}</span>
          <span>{{ game.releaseYear }}</span>
        </div>
        <h2>{{ game.title }}</h2>
        <p>{{ game.description }}</p>
        <a [routerLink]="['/games', game.id]">Voir le detail</a>
      </article>
    </section>
  `,
  styles: [`
    .hero, .card {
      border: 1px solid var(--border);
      background: var(--surface);
      box-shadow: var(--shadow);
      border-radius: 24px;
    }

    .hero {
      display: grid;
      grid-template-columns: 1.4fr 1fr;
      gap: 24px;
      padding: 28px;
      margin-bottom: 24px;
    }

    .eyebrow {
      margin: 0 0 8px;
      color: var(--accent);
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.08em;
    }

    h1 {
      margin: 0 0 10px;
      font-size: clamp(2rem, 5vw, 3.4rem);
      line-height: 0.95;
    }

    .lead {
      margin: 0;
      color: var(--muted);
    }

    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 18px;
    }

    .card {
      padding: 16px;
      display: grid;
      gap: 12px;
    }

    .cover {
      min-height: 220px;
      border-radius: 18px;
      background: linear-gradient(135deg, rgba(212, 90, 51, 0.18), rgba(33, 67, 92, 0.16));
      background-size: cover;
      background-position: center;
    }

    .meta {
      display: flex;
      justify-content: space-between;
      color: var(--muted);
      font-size: 0.92rem;
    }

    h2 {
      margin: 0;
    }

    p {
      margin: 0;
    }

    a {
      background: var(--accent);
      color: white;
      border: none;
      cursor: pointer;
      text-align: center;
      border-radius: 14px;
      padding: 12px 14px;
      text-decoration: none;
    }

    .error {
      color: #9b1c1c;
      font-weight: 600;
    }

    .empty {
      color: var(--muted);
      padding: 24px;
      text-align: center;
    }

    .empty a {
      display: inline-block;
      margin-top: 12px;
    }

    @media (max-width: 720px) {
      .hero {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class FavoritesListPage {
  private readonly favoritesApi = inject(FavoritesApi);

  games: Game[] = [];
  error = '';
  isLoading = true;

  constructor() {
    this.loadFavorites();
  }

  loadFavorites(): void {
    this.error = '';
    this.isLoading = true;
    this.favoritesApi.getMyFavorites().subscribe({
      next: (games) => {
        this.games = games;
        this.isLoading = false;
      },
      error: () => {
        this.error = 'Impossible de charger vos favoris.';
        this.isLoading = false;
      },
    });
  }
}

import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GamesApi } from '../../data/games.api';
import { Game } from '../../data/game.model';

@Component({
  selector: 'app-games-list-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="hero">
      <div>
        <h1>Catalogue des jeux</h1>
        <p class="lead">Recherche, filtrage et navigation vers les details du catalogue.</p>
      </div>
      <form [formGroup]="filters" (ngSubmit)="loadGames()" class="filters">
        <input type="text" placeholder="Titre" formControlName="title">
        
        <select formControlName="genre" title="Choisir un genre">
          <option value="">Tous les genres</option>
          <option *ngFor="let g of availableGenres" [value]="g">{{ g }}</option>
        </select>
        
        <input type="number" placeholder="Annee" formControlName="year" min="0" step="1" (keypress)="$event.charCode >= 48 && $event.charCode <= 57">
        <button type="submit">Filtrer</button>
      </form>
    </section>

    <p *ngIf="error" class="error">{{ error }}</p>

    <section class="grid">
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

    .filters {
      display: grid;
      gap: 12px;
      align-content: start;
    }

    /* AJOUT DE 'select' ICI pour garantir un affichage identique aux inputs */
    input, select, button, a {
      border-radius: 14px;
      border: 1px solid var(--border);
      padding: 12px 14px;
      background-color: white; /* Fond blanc propre pour le select */
      font-family: inherit;
      font-size: 1rem;
      color: inherit;
    }

    button, a {
      background: var(--accent);
      color: white;
      border: none;
      cursor: pointer;
      text-align: center;
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

    .error {
      color: #9b1c1c;
      font-weight: 600;
    }

    @media (max-width: 720px) {
      .hero {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class GamesListPage {
  private readonly api = inject(GamesApi);
  private readonly fb = inject(FormBuilder);

  readonly availableGenres = [
    'ARPG', 'Action', 'Action Game', 'Battle Royale', 'Card Game', 
    'Dungeon Crawler', 'Fighting', 'MMO', 'MMOARPG', 'MMORPG', 'MOBA', 
    'RPG', 'Racing', 'Shooter', 'Social', 'Sports', 'Strategy'
  ];

  readonly filters = this.fb.nonNullable.group({
    title: [''],
    genre: [''],
    year: [''],
  });

  games: Game[] = [];
  error = '';

  constructor() {
    this.loadGames();
  }

  loadGames(): void {
    this.error = '';
    this.api.list(this.filters.getRawValue()).subscribe({
      next: (games) => {
        this.games = games;
      },
      error: () => {
        this.error = 'Impossible de charger le catalogue.';
      },
    });
  }
}
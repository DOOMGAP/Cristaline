import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { GamesApi } from '../../data/games.api';
import { Game } from '../../data/game.model';

@Component({
  selector: 'app-game-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <a routerLink="/games" class="back">Retour au catalogue</a>

    <section *ngIf="game" class="detail">
      <div class="cover" [style.background-image]="'url(' + (game.coverUrl || '') + ')'"></div>
      <div class="content">
        <p class="eyebrow">{{ game.genre }} • {{ game.releaseYear }}</p>
        <h1>{{ game.title }}</h1>
        <p>{{ game.description }}</p>
      </div>
    </section>

    <p *ngIf="error" class="error">{{ error }}</p>
  `,
  styles: [`
    .back, .detail {
      border: 1px solid var(--border);
      background: var(--surface);
      box-shadow: var(--shadow);
      border-radius: 24px;
    }

    .back {
      display: inline-block;
      padding: 10px 14px;
      margin-bottom: 18px;
    }

    .detail {
      display: grid;
      grid-template-columns: 320px 1fr;
      gap: 24px;
      padding: 24px;
    }

    .cover {
      min-height: 420px;
      border-radius: 20px;
      background: linear-gradient(135deg, rgba(212, 90, 51, 0.18), rgba(33, 67, 92, 0.16));
      background-size: cover;
      background-position: center;
    }

    .eyebrow {
      margin: 0;
      color: var(--accent);
      font-weight: 700;
    }

    h1 {
      margin: 8px 0 14px;
      font-size: clamp(2rem, 5vw, 3.5rem);
      line-height: 0.95;
    }

    .error {
      color: #9b1c1c;
      font-weight: 600;
    }

    @media (max-width: 720px) {
      .detail {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class GameDetailPage {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(GamesApi);

  game: Game | null = null;
  error = '';

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Identifiant de jeu invalide.';
      return;
    }

    this.api.getById(id).subscribe({
      next: (game) => {
        this.game = game;
      },
      error: () => {
        this.error = 'Jeu introuvable.';
      },
    });
  }
}

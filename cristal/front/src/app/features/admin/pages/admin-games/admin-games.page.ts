import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { GamesApi } from '../../../games/data/games.api';
import { Game, GamePayload } from '../../../games/data/game.model';

@Component({
  selector: 'app-admin-games-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="layout">
      <article class="panel">
        <p class="eyebrow">Administration</p>
        <h1>{{ editingId ? 'Modifier un jeu' : 'Ajouter un jeu' }}</h1>
        <form [formGroup]="form" (ngSubmit)="save()">
          <input type="text" placeholder="Titre" formControlName="title">
          <input type="text" placeholder="Genre" formControlName="genre">
          <input type="number" placeholder="Annee" formControlName="releaseYear">
          <input type="text" placeholder="URL jaquette" formControlName="coverUrl">
          <textarea rows="6" placeholder="Description" formControlName="description"></textarea>
          <div class="actions">
            <button type="submit">{{ editingId ? 'Mettre a jour' : 'Creer' }}</button>
            <button type="button" class="ghost" (click)="resetForm()">Vider</button>
          </div>
        </form>
        <p *ngIf="message" class="message">{{ message }}</p>
      </article>

      <article class="panel">
        <h2>Jeux existants</h2>
        <div class="list">
          <section *ngFor="let game of games" class="item">
            <div>
              <strong>{{ game.title }}</strong>
              <p>{{ game.genre }} • {{ game.releaseYear }}</p>
            </div>
            <div class="actions">
              <button type="button" class="ghost" (click)="edit(game)">Modifier</button>
              <button type="button" class="danger" (click)="remove(game.id)">Supprimer</button>
            </div>
          </section>
        </div>
      </article>
    </section>
  `,
  styles: [`
    .layout {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }

    .panel {
      border: 1px solid var(--border);
      background: var(--surface);
      box-shadow: var(--shadow);
      border-radius: 24px;
      padding: 22px;
    }

    .eyebrow {
      margin: 0 0 8px;
      color: var(--accent);
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.08em;
    }

    form, .list {
      display: grid;
      gap: 12px;
    }

    input, textarea, button {
      width: 100%;
      border-radius: 14px;
      border: 1px solid var(--border);
      padding: 12px 14px;
    }

    button {
      cursor: pointer;
      border: none;
      background: var(--accent);
      color: white;
    }

    .ghost {
      background: var(--surface-strong);
      color: var(--text);
      border: 1px solid var(--border);
    }

    .danger {
      background: var(--accent-dark);
    }

    .actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }

    .item {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      border: 1px solid var(--border);
      border-radius: 18px;
      padding: 14px;
      background: var(--surface-strong);
    }

    .item p, h1, h2 {
      margin: 0;
    }

    .message {
      margin: 0;
      color: var(--accent-dark);
      font-weight: 700;
    }

    @media (max-width: 860px) {
      .layout {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class AdminGamesPage {
  private readonly api = inject(GamesApi);
  private readonly fb = inject(FormBuilder);

  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    genre: ['', Validators.required],
    releaseYear: [2024, Validators.required],
    description: ['', Validators.required],
    coverUrl: [''],
  });

  games: Game[] = [];
  editingId: number | null = null;
  message = '';

  constructor() {
    this.loadGames();
  }

  loadGames(): void {
    this.api.list({}).subscribe((games) => {
      this.games = games;
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.message = 'Merci de remplir tous les champs obligatoires.';
      return;
    }

    const payload: GamePayload = this.form.getRawValue();
    const request = this.editingId
      ? this.api.update(this.editingId, payload)
      : this.api.create(payload);

    request.subscribe({
      next: () => {
        this.message = this.editingId ? 'Jeu mis a jour.' : 'Jeu cree.';
        this.resetForm();
        this.loadGames();
      },
      error: () => {
        this.message = 'Une erreur est survenue.';
      },
    });
  }

  edit(game: Game): void {
    this.editingId = game.id;
    this.form.patchValue({
      title: game.title,
      genre: game.genre,
      releaseYear: game.releaseYear,
      description: game.description,
      coverUrl: game.coverUrl ?? '',
    });
  }

  remove(id: number): void {
    this.api.delete(id).subscribe({
      next: () => {
        this.message = 'Jeu supprime.';
        if (this.editingId === id) {
          this.resetForm();
        }
        this.loadGames();
      },
      error: () => {
        this.message = 'Suppression impossible.';
      },
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form.reset({
      title: '',
      genre: '',
      releaseYear: 2024,
      description: '',
      coverUrl: '',
    });
  }
}

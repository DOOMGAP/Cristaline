import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GamesApi } from '../../data/games.api';
import { RatingsApi } from '../../../ratings/data/ratings.api';
import { Game } from '../../data/game.model';

@Component({
  selector: 'app-game-detail-page',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  template: `
    <a routerLink="/games" class="back">Retour au catalogue</a>

    <section *ngIf="game" class="detail">
      <div class="cover" [style.background-image]="'url(' + (game.coverUrl || '') + ')'"></div>
      <div class="content">
        <p class="eyebrow">{{ game.genre }} • {{ game.releaseYear }}</p>
        <h1>{{ game.title }}</h1>
        <p>{{ game.description }}</p>

        <!-- Rating Section -->
        <div class="rating-section">
          <h3>{{ hasExistingRating ? 'Modifier votre note' : 'Noter ce jeu' }}</h3>
          <form [formGroup]="ratingForm" (ngSubmit)="submitRating()">
            <div class="rating-input">
              <label for="rating">Note (1-10):</label>
              <input
                type="number"
                id="rating"
                formControlName="rating"
                min="1"
                max="10"
                step="0.5"
                class="rating-field"
              />
            </div>

            <button
              type="submit"
              class="submit-btn"
              [disabled]="ratingForm.invalid || isSubmitting || !isRatingChanged"
            >
              {{ isSubmitting ? 'Envoi...' : (hasExistingRating ? 'Modifier la note' : 'Soumettre la note') }}
            </button>

            <p *ngIf="ratingSuccess" class="success">Note {{ hasExistingRating ? 'modifiée' : 'soumise' }} avec succès!</p>
            <p *ngIf="ratingError" class="error">{{ ratingError }}</p>
          </form>
        </div>
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

    .content {
      display: flex;
      flex-direction: column;
      gap: 16px;
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

    h3 {
      margin-top: 24px;
      margin-bottom: 12px;
      font-size: 1.2rem;
    }

    .rating-section {
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid var(--border);
    }

    form {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .rating-input {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    label {
      font-weight: 500;
      font-size: 0.95rem;
    }

    .rating-field {
      padding: 10px;
      border: 1px solid var(--border);
      border-radius: 8px;
      font-family: inherit;
      font-size: 1rem;
      background: var(--surface);
      color: inherit;
    }

    .rating-field:focus {
      outline: none;
      border-color: var(--accent);
      box-shadow: 0 0 0 2px rgba(212, 90, 51, 0.1);
    }

    .submit-btn {
      padding: 10px 16px;
      margin-top: 8px;
      background: var(--accent);
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: opacity 0.2s;
    }

    .submit-btn:hover:not(:disabled) {
      opacity: 0.9;
    }

    .submit-btn:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .success {
      color: #15803d;
      font-weight: 600;
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
  private readonly gamesApi = inject(GamesApi);
  private readonly ratingsApi = inject(RatingsApi);
  private readonly fb = inject(FormBuilder);

  game: Game | null = null;
  error = '';
  ratingForm!: FormGroup;
  isSubmitting = false;
  ratingSuccess = false;
  ratingError = '';
  hasExistingRating = false;
  isRatingChanged = false;
  private initialRating: any = null;

  constructor() {
    this.ratingForm = this.fb.group({
      rating: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
    });

    // Track changes to the form
    this.ratingForm.valueChanges.subscribe(() => {
      this.checkIfRatingChanged();
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Identifiant de jeu invalide.';
      return;
    }

    // Load game and existing rating
    this.gamesApi.getById(id).subscribe({
      next: (game) => {
        this.game = game;
        this.loadExistingRating(parseInt(id));
      },
      error: () => {
        this.error = 'Jeu introuvable.';
      },
    });
  }

  private loadExistingRating(gameId: number) {
    this.ratingsApi.getRating(gameId).subscribe({
      next: (rating) => {
        this.hasExistingRating = true;
        this.initialRating = { rating: rating.rating };
        this.ratingForm.patchValue({
          rating: rating.rating,
        });
        this.isRatingChanged = false;
      },
      error: () => {
        // No existing rating, form stays empty
        this.hasExistingRating = false;
        this.initialRating = null;
      },
    });
  }

  private checkIfRatingChanged() {
    if (!this.initialRating) {
      // New rating
      this.isRatingChanged = this.ratingForm.get('rating')?.value !== null;
    } else {
      // Check if modified
      this.isRatingChanged =
        this.ratingForm.get('rating')?.value !== this.initialRating.rating;
    }
  }

  submitRating() {
    if (this.ratingForm.invalid || !this.game) {
      return;
    }

    this.isSubmitting = true;
    this.ratingError = '';
    this.ratingSuccess = false;

    this.ratingsApi.submitRating(this.game.id, this.ratingForm.value).subscribe({
      next: () => {
        this.ratingSuccess = true;
        this.initialRating = { ...this.ratingForm.value };
        this.isRatingChanged = false;
        this.isSubmitting = false;

        // Hide success message after 3 seconds
        setTimeout(() => {
          this.ratingSuccess = false;
        }, 3000);
      },
      error: (err) => {
        this.ratingError = err.error?.message || err.error || 'Erreur lors de la soumission de la note.';
        this.isSubmitting = false;
      },
    });
  }
}

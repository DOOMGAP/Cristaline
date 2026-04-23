import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { distinctUntilChanged, skip } from 'rxjs';
import { GamesApi } from '../../data/games.api';
import { RatingsApi } from '../../../ratings/data/ratings.api';
import { FavoritesApi } from '../../../favorites/data/favorites.api';
import { AuthService } from '../../../auth/auth.service';
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
        <div class="header">
          <div>
            <p class="eyebrow">{{ game.genre }} • {{ game.releaseYear }}</p>
            <h1>{{ game.title }}</h1>
          </div>
          <button 
            class="favorite-btn" 
            [class.favorited]="isFavorited"
            (click)="toggleFavorite()"
            [disabled]="isTogglingFavorite"
            [title]="isFavorited ? 'Retirer des favoris' : 'Ajouter aux favoris'"
          >
            <span class="star">★</span>
          </button>
        </div>
        <p>{{ game.description }}</p>
        <p class="average-rating" *ngIf="ratingsCount > 0">
          Note moyenne: {{ averageRatingText }} / 10 ({{ ratingsCount }} notes)
        </p>
        <p class="average-rating" *ngIf="ratingsCount === 0">
          Note moyenne: pas encore de note.
        </p>

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

    .header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
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

    .favorite-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border: 2px solid var(--border);
      background: var(--surface);
      border-radius: 50%;
      cursor: pointer;
      transition: all 0.2s ease;
      flex-shrink: 0;
    }

    .favorite-btn:hover:not(:disabled) {
      border-color: var(--accent);
      background: rgba(212, 90, 51, 0.08);
    }

    .favorite-btn .star {
      font-size: 24px;
      color: var(--border);
      transition: all 0.2s ease;
      line-height: 1;
      display: block;
    }

    .favorite-btn.favorited {
      border-color: var(--accent);
      background: rgba(212, 90, 51, 0.12);
    }

    .favorite-btn.favorited .star {
      color: var(--accent);
    }

    .favorite-btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .rating-section {
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid var(--border);
    }

    .average-rating {
      margin: 0;
      font-weight: 600;
      color: var(--muted);
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

    .error {
      color: #9b1c1c;
      font-weight: 600;
    }

    @media (max-width: 720px) {
      .detail {
        grid-template-columns: 1fr;
      }

      .header {
        align-items: center;
      }

      h1 {
        font-size: clamp(1.5rem, 4vw, 2.5rem);
      }
    }
  `],
})
export class GameDetailPage {
  private readonly route = inject(ActivatedRoute);
  private readonly gamesApi = inject(GamesApi);
  private readonly ratingsApi = inject(RatingsApi);
  private readonly favoritesApi = inject(FavoritesApi);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);

  game: Game | null = null;
  error = '';
  ratingForm!: FormGroup;
  isSubmitting = false;
  ratingError = '';
  hasExistingRating = false;
  isRatingChanged = false;
  isFavorited = false;
  isTogglingFavorite = false;
  averageRatingText = '0.0';
  ratingsCount = 0;
  private initialRating: any = null;
  private ratingRequestSeq = 0;
  private favoriteRequestSeq = 0;

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
        const gameId = parseInt(id);
        this.resetUserSpecificState();
        this.loadRatingSummary(gameId);
        this.loadExistingRating(gameId);
        this.loadFavoriteStatus(gameId);
      },
      error: () => {
        this.error = 'Jeu introuvable.';
      },
    });

    // Reload per-user state when account changes without leaving the page.
    this.authService.currentUser$
      .pipe(skip(1), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (!this.game) {
          return;
        }
        this.resetUserSpecificState();
        this.loadExistingRating(this.game.id);
        this.loadFavoriteStatus(this.game.id);
      });
  }

  private resetUserSpecificState() {
    this.isFavorited = false;
    this.hasExistingRating = false;
    this.initialRating = null;
    this.isRatingChanged = false;
    this.ratingError = '';
    this.ratingForm.patchValue({ rating: null }, { emitEvent: false });
  }

  private loadRatingSummary(gameId: number) {
    this.ratingsApi.getRatingSummary(gameId).subscribe({
      next: (summary) => {
        this.ratingsCount = summary.ratingsCount ?? 0;
        const average = summary.averageRating;
        this.averageRatingText = average == null ? '0.0' : average.toFixed(1);
      },
      error: () => {
        this.ratingsCount = 0;
        this.averageRatingText = '0.0';
      },
    });
  }

  private loadExistingRating(gameId: number) {
    const requestSeq = ++this.ratingRequestSeq;
    this.ratingsApi.getRating(gameId).subscribe({
      next: (rating) => {
        if (requestSeq !== this.ratingRequestSeq) {
          return;
        }
        this.hasExistingRating = true;
        this.initialRating = { rating: rating.rating };
        this.ratingForm.patchValue({
          rating: rating.rating,
        });
        this.isRatingChanged = false;
      },
      error: () => {
        if (requestSeq !== this.ratingRequestSeq) {
          return;
        }
        // No existing rating, form stays empty
        this.hasExistingRating = false;
        this.initialRating = null;
        this.ratingForm.patchValue({ rating: null });
        this.isRatingChanged = false;
      },
    });
  }

  private loadFavoriteStatus(gameId: number) {
    const requestSeq = ++this.favoriteRequestSeq;
    this.favoritesApi.isFavorited(gameId).subscribe({
      next: (response) => {
        if (requestSeq !== this.favoriteRequestSeq) {
          return;
        }
        this.isFavorited = response.favorited;
      },
      error: () => {
        if (requestSeq !== this.favoriteRequestSeq) {
          return;
        }
        // Error loading favorite status
        this.isFavorited = false;
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

  toggleFavorite() {
    if (!this.game || this.isTogglingFavorite) {
      return;
    }

    this.isTogglingFavorite = true;

    if (this.isFavorited) {
      this.favoritesApi.removeFavorite(this.game.id).subscribe({
        next: () => {
          this.isFavorited = false;
          this.isTogglingFavorite = false;
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du favori:', err);
          this.isTogglingFavorite = false;
        },
      });
    } else {
      this.favoritesApi.addFavorite(this.game.id).subscribe({
        next: () => {
          this.isFavorited = true;
          this.isTogglingFavorite = false;
        },
        error: (err) => {
          console.error('Erreur lors de l\'ajout du favori:', err);
          this.isTogglingFavorite = false;
        },
      });
    }
  }

  submitRating() {
    if (this.ratingForm.invalid || !this.game) {
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.ratingError = 'Vous devez etre connecte pour noter un jeu.';
      return;
    }

    this.isSubmitting = true;
    this.ratingError = '';

    this.ratingsApi.submitRating(this.game.id, this.ratingForm.value).subscribe({
      next: () => {
        this.initialRating = { ...this.ratingForm.value };
        this.isRatingChanged = false;
        this.loadRatingSummary(this.game!.id);
        this.isSubmitting = false;
      },
      error: (err) => {
        if (err.status === 401) {
          this.authService.logout();
          this.ratingError = 'Session expiree. Reconnectez-vous pour noter un jeu.';
        } else if (err.status === 403) {
          this.ratingError = 'Action refusee par le serveur. Reessayez apres reconnexion.';
        } else {
          this.ratingError = err.error?.message || err.error || 'Erreur lors de la soumission de la note.';
        }
        this.isSubmitting = false;
      },
    });
  }
}

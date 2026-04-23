import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Game } from '../../games/data/game.model';

export interface FavoriteStatusResponse {
  favorited: boolean;
}

@Injectable({ providedIn: 'root' })
export class FavoritesApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = this.resolveBaseUrl();

  isFavorited(gameId: number): Observable<FavoriteStatusResponse> {
    return this.http.get<FavoriteStatusResponse>(
      `${this.baseUrl}/games/${gameId}/favorites/user`,
      { headers: this.buildAuthHeaders() }
    );
  }

  addFavorite(gameId: number): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/games/${gameId}/favorites`,
      null,
      { headers: this.buildAuthHeaders() }
    );
  }

  removeFavorite(gameId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/games/${gameId}/favorites`,
      { headers: this.buildAuthHeaders() }
    );
  }

  getMyFavorites(): Observable<Game[]> {
    return this.http.get<Game[]>(
      `${this.baseUrl}/me/favorites`,
      { headers: this.buildAuthHeaders() }
    );
  }

  private buildAuthHeaders(): Record<string, string> {
    const token = localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  private resolveBaseUrl(): string {
    if (typeof window === 'undefined') {
      return 'http://localhost:8080';
    }

    return `${window.location.protocol}//${window.location.hostname}:8080`;
  }
}

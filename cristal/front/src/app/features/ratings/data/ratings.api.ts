import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Rating, RatingRequest, RatingSummary } from './rating.model';

@Injectable({ providedIn: 'root' })
export class RatingsApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = this.resolveBaseUrl();

  getRating(gameId: number): Observable<Rating> {
    return this.http.get<Rating>(
      `${this.baseUrl}/games/${gameId}/ratings/user`,
      { headers: this.buildAuthHeaders() }
    );
  }

  submitRating(gameId: number, payload: RatingRequest): Observable<Rating> {
    return this.http.post<Rating>(
      `${this.baseUrl}/games/${gameId}/ratings`,
      payload,
      { headers: this.buildAuthHeaders() }
    );
  }

  getRatingSummary(gameId: number): Observable<RatingSummary> {
    return this.http.get<RatingSummary>(
      `${this.baseUrl}/games/${gameId}/ratings/summary`
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

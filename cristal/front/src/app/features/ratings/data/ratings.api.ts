import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Rating, RatingRequest } from './rating.model';

@Injectable({ providedIn: 'root' })
export class RatingsApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = this.resolveBaseUrl();

  getRating(gameId: number): Observable<Rating> {
    return this.http.get<Rating>(
      `${this.baseUrl}/games/${gameId}/ratings/user`
    );
  }

  submitRating(gameId: number, payload: RatingRequest): Observable<Rating> {
    return this.http.post<Rating>(
      `${this.baseUrl}/games/${gameId}/ratings`,
      payload
    );
  }

  private resolveBaseUrl(): string {
    if (typeof window === 'undefined') {
      return 'http://localhost:8080';
    }

    return `${window.location.protocol}//${window.location.hostname}:8080`;
  }
}

import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Game, GamePayload } from './game.model';

@Injectable({ providedIn: 'root' })
export class GamesApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  list(filters: { title?: string; genre?: string; year?: string }): Observable<Game[]> {
    let params = new HttpParams();

    if (filters.title) {
      params = params.set('title', filters.title);
    }
    if (filters.genre) {
      params = params.set('genre', filters.genre);
    }
    if (filters.year) {
      params = params.set('year', filters.year);
    }

    return this.http.get<Game[]>(`${this.baseUrl}/games`, { params });
  }

  getById(id: string): Observable<Game> {
    return this.http.get<Game>(`${this.baseUrl}/games/${id}`);
  }

  create(payload: GamePayload): Observable<Game> {
    return this.http.post<Game>(`${this.baseUrl}/admin/games`, payload);
  }

  update(id: number, payload: GamePayload): Observable<Game> {
    return this.http.put<Game>(`${this.baseUrl}/admin/games/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/admin/games/${id}`);
  }
}

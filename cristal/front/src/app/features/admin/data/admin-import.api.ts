import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminImportApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = this.resolveBaseUrl();

  triggerFreeToGameImport(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/admin/import/freetogame`, null, {
      headers: this.buildAuthHeaders(),
    });
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

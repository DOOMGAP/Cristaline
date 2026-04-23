import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { FavoritesApi } from './favorites.api';

describe('FavoritesApi', () => {
  let service: FavoritesApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.setItem('token', 'token-123');
    TestBed.configureTestingModule({
      providers: [
        FavoritesApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(FavoritesApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    localStorage.clear();
    httpMock.verify();
  });

  it('should send auth header when checking favorites', () => {
    service.isFavorited(12).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/games/12/favorites/user');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-123');
    req.flush({ favorited: true });
  });

  it('should load current user favorites', () => {
    service.getMyFavorites().subscribe();

    const req = httpMock.expectOne('http://localhost:8080/me/favorites');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});

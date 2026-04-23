import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RatingsApi } from './ratings.api';

describe('RatingsApi', () => {
  let service: RatingsApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.setItem('token', 'rating-token');
    TestBed.configureTestingModule({
      providers: [
        RatingsApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(RatingsApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    localStorage.clear();
    httpMock.verify();
  });

  it('should fetch current user rating with auth header', () => {
    service.getRating(8).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/games/8/ratings/user');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer rating-token');
    req.flush({ id: 1, rating: 8, ratedAt: 'now' });
  });

  it('should submit a rating payload', () => {
    service.submitRating(8, { rating: 7.5 }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/games/8/ratings');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ rating: 7.5 });
    req.flush({ id: 1, rating: 7.5, ratedAt: 'now' });
  });
});

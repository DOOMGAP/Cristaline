import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { GamesApi } from './games.api';

describe('GamesApi', () => {
  let service: GamesApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        GamesApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(GamesApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should send filters as query params', () => {
    service.list({ title: 'Hades', genre: 'Rogue-like', year: '2020' }).subscribe();

    const req = httpMock.expectOne((request) =>
      request.method === 'GET' &&
      request.url === 'http://localhost:8080/games' &&
      request.params.get('title') === 'Hades' &&
      request.params.get('genre') === 'Rogue-like' &&
      request.params.get('year') === '2020'
    );

    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should create a game through admin endpoint', () => {
    service.create({
      title: 'Hades',
      genre: 'Rogue-like',
      releaseYear: 2020,
      description: 'desc',
      coverUrl: 'cover',
    }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/admin/games');
    expect(req.request.method).toBe('POST');
    req.flush({});
  });
});

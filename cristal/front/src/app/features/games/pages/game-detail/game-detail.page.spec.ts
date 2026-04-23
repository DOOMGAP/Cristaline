import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { AuthService } from '../../../auth/auth.service';
import { FavoritesApi } from '../../../favorites/data/favorites.api';
import { RatingsApi } from '../../../ratings/data/ratings.api';
import { GamesApi } from '../../data/games.api';
import { GameDetailPage } from './game-detail.page';

describe('GameDetailPage', () => {
  let fixture: ComponentFixture<GameDetailPage>;
  let component: GameDetailPage;
  let gamesApiSpy: jasmine.SpyObj<GamesApi>;
  let ratingsApiSpy: jasmine.SpyObj<RatingsApi>;
  let favoritesApiSpy: jasmine.SpyObj<FavoritesApi>;
  let authServiceMock: { currentUser$: BehaviorSubject<string | null>; logout: jasmine.Spy };

  beforeEach(async () => {
    gamesApiSpy = jasmine.createSpyObj<GamesApi>('GamesApi', ['getById']);
    ratingsApiSpy = jasmine.createSpyObj<RatingsApi>('RatingsApi', ['getRatingSummary', 'getRating', 'submitRating']);
    favoritesApiSpy = jasmine.createSpyObj<FavoritesApi>('FavoritesApi', ['isFavorited', 'addFavorite', 'removeFavorite']);
    authServiceMock = {
      currentUser$: new BehaviorSubject<string | null>(null),
      logout: jasmine.createSpy('logout'),
    };

    gamesApiSpy.getById.and.returnValue(of({
      id: 1,
      title: 'Celeste',
      genre: 'Platformer',
      releaseYear: 2018,
      description: 'Climb the mountain',
      coverUrl: '',
    }));
    ratingsApiSpy.getRatingSummary.and.returnValue(of({ averageRating: 9.2, ratingsCount: 4 }));
    ratingsApiSpy.getRating.and.returnValue(throwError(() => ({ status: 404 })));
    favoritesApiSpy.isFavorited.and.returnValue(of({ favorited: false }));
    favoritesApiSpy.addFavorite.and.returnValue(of(void 0));
    favoritesApiSpy.removeFavorite.and.returnValue(of(void 0));
    ratingsApiSpy.submitRating.and.returnValue(of({ id: 1, rating: 9, ratedAt: 'now' }));

    await TestBed.configureTestingModule({
      imports: [GameDetailPage],
      providers: [
        { provide: GamesApi, useValue: gamesApiSpy },
        { provide: RatingsApi, useValue: ratingsApiSpy },
        { provide: FavoritesApi, useValue: favoritesApiSpy },
        { provide: AuthService, useValue: authServiceMock },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GameDetailPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load the requested game and summary', () => {
    expect(component.game?.title).toBe('Celeste');
    expect(component.averageRatingText).toBe('9.2');
    expect(component.ratingsCount).toBe(4);
  });

  it('should show an auth error when submitting a rating without token', () => {
    localStorage.removeItem('token');
    component.ratingForm.patchValue({ rating: 8 });

    component.submitRating();

    expect(component.ratingError).toBe('Vous devez etre connecte pour noter un jeu.');
  });

  it('should toggle favorite status after a successful add', () => {
    component.toggleFavorite();

    expect(favoritesApiSpy.addFavorite).toHaveBeenCalledWith(1);
    expect(component.isFavorited).toBeTrue();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { GamesApi } from '../../data/games.api';
import { GamesListPage } from './games-list.page';

describe('GamesListPage', () => {
  let fixture: ComponentFixture<GamesListPage>;
  let component: GamesListPage;
  let gamesApiSpy: jasmine.SpyObj<GamesApi>;

  beforeEach(async () => {
    gamesApiSpy = jasmine.createSpyObj<GamesApi>('GamesApi', ['list']);
    gamesApiSpy.list.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [GamesListPage],
      providers: [{ provide: GamesApi, useValue: gamesApiSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(GamesListPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load games on init', () => {
    expect(gamesApiSpy.list).toHaveBeenCalled();
  });

  it('should render an error when api fails', () => {
    gamesApiSpy.list.and.returnValue(throwError(() => new Error('boom')));

    component.loadGames();
    fixture.detectChanges();

    expect(component.error).toBe('Impossible de charger le catalogue.');
  });
});

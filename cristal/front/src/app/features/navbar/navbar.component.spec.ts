import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../auth/auth.service';
import { BehaviorSubject } from 'rxjs';
import { MatDialogModule } from '@angular/material/dialog';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let authServiceMock: { currentUser$: BehaviorSubject<string | null>; logout: jasmine.Spy };
  let currentUserSubject: BehaviorSubject<string | null>;

  beforeEach(async () => {
    currentUserSubject = new BehaviorSubject<string | null>(null);
    authServiceMock = {
      currentUser$: currentUserSubject,
      logout: jasmine.createSpy('logout')
    };

    await TestBed.configureTestingModule({
      imports: [NavbarComponent, MatDialogModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display "Connexion" button when user is not logged in', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-testid="login-button"]')?.textContent).toContain('Connexion');
  });

  it('should display "Bonjour" when user is logged in', () => {
    currentUserSubject.next('Arona');
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Bonjour, Arona');
  });
});

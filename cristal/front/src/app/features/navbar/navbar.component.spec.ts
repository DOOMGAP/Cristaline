import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../auth/auth.service';
import { of } from 'rxjs';
import { MatDialogModule } from '@angular/material/dialog';

// @ts-ignore
describe('NavbarComponent', () => {
    let component: NavbarComponent;
    let fixture: ComponentFixture<NavbarComponent>;
    let authServiceMock: any;

    // @ts-ignore
    beforeEach(async () => {
        // Création d'un mock pour le service
        authServiceMock = {
            currentUser$: of(null), // Utilisateur déconnecté par défaut
            // @ts-ignore
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

    // @ts-ignore
    it('should display "Connexion" button when user is not logged in', () => {
        const compiled = fixture.nativeElement;
        // @ts-ignore
        expect(compiled.querySelector('button').textContent).toContain('Connexion');
    });

    // @ts-ignore
    it('should display "Bonjour" when user is logged in', () => {
        authServiceMock.currentUser$ = of('Arona');
        fixture.detectChanges(); // On force la mise à jour du HTML

        const compiled = fixture.nativeElement;
        // @ts-ignore
        expect(compiled.textContent).toContain('Bonjour, Arona');
    });
});
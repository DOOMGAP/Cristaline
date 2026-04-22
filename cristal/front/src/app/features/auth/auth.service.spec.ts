import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
    let service: AuthService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                AuthService,
                provideHttpClient(),        // Fournit le client HTTP réel
                provideHttpClientTesting() // Fournit les outils de mock (remplace le module)
            ]
        });
        service = TestBed.inject(AuthService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should login and return a token', () => {
        const mockResponse = { token: 'token-123' };
        const credentials = { email: 'test@test.com', password: 'password' };

        service.login(credentials).subscribe(res => {
            expect(res).toEqual(mockResponse);
        });

        // On s'attend à un appel vers cette URL
        const req = httpMock.expectOne('http://localhost:8080/auth/login');
        expect(req.request.method).toBe('POST');

        // On simule la réponse
        req.flush(mockResponse);
    });
});
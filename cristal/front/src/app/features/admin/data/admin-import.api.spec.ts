import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AdminImportApi } from './admin-import.api';

describe('AdminImportApi', () => {
  let service: AdminImportApi;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.setItem('token', 'admin-token');
    TestBed.configureTestingModule({
      providers: [
        AdminImportApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(AdminImportApi);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    localStorage.clear();
    httpMock.verify();
  });

  it('should trigger the admin import endpoint with auth header', () => {
    service.triggerFreeToGameImport().subscribe();

    const req = httpMock.expectOne('http://localhost:8080/admin/import/freetogame');
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe('Bearer admin-token');
    req.flush({});
  });
});

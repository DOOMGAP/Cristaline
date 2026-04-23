import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AdminImportApi } from '../../data/admin-import.api';
import { AdminImportPage } from './admin-import.page';

describe('AdminImportPage', () => {
  let fixture: ComponentFixture<AdminImportPage>;
  let component: AdminImportPage;
  let adminImportApiSpy: jasmine.SpyObj<AdminImportApi>;

  beforeEach(async () => {
    adminImportApiSpy = jasmine.createSpyObj<AdminImportApi>('AdminImportApi', ['triggerFreeToGameImport']);
    adminImportApiSpy.triggerFreeToGameImport.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [AdminImportPage],
      providers: [{ provide: AdminImportApi, useValue: adminImportApiSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminImportPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should trigger import and show success message', () => {
    component.triggerImport();

    expect(adminImportApiSpy.triggerFreeToGameImport).toHaveBeenCalled();
    expect(component.message).toBe("Demande d'import envoyee.");
  });

  it('should surface import errors', () => {
    adminImportApiSpy.triggerFreeToGameImport.and.returnValue(throwError(() => new Error('ko')));

    component.triggerImport();

    expect(component.message).toBe("Impossible de lancer l'import.");
  });
});

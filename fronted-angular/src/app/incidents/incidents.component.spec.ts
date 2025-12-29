import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IncidentsComponent } from './incidents.component';
import { IncidentsServiceService } from '../core/services/incidents-service.service';
import { TokenStorageService } from '../core/services/token-storage.service';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('IncidentsComponent', () => {
  let component: IncidentsComponent;
  let fixture: ComponentFixture<IncidentsComponent>;
  let incidentServiceSpy: jasmine.SpyObj<IncidentsServiceService>;
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;

  beforeEach(async () => {
    const iSpy = jasmine.createSpyObj('IncidentsServiceService', ['getAllIncidents']);
    const tSpy = jasmine.createSpyObj('TokenStorageService', ['getUserRole']);

    await TestBed.configureTestingModule({
      imports: [IncidentsComponent, HttpClientTestingModule],
      providers: [
        { provide: IncidentsServiceService, useValue: iSpy },
        { provide: TokenStorageService, useValue: tSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IncidentsComponent);
    component = fixture.componentInstance;
    incidentServiceSpy = TestBed.inject(IncidentsServiceService) as jasmine.SpyObj<IncidentsServiceService>;
    tokenStorageSpy = TestBed.inject(TokenStorageService) as jasmine.SpyObj<TokenStorageService>;

    incidentServiceSpy.getAllIncidents.and.returnValue(of([]));
    tokenStorageSpy.getUserRole.and.returnValue('USER');

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load incidents on init', () => {
    const dummyIncidents = [{ id: 1, title: 'Test' }];
    incidentServiceSpy.getAllIncidents.and.returnValue(of(dummyIncidents));

    component.ngOnInit();

    expect(incidentServiceSpy.getAllIncidents).toHaveBeenCalled();
    expect(component.incidents).toEqual(dummyIncidents as any);
  });

  it('should check if user is supervisor', () => {
    tokenStorageSpy.getUserRole.and.returnValue('SUPERVISOR');
    expect(component.isSupervisor()).toBeTrue();

    tokenStorageSpy.getUserRole.and.returnValue('USER');
    expect(component.isSupervisor()).toBeFalse();
  });
});

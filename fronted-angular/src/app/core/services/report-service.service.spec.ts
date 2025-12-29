import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReportServiceService } from './report-service.service';
import { IncidentReport } from '../models/report.model';
import { environment } from '../../../environments/environment';

describe('ReportServiceService', () => {
  let service: ReportServiceService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReportServiceService]
    });
    service = TestBed.inject(ReportServiceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get incident report via GET', () => {
    const dummyReport: IncidentReport = {
      topAreas: [{ areaName: 'Area 1', incidentCount: 5 }],
      criticalWeekly: [{ week: 'Week 1', incidentCount: 2 }]
    };

    service.getIncidentReport().subscribe(report => {
      expect(report).toEqual(dummyReport);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/incidents/reports`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyReport);
  });
});

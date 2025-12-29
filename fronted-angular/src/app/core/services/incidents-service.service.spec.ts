import { TestBed } from '@angular/core/testing';

import { IncidentsServiceService } from './incidents-service.service';

describe('IncidentsServiceService', () => {
  let service: IncidentsServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IncidentsServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

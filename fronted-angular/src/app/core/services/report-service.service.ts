import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IncidentReport } from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportServiceService {

  private apiUrl = 'http://localhost:8080/api/incidents/reports';

  constructor(private http: HttpClient) { }

  getIncidentReport(): Observable<IncidentReport> {
    return this.http.get<IncidentReport>(this.apiUrl);
  }
}

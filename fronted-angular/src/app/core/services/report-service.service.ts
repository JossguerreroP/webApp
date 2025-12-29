import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IncidentReport } from '../models/report.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportServiceService {

  private apiUrl = `${environment.apiUrl}/incidents/reports`;

  constructor(private http: HttpClient) { }

  getIncidentReport(): Observable<IncidentReport> {
    return this.http.get<IncidentReport>(this.apiUrl);
  }
}

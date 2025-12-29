import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Incident, IncidentCriteria } from '../models/incident.model';
import { HistoryDTO } from '../models/history.model';

@Injectable({
  providedIn: 'root'
})
export class IncidentsServiceService {

  private apiUrl = 'http://localhost:8080/api/incidents';

  constructor(private http: HttpClient) { }

  getAllIncidents(criteria?: IncidentCriteria): Observable<any> {
    let params = new HttpParams();
    if (criteria) {
      const size = criteria.size || 10;
      const page = (criteria.page || 0) + 1; // Backend is 1-indexed

      Object.keys(criteria).forEach(key => {
        let value = (criteria as any)[key];
        if (value !== undefined && value !== null && value !== '') {
          // Skip specialized handling keys and pagination keys already handled or to be handled
          if (key === 'sortBy' || key === 'sortOrder' || key === 'page' || key === 'offset' || key === 'size') {
            return;
          }

          // Format dates for LocalDateTime.parse() -> "yyyy-MM-ddTHH:mm:ss"
          if ((key === 'startDate' || key === 'endDate') && typeof value === 'string') {
            if (value.length === 10) { // yyyy-MM-dd
              value = key === 'startDate' ? `${value}T00:00:00` : `${value}T23:59:59`;
            } else if (value.includes(' ')) {
              value = value.replace(' ', 'T');
            }
          }

          params = params.append(key, value.toString());
        }
      });

      // Add pagination params
      params = params.append('size', size.toString());
      params = params.append('page', page.toString());

      // Send sortBy and sortOrder as separate parameters
      if (criteria.sortBy) {
        params = params.append('sortBy', criteria.sortBy);
      }
      if (criteria.sortOrder) {
        params = params.append('sortOrder', criteria.sortOrder);
      }
    }

    console.log('Sending request to:', this.apiUrl, 'with params:', params.toString());
    return this.http.get<any>(this.apiUrl, { params });
  }

  getIncidentById(id: number): Observable<Incident> {
    return this.http.get<Incident>(`${this.apiUrl}/${id}`);
  }

  createIncident(incident: Incident): Observable<Incident> {
    return this.http.post<Incident>(this.apiUrl, incident);
  }

  updateIncident(id: number, incident: Incident, userId?: number): Observable<Incident> {
    let params = new HttpParams();
    if (userId) {
      params = params.set('userId', userId.toString());
    }
    return this.http.put<Incident>(`${this.apiUrl}/${id}`, incident, { params });
  }

  deleteIncident(id: number, userId?: number): Observable<any> {
    let params = new HttpParams();
    if (userId) {
      params = params.set('userId', userId.toString());
    }
    return this.http.delete<any>(`${this.apiUrl}/${id}`, { params });
  }

  getIncidentHistory(id: number): Observable<HistoryDTO[]> {
    return this.http.get<HistoryDTO[]>(`${this.apiUrl}/history/${id}`);
  }
}

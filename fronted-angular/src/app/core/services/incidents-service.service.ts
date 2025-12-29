import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Incident, IncidentCriteria } from '../models/incident.model';

@Injectable({
  providedIn: 'root'
})
export class IncidentsServiceService {

  private apiUrl = 'http://localhost:8080/api/incidents';

  constructor(private http: HttpClient) { }

  getAllIncidents(criteria?: IncidentCriteria): Observable<Incident[]> {
    let params = new HttpParams();
    if (criteria) {
      Object.keys(criteria).forEach(key => {
        const value = (criteria as any)[key];
        if (value !== undefined && value !== null) {
          params = params.append(key, value.toString());
        }
      });
    }
    return this.http.get<Incident[]>(this.apiUrl, { params });
  }

  getIncidentById(id: number): Observable<Incident> {
    return this.http.get<Incident>(`${this.apiUrl}/${id}`);
  }

  createIncident(incident: Incident): Observable<Incident> {
    return this.http.post<Incident>(this.apiUrl, incident);
  }

  updateIncident(id: number, incident: Incident, userId: number = 1): Observable<Incident> {
    let params = new HttpParams().set('userId', userId.toString());
    return this.http.put<Incident>(`${this.apiUrl}/${id}`, incident, { params });
  }

  deleteIncident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

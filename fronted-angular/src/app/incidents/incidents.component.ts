import { Component, OnInit } from '@angular/core';
import { IncidentsServiceService } from '../core/services/incidents-service.service';
import { Incident } from '../core/models/incident.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './incidents.component.html',
  styleUrl: './incidents.component.css'
})
export class IncidentsComponent implements OnInit {
  incidents: Incident[] = [];

  readonly statusMap: { [key: string]: string } = {
    'OPEN': 'Abierto',
    'IN_PROGRESS': 'En Progreso',
    'CLOSED': 'Cerrado'
  };

  readonly levelMap: { [key: string]: string } = {
    'LOW': 'Bajo',
    'MEDIUM': 'Medio',
    'HIGH': 'Alto',
    'CRITICAL': 'Crítico'
  };

  constructor(private incidentService: IncidentsServiceService) {}

  ngOnInit(): void {
    this.loadIncidents();
  }

  loadIncidents(): void {
    this.incidentService.getAllIncidents().subscribe({
      next: (data) => {
        this.incidents = data;
        console.log('Incidents loaded:', this.incidents);
      },
      error: (err) => {
        console.error('Error loading incidents:', err);
      }
    });
  }

  translateStatus(status: string): string {
    return this.statusMap[status.toUpperCase().replace(/\s+/g, '_')] || status;
  }

  translateLevel(level: string): string {
    return this.levelMap[level.toUpperCase()] || level;
  }

  getStatusClass(status: string): string {
    return 'badge-status-' + status.toLowerCase().replace(/\s+/g, '_');
  }

  getLevelClass(level: string): string {
    return 'badge-level-' + level.toLowerCase();
  }
}

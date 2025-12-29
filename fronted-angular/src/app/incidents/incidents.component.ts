import { Component, OnInit } from '@angular/core';
import { IncidentsServiceService } from '../core/services/incidents-service.service';
import { Incident, IncidentCriteria } from '../core/models/incident.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreateIncidentComponent } from './create-incident/create-incident.component';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule, FormsModule, CreateIncidentComponent],
  templateUrl: './incidents.component.html',
  styleUrl: './incidents.component.css'
})
export class IncidentsComponent implements OnInit {
  incidents: Incident[] = [];
  showModal = false;
  criteria: IncidentCriteria = {
    status: '',
    level: '',
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    sortOrder: 'desc'
  };

  readonly statusMap: { [key: string]: string } = {
    'abierto': 'Abierto',
    'analisis': 'En Análisis',
    'en progreso': 'En Progreso',
    'cerrado': 'Cerrado'
  };

  readonly levelMap: { [key: string]: string } = {
    'bajo': 'Bajo',
    'medio': 'Medio',
    'alto': 'Alto',
    'critico': 'Crítico'
  };

  constructor(private incidentService: IncidentsServiceService) {}

  ngOnInit(): void {
    this.loadIncidents();
  }

  loadIncidents(): void {
    this.incidentService.getAllIncidents(this.criteria).subscribe({
      next: (data) => {
        console.log('API Response:', data);
        // Handle both raw array and paginated response (Spring Page)
        if (Array.isArray(data)) {
          this.incidents = data;
        } else if (data && data.content && Array.isArray(data.content)) {
          this.incidents = data.content;
        } else if (data && Array.isArray(data.incidents)) {
          // Some custom backends might wrap it in an object with a different key
          this.incidents = data.incidents;
        } else {
          this.incidents = [];
          console.warn('Unexpected data format from incidents API:', data);
        }
        console.log('Incidents processed:', this.incidents);
      },
      error: (err) => {
        console.error('Error loading incidents:', err);
      }
    });
  }

  applyFilters(): void {
    this.criteria.page = 0;
    this.loadIncidents();
  }

  openCreateModal(): void {
    this.showModal = true;
  }

  closeCreateModal(): void {
    this.showModal = false;
    this.loadIncidents();
  }

  resetFilters(): void {
    this.criteria = {
      status: '',
      level: '',
      page: 0,
      size: 10,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    };
    this.loadIncidents();
  }

  changePage(page: number): void {
    this.criteria.page = page;
    this.loadIncidents();
  }

  changeSort(sortBy: string): void {
    if (this.criteria.sortBy === sortBy) {
      this.criteria.sortOrder = this.criteria.sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      this.criteria.sortBy = sortBy;
      this.criteria.sortOrder = 'asc';
    }
    this.loadIncidents();
  }

  translateStatus(status: string): string {
    if (!status) return '';
    const key = status.toLowerCase();
    return this.statusMap[key] || status;
  }

  translateLevel(level: string): string {
    if (!level) return '';
    const key = level.toLowerCase();
    return this.levelMap[key] || level;
  }

  getStatusClass(status: string): string {
    if (!status) return '';
    const statusKey = status.toLowerCase().replace(/\s+/g, '_');
    const map: { [key: string]: string } = {
      'abierto': 'open',
      'analisis': 'in_analysis',
      'en_progreso': 'in_progress',
      'cerrado': 'closed'
    };
    return 'badge-status-' + (map[statusKey] || statusKey.replace(/[áéíóú]/g, (match) => {
      const accents: { [key: string]: string } = { 'á': 'a', 'é': 'e', 'í': 'i', 'ó': 'o', 'ú': 'u' };
      return accents[match];
    }));
  }

  getLevelClass(level: string): string {
    if (!level) return '';
    const levelKey = level.toLowerCase();
    const map: { [key: string]: string } = {
      'bajo': 'low',
      'medio': 'medium',
      'alto': 'high',
      'critico': 'critical',
      'crítico': 'critical'
    };
    return 'badge-level-' + (map[levelKey] || levelKey.replace(/[áéíóú]/g, (match) => {
      const accents: { [key: string]: string } = { 'á': 'a', 'é': 'e', 'í': 'i', 'ó': 'o', 'ú': 'u' };
      return accents[match];
    }));
  }
}

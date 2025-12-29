import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentsServiceService } from '../../core/services/incidents-service.service';
import { HistoryDTO } from '../../core/models/history.model';

@Component({
  selector: 'app-incident-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './incident-history.component.html',
  styleUrl: './incident-history.component.css'
})
export class IncidentHistoryComponent implements OnInit {
  @Input() incidentId?: number;
  @Output() onClose = new EventEmitter<void>();

  history: HistoryDTO[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(private incidentService: IncidentsServiceService) {}

  ngOnInit(): void {
    if (this.incidentId) {
      this.loadHistory();
    }
  }

  loadHistory(): void {
    if (!this.incidentId) return;

    this.isLoading = true;
    this.incidentService.getIncidentHistory(this.incidentId).subscribe({
      next: (data) => {
        this.history = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading history:', err);
        this.error = 'No se pudo cargar el historial.';
        this.isLoading = false;
      }
    });
  }

  close(): void {
    this.onClose.emit();
  }
}

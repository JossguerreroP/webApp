import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportServiceService } from '../../core/services/report-service.service';
import { IncidentReport } from '../../core/models/report.model';

@Component({
  selector: 'app-report-incident',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './report-incident.component.html',
  styleUrl: './report-incident.component.css'
})
export class ReportIncidentComponent implements OnInit {
  report: IncidentReport | null = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(private reportService: ReportServiceService) { }

  ngOnInit(): void {
    this.fetchReport();
  }

  fetchReport(): void {
    this.loading = true;
    this.reportService.getIncidentReport().subscribe({
      next: (data) => {
        this.report = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching report:', err);
        this.error = 'Failed to load report data.';
        this.loading = false;
      }
    });
  }
}

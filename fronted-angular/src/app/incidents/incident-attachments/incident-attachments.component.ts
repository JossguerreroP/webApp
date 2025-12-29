import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncidentsServiceService } from '../../core/services/incidents-service.service';
import { AttachmentDTO } from '../../core/models/attachment.model';

@Component({
  selector: 'app-incident-attachments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './incident-attachments.component.html',
  styleUrl: './incident-attachments.component.css'
})
export class IncidentAttachmentsComponent implements OnInit {
  @Input() incidentId!: number;
  @Output() onClose = new EventEmitter<void>();

  attachments: AttachmentDTO[] = [];
  selectedFile: File | null = null;
  loading = false;
  uploading = false;

  constructor(private incidentService: IncidentsServiceService) {}

  ngOnInit(): void {
    this.loadAttachments();
  }

  loadAttachments(): void {
    this.loading = true;
    this.incidentService.getAttachments(this.incidentId).subscribe({
      next: (data) => {
        this.attachments = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading attachments:', err);
        this.loading = false;
      }
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] || null;
  }

  uploadFile(): void {
    if (!this.selectedFile) return;

    this.uploading = true;
    this.incidentService.uploadAttachment(this.incidentId, this.selectedFile).subscribe({
      next: (data) => {
        this.attachments.push(data);
        this.selectedFile = null;
        this.uploading = false;
      },
      error: (err) => {
        console.error('Error uploading file:', err);
        const errorMessage = err.error?.error || err.message || 'Error desconocido al subir el archivo.';
        alert('Error al subir el archivo: ' + errorMessage);
        this.uploading = false;
      }
    });
  }

  close(): void {
    this.onClose.emit();
  }
}

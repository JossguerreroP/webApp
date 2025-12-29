import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { IncidentsServiceService } from '../../core/services/incidents-service.service';
import { TokenStorageService } from '../../core/services/token-storage.service';
import { Incident } from '../../core/models/incident.model';

@Component({
  selector: 'app-create-incident',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-incident.component.html',
  styleUrl: './create-incident.component.css'
})
export class CreateIncidentComponent implements OnInit {
  @Input() incident?: Incident | null;
  @Output() onClose = new EventEmitter<void>();
  incidentForm: FormGroup;
  isSubmitting = false;

  readonly statusMap = [
    { key: 'abierto', value: 'Abierto' },
    { key: 'analisis', value: 'En Análisis' },
    { key: 'en progreso', value: 'En Progreso' },
    { key: 'cerrado', value: 'Cerrado' }
  ];

  readonly levelMap = [
    { key: 'bajo', value: 'Bajo' },
    { key: 'medio', value: 'Medio' },
    { key: 'alto', value: 'Alto' },
    { key: 'critico', value: 'Crítico' }
  ];

  constructor(
    private fb: FormBuilder,
    private incidentService: IncidentsServiceService,
    private tokenStorage: TokenStorageService
  ) {
    this.incidentForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      type: ['', Validators.required],
      level: ['bajo', Validators.required],
      status: ['abierto', Validators.required],
      responsibleId: [null, [Validators.min(1)]],
      areaId: [null, [Validators.required, Validators.min(1)]]
    });
  }

  isSupervisor(): boolean {
    return this.tokenStorage.getUserRole() === 'SUPERVISOR';
  }

  ngOnInit(): void {
    if (this.incident) {
      this.incidentForm.patchValue({
        title: this.incident.title,
        description: this.incident.description,
        type: this.incident.type,
        level: this.incident.level,
        status: this.incident.status,
        responsibleId: this.incident.responsibleId,
        areaId: this.incident.areaId
      });
    }
  }

  onSubmit(): void {
    if (this.incidentForm.valid) {
      this.isSubmitting = true;

      const incidentData = {
        ...this.incidentForm.value,
        version: this.incident?.version
      };

      if (!this.incident) {
        // Automatically set responsibleId for new incidents
        const userId = this.tokenStorage.getUserId();
        if (userId) {
          incidentData.responsibleId = userId;
        }
      }

      if (this.incident && this.incident.id) {
        // Edit mode
        const userId = this.tokenStorage.getUserId();
        this.incidentService.updateIncident(this.incident.id, incidentData, userId || undefined).subscribe({
          next: () => {
            this.isSubmitting = false;
            this.close();
          },
          error: (err) => {
            this.isSubmitting = false;
            console.error('Error updating incident:', err);
            alert('Error al actualizar el incidente. Por favor, intente de nuevo.');
          }
        });
      } else {
        // Create mode
        this.incidentService.createIncident(incidentData).subscribe({
          next: () => {
            this.isSubmitting = false;
            this.close();
          },
          error: (err) => {
            this.isSubmitting = false;
            console.error('Error creating incident:', err);
            alert('Error al crear el incidente. Por favor, intente de nuevo.');
          }
        });
      }
    } else {
      this.markFormGroupTouched(this.incidentForm);
    }
  }

  close() {
    this.onClose.emit();
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }
}

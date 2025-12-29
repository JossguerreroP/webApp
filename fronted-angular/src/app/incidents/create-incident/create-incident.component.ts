import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-create-incident',
  standalone: true,
  imports: [],
  templateUrl: './create-incident.component.html',
  styleUrl: './create-incident.component.css'
})
export class CreateIncidentComponent {
  @Output() onClose = new EventEmitter<void>();

  close() {
    this.onClose.emit();
  }
}

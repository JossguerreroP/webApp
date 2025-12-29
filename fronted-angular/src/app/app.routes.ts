import { Routes } from '@angular/router';
import {AuthComponent} from './auth/auth.component';
import {IncidentsComponent} from './incidents/incidents.component';
import {ReportIncidentComponent} from './incidents/report-incident/report-incident.component';

export const routes: Routes = [

  { path: '', component: AuthComponent},
  { path: 'incidents', component: IncidentsComponent},
  { path: 'report-incident', component: ReportIncidentComponent},
];

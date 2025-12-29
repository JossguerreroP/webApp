export interface AreaIncidentCount {
  areaName: string;
  incidentCount: number;
}

export interface WeeklyIncidentCount {
  week: string;
  incidentCount: number;
}

export interface IncidentReport {
  topAreas: AreaIncidentCount[];
  criticalWeekly: WeeklyIncidentCount[];
}

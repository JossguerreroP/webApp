export interface Incident {
  id?: number;
  title: string;
  description: string;
  type: string;
  level: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
  responsibleId: number;
  areaId: number;
  createdBy?: number;
  version?: number;
}

export interface IncidentCriteria {
  status?: string;
  level?: string;
  areaId?: number;
  responsibleId?: number;
  searchTerm?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: string;
  startDate?: string;
  endDate?: string;
  offset?: number;
}

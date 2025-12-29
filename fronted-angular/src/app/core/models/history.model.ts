export interface HistoryDTO {
    id: number;
    incidentId: number;
    changedBy: number;
    changedByName?: string;
    fieldName: string;
    oldValue: string;
    newValue: string;
    changedAt: string;
}

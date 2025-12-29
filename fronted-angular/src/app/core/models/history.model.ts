export interface HistoryDTO {
    id: number;
    incidentId: number;
    changedBy: number;
    fieldName: string;
    oldValue: string;
    newValue: string;
    changedAt: string;
}

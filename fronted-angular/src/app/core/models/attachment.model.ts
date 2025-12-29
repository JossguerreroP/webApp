export interface AttachmentDTO {
  id?: number;
  incidentId: number;
  originalFilename: string;
  storedFilename?: string;
  fileSize: number;
  mimeType: string;
  description?: string;
  uploadedBy?: number;
  uploadedAt?: string;
}

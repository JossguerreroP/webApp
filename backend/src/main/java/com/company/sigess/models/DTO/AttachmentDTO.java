package com.company.sigess.models.DTO;

import java.time.LocalDateTime;

public class AttachmentDTO {
    private int id;
    private int incidentId;
    private String originalFilename;
    private String storedFilename;
    private long fileSize;
    private String mimeType;
    private String description;
    private int uploadedBy;
    private LocalDateTime uploadedAt;

    public AttachmentDTO() {}

    public AttachmentDTO(int id, int incidentId, String originalFilename, String storedFilename, long fileSize, String mimeType, String description, int uploadedBy, LocalDateTime uploadedAt) {
        this.id = id;
        this.incidentId = incidentId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.description = description;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIncidentId() { return incidentId; }
    public void setIncidentId(int incidentId) { this.incidentId = incidentId; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(int uploadedBy) { this.uploadedBy = uploadedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}

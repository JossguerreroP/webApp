package com.company.sigess.models.DTO;

import java.time.LocalDateTime;

public class IncidentDTO {

    private int id;

    private String title;
    private String description;
    private String type;      // casi incidente, condición insegura, etc.
    private String level;     // bajo, medio, alto
    private String status;    // abierto, en análisis, cerrado

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int responsibleId;
    private int areaId;
    private int createdBy;

    private int version;


    public IncidentDTO() {
    }

    public IncidentDTO(
            int id,
            String title,
            String description,
            String type,
            String level,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            int responsibleId,
            int areaId,
            int createdBy,
            int version
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.level = level;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.responsibleId = responsibleId;
        this.areaId = areaId;
        this.createdBy = createdBy;
        this.version = version;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "{"
                + "\"id\":" + id + ","
                + "\"title\":\"" + title + "\","
                + "\"description\":\"" + description + "\","
                + "\"type\":\"" + type + "\","
                + "\"level\":\"" + level + "\","
                + "\"status\":\"" + status + "\","
                + "\"createdAt\":\"" + createdAt + "\","
                + "\"updatedAt\":\"" + updatedAt + "\","
                + "\"responsibleId\":" + responsibleId + ","
                + "\"areaId\":" + areaId + ","
                + "\"createdBy\":" + createdBy + ","
                + "\"version\":" + version
                + "}";
    }
}

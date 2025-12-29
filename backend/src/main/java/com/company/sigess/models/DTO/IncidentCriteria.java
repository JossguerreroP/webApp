package com.company.sigess.models.DTO;

import java.time.LocalDateTime;

public class IncidentCriteria {
    private String status;
    private String level;
    private Integer responsibleId;
    private Integer areaId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String searchTerm;
    
    private int page = 1;
    private int size = 3;
    private String sortBy = "created_at";
    private String sortOrder = "DESC";

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Integer getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Integer responsibleId) { this.responsibleId = responsibleId; }

    public Integer getAreaId() { return areaId; }
    public void setAreaId(Integer areaId) { this.areaId = areaId; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public int getOffset() {
        return (page - 1) * size;
    }
}

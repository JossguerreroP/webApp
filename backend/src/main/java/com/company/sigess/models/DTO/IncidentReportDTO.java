package com.company.sigess.models.DTO;

import java.util.List;

public class IncidentReportDTO {
    private List<AreaIncidentCountDTO> topAreas;
    private List<WeeklyIncidentCountDTO> criticalWeekly;

    public IncidentReportDTO(List<AreaIncidentCountDTO> topAreas, List<WeeklyIncidentCountDTO> criticalWeekly) {
        this.topAreas = topAreas;
        this.criticalWeekly = criticalWeekly;
    }

    public List<AreaIncidentCountDTO> getTopAreas() { return topAreas; }
    public void setTopAreas(List<AreaIncidentCountDTO> topAreas) { this.topAreas = topAreas; }
    public List<WeeklyIncidentCountDTO> getCriticalWeekly() { return criticalWeekly; }
    public void setCriticalWeekly(List<WeeklyIncidentCountDTO> criticalWeekly) { this.criticalWeekly = criticalWeekly; }
}

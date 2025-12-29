package com.company.sigess.models.DTO;

public class AreaIncidentCountDTO {
    private String areaName;
    private int incidentCount;

    public AreaIncidentCountDTO(String areaName, int incidentCount) {
        this.areaName = areaName;
        this.incidentCount = incidentCount;
    }

    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public int getIncidentCount() { return incidentCount; }
    public void setIncidentCount(int incidentCount) { this.incidentCount = incidentCount; }
}

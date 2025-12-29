package com.company.sigess.models.DTO;

public class WeeklyIncidentCountDTO {
    private String week;
    private int incidentCount;

    public WeeklyIncidentCountDTO(String week, int incidentCount) {
        this.week = week;
        this.incidentCount = incidentCount;
    }

    public String getWeek() { return week; }
    public void setWeek(String week) { this.week = week; }
    public int getIncidentCount() { return incidentCount; }
    public void setIncidentCount(int incidentCount) { this.incidentCount = incidentCount; }
}

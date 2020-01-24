package com.EMS.dto;

public class submissionHistoryRequestDTO {

    private long loggedId;

    private long projectId;

    private String period;

    public long getLoggedId() {
        return loggedId;
    }

    public void setLoggedId(long loggedId) {
        this.loggedId = loggedId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}

package com.EMS.dto;

import java.util.List;

public class WeeklyTaskTrackWithTaskResponse {

	private String date;
	private Boolean enabled;
	private Double finalHour;

	private List<TasktrackDto> taskList;
	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Double getFinalHour() {
		return finalHour;
	}

	public void setFinalHour(Double finalHour) {
		this.finalHour = finalHour;
	}

	public List<TasktrackDto> getTaskList() {
		return taskList;
	}
	
	public void setTaskList(List<TasktrackDto> taskList) {
		this.taskList = taskList;
	}
	
}

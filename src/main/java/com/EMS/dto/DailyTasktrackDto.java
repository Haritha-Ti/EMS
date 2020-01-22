package com.EMS.dto;

public class DailyTasktrackDto {

	private Double hour;
	private String taskSummary;
	private Long taskType;
	
	public Double getHour() {
		return hour;
	}
	public void setHour(Double hour) {
		this.hour = hour;
	}
	public String getTaskSummary() {
		return taskSummary;
	}
	public void setTaskSummary(String taskSummary) {
		this.taskSummary = taskSummary;
	}
	public Long getTaskType() {
		return taskType;
	}
	public void setTaskType(Long taskType) {
		this.taskType = taskType;
	}
	
	
}

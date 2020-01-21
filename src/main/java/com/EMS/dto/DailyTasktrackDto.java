package com.EMS.dto;

public class DailyTasktrackDto {

	private Double hour;
	private String taskSummary;
	private Integer taskType;
	
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
	public Integer getTaskType() {
		return taskType;
	}
	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}
	
	
}

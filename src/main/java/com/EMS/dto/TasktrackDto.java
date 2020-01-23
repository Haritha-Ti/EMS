package com.EMS.dto;

public class TasktrackDto {

	private Double hour;
	private Long taskTypeId;
	private String taskType;
	private String description;
	private String date;
	public Double getHour() {
		return hour;
	}
	public void setHour(Double hour) {
		this.hour = hour;
	}
	public Long getTaskTypeId() {
		return taskTypeId;
	}
	public void setTaskTypeId(Long taskTypeId) {
		this.taskTypeId = taskTypeId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getTaskType() {
		return taskType;
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
}

package com.EMS.dto;

public class SaveWeeklyTasktrackWithTaskRequestDTO {

	private Long uId;
	private Long projectId;
	private Long sessionId;


	
	private String startDate;
	private String endDate;
	
	private TasktrackDto task;
	
	public TasktrackDto getTask() {
		return task;
	}
	
	public void setTask(TasktrackDto task) {
		this.task = task;
	}
	
	public Long getuId() {
		return uId;
	}

	public void setuId(Long uId) {
		this.uId = uId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}


	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


}

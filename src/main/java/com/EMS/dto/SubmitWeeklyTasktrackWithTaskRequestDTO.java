package com.EMS.dto;

import java.util.List;

public class SubmitWeeklyTasktrackWithTaskRequestDTO {
	private Long uId;
	private Long projectId;
	private Long sessionId;

	private String startDate;
	private String endDate;
	
	private List<DateBasedTaskDto> dateBasedTaskDtoList;

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

	public List<DateBasedTaskDto> getDateBasedTaskDtoList() {
		return dateBasedTaskDtoList;
	}

	public void setDateBasedTaskDtoList(List<DateBasedTaskDto> dateBasedTaskDtoList) {
		this.dateBasedTaskDtoList = dateBasedTaskDtoList;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}

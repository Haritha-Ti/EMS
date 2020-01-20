package com.EMS.dto;

import java.util.Date;
import java.util.HashMap;

import com.EMS.model.Tasktrack;

public class SaveWeeklyTasktrackWithTaskRequestDTO {
	private Long uId;
	private Long projectId;
	private String startDate;
	private String endDate;
	private Long sessionId;
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
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	
	HashMap<Date, Tasktrack> dateTaskMap = new HashMap<>();
	public HashMap<Date, Tasktrack> getDateTaskMap() {
		return dateTaskMap;
	}
	
	public void setDateTaskMap(HashMap<Date, Tasktrack> dateTaskMap) {
		this.dateTaskMap = dateTaskMap;
	}
	
}

package com.EMS.dto.tasktrackapproval2.request;

import java.util.Date;

public class ApproveHoursRequest {

	
	private long projectId;
	
	private long userId;
	
	private long loggedId;
	
	private long approverId;
	
	private String endDate;
	
	private String startDate;
	
	private long sessionId;

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getLoggedId() {
		return loggedId;
	}

	public void setLoggedId(long loggedId) {
		this.loggedId = loggedId;
	}

	public long getApproverId() {
		return approverId;
	}

	public void setApproverId(long approverId) {
		this.approverId = approverId;
	}



	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public ApproveHoursRequest(long projectId, long userId, long loggedId, long approverId, String endDate,
			String startDate,long sessionId) {
		super();
		this.projectId = projectId;
		this.userId = userId;
		this.loggedId = loggedId;
		this.approverId = approverId;
		this.endDate = endDate;
		this.startDate = startDate;
		this.sessionId = sessionId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public ApproveHoursRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}

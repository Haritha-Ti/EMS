package com.EMS.dto.tasktrackapproval2.request;

import java.util.Date;

public class ApproveHoursRequest {

	
	private long projectId;
	
	private long userId;
	
	private long loggedId;
	
	private long approverId;
	
	private Date endDate;
	
	private Date startDate;

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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public ApproveHoursRequest(long projectId, long userId, long loggedId, long approverId, Date endDate,
			Date startDate) {
		super();
		this.projectId = projectId;
		this.userId = userId;
		this.loggedId = loggedId;
		this.approverId = approverId;
		this.endDate = endDate;
		this.startDate = startDate;
	}
	
	
	
}

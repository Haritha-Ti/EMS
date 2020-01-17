/**
* DTO Class for all request in approver one screen
* @author  Jinu Shaji
* @version 1.0
* @since   2020-01-15
*/
package com.EMS.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApproverOneDto {

	private Long projectId;
	private ArrayList<Long> userId;
	private Long approverId;
	private Date endDate;
	private Date startDate;
	private Long sessionId;
	
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public ArrayList<Long> getUserId() {
		return userId;
	}
	public void setUserId(ArrayList<Long> userId) {
		this.userId = userId;
	}

	public Long getApproverId() {
		return approverId;
	}
	public void setApproverId(Long approverId) {
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
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	
	
}

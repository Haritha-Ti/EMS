package com.EMS.dto;

import java.util.Date;

public class AssignUserDto {
	
	private Long app1Id;
	
	private Long app2Id;
	
	private Date startDate;
	
	private Date endDate;
	
	private Long projectId;

	public Long getApp1Id() {
		return app1Id;
	}

	public void setApp1Id(Long app1Id) {
		this.app1Id = app1Id;
	}

	public Long getApp2Id() {
		return app2Id;
	}

	public void setApp2Id(Long app2Id) {
		this.app2Id = app2Id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	

	
	

}

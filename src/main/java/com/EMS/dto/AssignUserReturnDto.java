package com.EMS.dto;

public class AssignUserReturnDto {
	
	private Long assignUserId;
	
	private String userName;
	
	private Boolean status;
	
	private Long userId ;
	
	private  Long projectId;
	
	private  String projectName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Long getAssignUserId() {
		return assignUserId;
	}

	public void setAssignUserId(Long assignUserId) {
		this.assignUserId = assignUserId;
	}
	
	
	
	

}

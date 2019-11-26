package com.EMS.dto;

import java.util.HashMap;
import java.util.List;

public class ProjectSubmissionDataDTO {

	private Long projectId;
	private String projectName;
	private Integer projectTier;
	private String clientName;
	private HashMap<String, Object> approver1;
	private HashMap<String, Object> approver2;

	private List<UsersEntryApprovalDetailsDTO> users;

	public ProjectSubmissionDataDTO() {
		super();
	}

	public ProjectSubmissionDataDTO(Long projectId, String projectName, Integer projectTier, String clientName,
			HashMap<String, Object> approver1, HashMap<String, Object> approver2) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.projectTier = projectTier;
		this.clientName = clientName;
		this.approver1 = approver1;
		this.approver2 = approver2;
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

	public Integer getProjectTier() {
		return projectTier;
	}

	public void setProjectTier(Integer projectTier) {
		this.projectTier = projectTier;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public HashMap<String, Object> getApprover1() {
		return approver1;
	}

	public void setApprover1(HashMap<String, Object> approver1) {
		this.approver1 = approver1;
	}

	public HashMap<String, Object> getApprover2() {
		return approver2;
	}

	public void setApprover2(HashMap<String, Object> approver2) {
		this.approver2 = approver2;
	}

	public List<UsersEntryApprovalDetailsDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UsersEntryApprovalDetailsDTO> users) {
		this.users = users;
	}

}

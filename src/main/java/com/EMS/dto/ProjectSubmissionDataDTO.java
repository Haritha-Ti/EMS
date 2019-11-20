package com.EMS.dto;

import java.util.List;

public class ProjectSubmissionDataDTO {

	private Long projectId;
	private String projectName;
	private Integer projectTier;
	private String clientName;

	private List<UsersEntryApprovalDetailsDTO> approver1;
	private List<UsersEntryApprovalDetailsDTO> approverFinal;

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

	public List<UsersEntryApprovalDetailsDTO> getApprover1() {
		return approver1;
	}

	public void setApprover1(List<UsersEntryApprovalDetailsDTO> approver1) {
		this.approver1 = approver1;
	}

	public List<UsersEntryApprovalDetailsDTO> getApproverFinal() {
		return approverFinal;
	}

	public void setApproverFinal(List<UsersEntryApprovalDetailsDTO> approverFinal) {
		this.approverFinal = approverFinal;
	}

}

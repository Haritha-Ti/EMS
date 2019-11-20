package com.EMS.dto;

public class UsersEntryApprovalDetailsDTO {

	private Integer userId;
	private String userName;
	private String userRegion;
	private Integer expectedApproverId;
	private String expectedApproverName;
	private Integer actualApproverId;
	private String actualApproverName;
	private Double billableHours;
	private Double overtimeHours;
	private String status;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserRegion() {
		return userRegion;
	}

	public void setUserRegion(String userRegion) {
		this.userRegion = userRegion;
	}

	public Integer getExpectedApproverId() {
		return expectedApproverId;
	}

	public void setExpectedApproverId(Integer expectedApproverId) {
		this.expectedApproverId = expectedApproverId;
	}

	public String getExpectedApproverName() {
		return expectedApproverName;
	}

	public void setExpectedApproverName(String expectedApproverName) {
		this.expectedApproverName = expectedApproverName;
	}

	public Integer getActualApproverId() {
		return actualApproverId;
	}

	public void setActualApproverId(Integer actualApproverId) {
		this.actualApproverId = actualApproverId;
	}

	public String getActualApproverName() {
		return actualApproverName;
	}

	public void setActualApproverName(String actualApproverName) {
		this.actualApproverName = actualApproverName;
	}

	public Double getBillableHours() {
		return billableHours;
	}

	public void setBillableHours(Double billableHours) {
		this.billableHours = billableHours;
	}

	public Double getOvertimeHours() {
		return overtimeHours;
	}

	public void setOvertimeHours(Double overtimeHours) {
		this.overtimeHours = overtimeHours;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

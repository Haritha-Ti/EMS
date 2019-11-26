package com.EMS.dto;

public class UsersEntryApprovalDetailsDTO {

	private Integer userId;
	private String userName;
	private String userRegion;
	private Double appr1FirstHalfTotalHours;
	private Double appr1SecondHalfTotalHours;
	private String appr1FirstHalfStatus;
	private String appr1SecondHalfStatus;
	private Double appr2FirstHalfTotalHours;
	private Double appr2SecondHalfTotalHours;
	private String appr2FirstHalfStatus;
	private String appr2SecondHalfStatus;

	public UsersEntryApprovalDetailsDTO(Integer userId, String userName, String userRegion, Double appr1FirstHalfHours,
			Double appr1SecondHalfHours, String appr1FirstHalfStatus, String appr1SecondHalfStatus,
			Double appr2FirstHalfHours, Double appr2SecondHalfHours, String appr2FirstHalfStatus,
			String appr2SecondHalfStatus) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userRegion = userRegion;
		this.appr1FirstHalfTotalHours = appr1FirstHalfHours;
		this.appr1SecondHalfTotalHours = appr1SecondHalfHours;
		this.appr1FirstHalfStatus = appr1FirstHalfStatus;
		this.appr1SecondHalfStatus = appr1SecondHalfStatus;
		this.appr2FirstHalfTotalHours = appr2FirstHalfHours;
		this.appr2SecondHalfTotalHours = appr2SecondHalfHours;
		this.appr2FirstHalfStatus = appr2FirstHalfStatus;
		this.appr2SecondHalfStatus = appr2SecondHalfStatus;
	}

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

	public String getAppr1FirstHalfStatus() {
		return appr1FirstHalfStatus;
	}

	public void setAppr1FirstHalfStatus(String appr1FirstHalfStatus) {
		this.appr1FirstHalfStatus = appr1FirstHalfStatus;
	}

	public String getAppr1SecondHalfStatus() {
		return appr1SecondHalfStatus;
	}

	public void setAppr1SecondHalfStatus(String appr1SecondHalfStatus) {
		this.appr1SecondHalfStatus = appr1SecondHalfStatus;
	}

	public String getAppr2FirstHalfStatus() {
		return appr2FirstHalfStatus;
	}

	public void setAppr2FirstHalfStatus(String appr2FirstHalfStatus) {
		this.appr2FirstHalfStatus = appr2FirstHalfStatus;
	}

	public String getAppr2SecondHalfStatus() {
		return appr2SecondHalfStatus;
	}

	public void setAppr2SecondHalfStatus(String appr2SecondHalfStatus) {
		this.appr2SecondHalfStatus = appr2SecondHalfStatus;
	}

	public Double getAppr1FirstHalfTotalHours() {
		return appr1FirstHalfTotalHours;
	}

	public void setAppr1FirstHalfTotalHours(Double appr1FirstHalfTotalHours) {
		this.appr1FirstHalfTotalHours = appr1FirstHalfTotalHours;
	}

	public Double getAppr1SecondHalfTotalHours() {
		return appr1SecondHalfTotalHours;
	}

	public void setAppr1SecondHalfTotalHours(Double appr1SecondHalfTotalHours) {
		this.appr1SecondHalfTotalHours = appr1SecondHalfTotalHours;
	}

	public Double getAppr2FirstHalfTotalHours() {
		return appr2FirstHalfTotalHours;
	}

	public void setAppr2FirstHalfTotalHours(Double appr2FirstHalfTotalHours) {
		this.appr2FirstHalfTotalHours = appr2FirstHalfTotalHours;
	}

	public Double getAppr2SecondHalfTotalHours() {
		return appr2SecondHalfTotalHours;
	}

	public void setAppr2SecondHalfTotalHours(Double appr2SecondHalfTotalHours) {
		this.appr2SecondHalfTotalHours = appr2SecondHalfTotalHours;
	}

}

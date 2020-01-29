package com.EMS.dto;

import java.util.List;

import org.json.simple.JSONObject;

public class SemiMonthlyTaskTrackWithTaskResponseDTO {

	
	private Boolean enabled;
	private JSONObject approver1;
	private JSONObject approver2;
//	private JSONObject user;
	private List<SemiMonthlyTaskTrackWithTaskResponse> tasktrackList;
	private String firstHalfFinalStatus;
	private String secondHalfFinalStatus;
	
	
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public JSONObject getApprover1() {
		return approver1;
	}
	public void setApprover1(JSONObject approver1) {
		this.approver1 = approver1;
	}
	public JSONObject getApprover2() {
		return approver2;
	}
	public void setApprover2(JSONObject approver2) {
		this.approver2 = approver2;
	}
//	public JSONObject getUser() {
//		return user;
//	}
//	public void setUser(JSONObject user) {
//		this.user = user;
//	}
	public List<SemiMonthlyTaskTrackWithTaskResponse> getTasktrackList() {
		return tasktrackList;
	}
	public void setTasktrackList(List<SemiMonthlyTaskTrackWithTaskResponse> tasktrackList) {
		this.tasktrackList = tasktrackList;
	}
	public String getFirstHalfFinalStatus() {
		return firstHalfFinalStatus;
	}
	public void setFirstHalfFinalStatus(String firstHalfFinalStatus) {
		this.firstHalfFinalStatus = firstHalfFinalStatus;
	}
	public String getSecondHalfFinalStatus() {
		return secondHalfFinalStatus;
	}
	public void setSecondHalfFinalStatus(String secondHalfFinalStatus) {
		this.secondHalfFinalStatus = secondHalfFinalStatus;
	}

	
	
}
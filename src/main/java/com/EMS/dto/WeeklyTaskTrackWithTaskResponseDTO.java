package com.EMS.dto;

import java.util.List;

import org.json.simple.JSONObject;

public class WeeklyTaskTrackWithTaskResponseDTO {

	private Boolean enabled;
	private JSONObject approver1;
	private JSONObject approver2;
	private List<WeeklyTaskTrackWithTaskResponse> tasktrackList;
	
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
	
	public List<WeeklyTaskTrackWithTaskResponse> getTasktrackList() {
		return tasktrackList;
	}

	public void setTasktrackList(List<WeeklyTaskTrackWithTaskResponse> tasktrackList) {
		this.tasktrackList = tasktrackList;
	}

	

}

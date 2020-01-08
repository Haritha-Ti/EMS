package com.EMS.service;

import org.json.simple.JSONObject;

public interface TaskWeeklyApprovalService {
	
	void submitWeeklyApproval(JSONObject requestData);

	void saveWeeklyApproval(JSONObject requestData);
	
	JSONObject getWeeklyTasktrack(JSONObject requestData);
}

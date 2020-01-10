package com.EMS.service;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public interface TaskWeeklyApprovalService {
	
	int submitWeeklyApproval(JSONObject requestData);

	int saveWeeklyApproval(JSONObject requestData);
	
	JSONObject getWeeklyTasktrack(JSONObject requestData);

	JSONObject getWeeklyTasktrackWithTask(JSONObject requestData);
	void getWeeklyTasksForSubmission(JsonNode requestData);
}


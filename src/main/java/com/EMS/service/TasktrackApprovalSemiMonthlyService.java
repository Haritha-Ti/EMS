package com.EMS.service;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public interface TasktrackApprovalSemiMonthlyService {
	JSONObject getSemiMonthlyTasktrack(JSONObject requestData) throws Exception;

	int submitForSemiMonthlyApproval(JSONObject requestData);

	int saveSemiMonthlyTaskTrackApproval(JSONObject requestData);

	int getSemiMonthlyTasksForSubmission(JsonNode requestData);
}

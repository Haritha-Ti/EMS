package com.EMS.service;

import org.json.simple.JSONObject;

public interface TasktrackApprovalSemiMonthlyService {
	JSONObject getSemiMonthlyTasktrack(JSONObject requestData) throws Exception;

	int submitForSemiMonthlyApproval(JSONObject requestData);

	int saveSemiMonthlyTaskTrackApproval(JSONObject requestData);
}

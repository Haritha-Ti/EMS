package com.EMS.service;

import org.json.simple.JSONObject;

public interface TasktrackApprovalSemiMonthlyService {
	JSONObject getSemiMonthlyTasktrack(JSONObject requestData);

	int submitForSemiMonthlyApproval(JSONObject requestData);

	int saveSemiMonthlyTaskTrackApproval(JSONObject requestData);
}

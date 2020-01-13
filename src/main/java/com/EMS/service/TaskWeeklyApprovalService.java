package com.EMS.service;

import java.text.ParseException;

import org.json.simple.JSONObject;

public interface TaskWeeklyApprovalService {
	
	int submitWeeklyApproval(JSONObject requestData);

	int saveWeeklyApproval(JSONObject requestData);
	
	JSONObject getWeeklyTasktrack(JSONObject requestData) throws ParseException, Exception;

	JSONObject getWeeklyTasktrackWithTask(JSONObject requestData);
}

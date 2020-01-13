package com.EMS.service;

import java.text.ParseException;

import org.json.simple.JSONObject;

import com.EMS.model.StatusResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface TasktrackApprovalSemiMonthlyService {
	JSONObject getSemiMonthlyTasktrack(JSONObject requestData) throws Exception;

	StatusResponse submitForSemiMonthlyApproval(JSONObject requestData) throws ParseException;

	StatusResponse saveSemiMonthlyTaskTrackApproval(JSONObject requestData) throws ParseException;

	StatusResponse getSemiMonthlyTasksForSubmission(JsonNode requestData) throws ParseException;
}

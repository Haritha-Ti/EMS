package com.EMS.service;

import java.text.ParseException;

import org.json.simple.JSONObject;

import com.EMS.dto.WeeklyTaskTrackWithoutTaskRequestDTO;
import com.EMS.model.StatusResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface TaskWeeklyApprovalService {
	
	StatusResponse submitWeeklyApproval(JSONObject requestData) throws ParseException,Exception;

	StatusResponse saveWeeklyApproval(JSONObject requestData) throws ParseException;
	
	StatusResponse getWeeklyTasktrack(WeeklyTaskTrackWithoutTaskRequestDTO requestData) throws ParseException, Exception;

	JSONObject getWeeklyTasktrackWithTask(JSONObject requestData);
	
	StatusResponse getWeeklyTasksForSubmission(JsonNode requestData) throws ParseException;
}


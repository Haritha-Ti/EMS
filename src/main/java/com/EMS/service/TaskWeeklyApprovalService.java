package com.EMS.service;

import java.text.ParseException;

import org.json.simple.JSONObject;

import com.EMS.dto.SaveWeeklyTasktrackWithTaskRequestDTO;
import com.EMS.dto.SubmitWeeklyTasktrackWithTaskRequestDTO2;
import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithTaskResponseDTO;
import com.EMS.dto.WeeklyTaskTrackWithoutTaskRequestDTO;
import com.EMS.model.StatusResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface TaskWeeklyApprovalService {
	
	StatusResponse submitWeeklyApproval(JSONObject requestData) throws ParseException,Exception;

	StatusResponse saveWeeklyApproval(JSONObject requestData) throws ParseException;
	
	StatusResponse getWeeklyTasktrack(WeeklyTaskTrackWithoutTaskRequestDTO requestData) throws  Exception;

	WeeklyTaskTrackWithTaskResponseDTO getWeeklyTasktrackWithTask(WeeklyTaskTrackWithTaskRequestDTO requestData) throws Exception;
	
	StatusResponse getWeeklyTasksForSubmission(JsonNode requestData) throws ParseException;

	StatusResponse saveWeeklyTasktrackWithTask(SaveWeeklyTasktrackWithTaskRequestDTO requestData) throws Exception;

	StatusResponse submitWeeklyTasktrackWithTask(SubmitWeeklyTasktrackWithTaskRequestDTO2 requestData) throws Exception;
}


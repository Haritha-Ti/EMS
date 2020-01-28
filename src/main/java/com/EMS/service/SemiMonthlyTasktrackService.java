package com.EMS.service;

import java.text.ParseException;

import org.json.simple.JSONObject;

import com.EMS.dto.DateBasedTaskTrackDto;
import com.EMS.dto.SaveSemiMonthlyWithTasksRequestDto;
import com.EMS.dto.SemiMonthlyTaskTrackRequestDTO;
import com.EMS.dto.SemiMonthlyTaskTrackWithTaskResponseDTO;
import com.EMS.dto.SubmitSemiMonthlyTasktrackWithTaskRequestDto;
import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.model.StatusResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface SemiMonthlyTasktrackService {
	
	
	StatusResponse getSemiMonthlyTasktrack(SemiMonthlyTaskTrackRequestDTO requestData) throws Exception;

	StatusResponse submitForSemiMonthlyApproval(JSONObject requestData) throws ParseException;

	StatusResponse saveSemiMonthlyTaskTrackApproval(JSONObject requestData) throws ParseException;

	StatusResponse getSemiMonthlyTasksForSubmission(JsonNode requestData) throws ParseException;

	SemiMonthlyTaskTrackWithTaskResponseDTO getSemiMonthlyTasktrackWithTask(SemiMonthlyTaskTrackRequestDTO requestData) throws Exception;
	
	  StatusResponse saveSemiMonthlyTasktrackWithTask(SaveSemiMonthlyWithTasksRequestDto dateBasedTaskTrackDto) throws Exception;
	    
	   StatusResponse submitSemiMonthlyTasktrackWithTask(SubmitSemiMonthlyTasktrackWithTaskRequestDto dateBasedTaskTrackDto) throws Exception ;
}

package com.EMS.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.SemiMonthlyTaskTrackRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.model.ExceptionResponse;
import com.EMS.model.StatusResponse;
import com.EMS.service.TasktrackApprovalSemiMonthlyService;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class TasktrackApprovalSemiMonthlyController {

	@Autowired
	private TasktrackApprovalSemiMonthlyService approvalSemiMonthlyService;

	@Autowired
	ObjectMapper objectmapper;

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/get/semi_monthly_tasktrack")
	public StatusResponse getSemiMonthlyTasktrack(@RequestBody SemiMonthlyTaskTrackRequestDTO requestData) {
		StatusResponse response = new StatusResponse();
		try {
			response = approvalSemiMonthlyService.getSemiMonthlyTasktrack(requestData);
		} catch (Exception e) {
			ExceptionResponse exceptionResponse = new ExceptionResponse(501, e.getMessage(), new Date());
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, exceptionResponse);
		}

		return response;
	}

	/*
	 * To submit timetrack semimonthly for approval
	 * 
	 * @Author Haritha version 1.0
	 * 
	 * @Since 09-01-2020
	 */

	@PostMapping(value = "/submitSemiMonthlyTaskTrack")
	public StatusResponse submitSemiMonthlyTaskTrack(@RequestBody JSONObject requestData) {

		StatusResponse response = new StatusResponse();
		try {
			response = approvalSemiMonthlyService.submitForSemiMonthlyApproval(requestData);
		} catch (Exception e) {
			ExceptionResponse exceptionresponse = new ExceptionResponse(501, e.getMessage(), new Date());
			response = new StatusResponse("Failure", 500, exceptionresponse);
		}

		return response;
	}

	/*
	 * To save timetrack semimonthly
	 * 
	 * @Author Haritha version 1.0
	 * 
	 * @Since 09-01-2020
	 */

	@PostMapping(value = "/saveSemiMonthlyTaskTrackApproval")
	public StatusResponse saveSemiMonthlyTaskTrackApproval(@RequestBody JSONObject requestData) {
		StatusResponse response = new StatusResponse();
		try {
		response = approvalSemiMonthlyService.saveSemiMonthlyTaskTrackApproval(requestData);

		}catch (Exception e) {
			ExceptionResponse exceptionresponse = new ExceptionResponse(501, e.getMessage(), new Date());
			response = new StatusResponse("Failure", 500, exceptionresponse);
		}

		return response;
	}

	/*
	 * To submit timetrack semimonthly with daily tasks
	 * 
	 * @Author Haritha version 1.0
	 * 
	 * @Since 10-01-2020
	 */

	@PostMapping(value = "/submitTasktrackSemimonthly")
	public StatusResponse submitTasktrackSemimonthly(@RequestBody JsonNode requestData, HttpServletResponse httpstatus) {

		StatusResponse response = new StatusResponse();
		try {

			response = approvalSemiMonthlyService.getSemiMonthlyTasksForSubmission(requestData);
			
		} catch (Exception e) {
			ExceptionResponse exceptionresponse = new ExceptionResponse(501, e.getMessage(), new Date());
			response = new StatusResponse("Failure", 500, exceptionresponse);
		}


		return response;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/get/tasktrack_with_task/semi_monthly")
	public StatusResponse getSemiMonthlyTasktrackWithTask(@RequestBody SemiMonthlyTaskTrackRequestDTO requestData) {
		StatusResponse response = new StatusResponse();
		try {
			response = approvalSemiMonthlyService.getSemiMonthlyTasktrackWithTask(requestData);
			System.out.println(response);
		} catch (Exception e) {

			ExceptionResponse exceptionResponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, exceptionResponse);

		}

		return response;
	}

}

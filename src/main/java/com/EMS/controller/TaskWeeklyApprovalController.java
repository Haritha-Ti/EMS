package com.EMS.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithoutTaskRequestDTO;
import com.EMS.model.ExceptionResponse;
import com.EMS.model.StatusResponse;
import com.EMS.service.TaskWeeklyApprovalService;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = { "/tasktrack" })
public class TaskWeeklyApprovalController {

	@Autowired
	private TaskWeeklyApprovalService weeklyApprovalService;

	@Autowired
	private ObjectMapper objectmapper;

	/*
	 * To submit timetrack weekly base for approval
	 * 
	 * @Author Haritha version 1.0
	 * 
	 * @Since 08-01-2020
	 */

	@PostMapping(value = "/submit_weekly_approval")
	public StatusResponse submitWeeklyApproval(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {

		StatusResponse response = new StatusResponse();
		try {
			response = weeklyApprovalService.submitWeeklyApproval(requestData);

		} catch (Exception e) {
			e.printStackTrace();
			ExceptionResponse exceptionresponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse("Failure", 500, exceptionresponse);
		}
		return response;
	}

	/*
	 * To save weekly timetrack without daily task
	 * 
	 * @Author Haritha version 1.0
	 * 
	 * @Since 08-01-2020
	 */

	@PostMapping(value = "/save_weekly_approval")
	public StatusResponse saveWeeklyApproval(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {

		StatusResponse response = new StatusResponse();

		try {
			response = weeklyApprovalService.saveWeeklyApproval(requestData);

		} catch (Exception e) {
			ExceptionResponse exceptionresponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse("Failure", 500, exceptionresponse);
		}

		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = "/get/weekly_tasktrack")
	public StatusResponse getWeeklyTasktrack(@RequestBody WeeklyTaskTrackWithoutTaskRequestDTO requestData) {
		StatusResponse response = new StatusResponse();
		try {
			response = weeklyApprovalService.getWeeklyTasktrack(requestData);
		} catch (Exception e) {
			ExceptionResponse exceptionResponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse(Constants.ERROR, Constants.ERROR_CODE, exceptionResponse);
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/get/tasktrack_with_task/weekly")
	public StatusResponse getWeeklyTasktrackWithTask(@RequestBody WeeklyTaskTrackWithTaskRequestDTO requestData) {
		StatusResponse response = new StatusResponse();
		try {
			response = weeklyApprovalService.getWeeklyTasktrackWithTask(requestData);
			System.out.println(response);
		} catch (Exception e) {
			ExceptionResponse exceptionResponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse(Constants.ERROR, Constants.ERROR_CODE, exceptionResponse);
		}

		return response;
	}

	/*
	 * To submit weekly timetrack for approval with daily task
	 * 
	 * @Author Haritha version 1.0
	 * 
	 * @Since 10-01-2020
	 */

	@PostMapping(value = "/submitTasktrackWeekly")
	public StatusResponse submitTasktrackWeekly(@RequestBody JsonNode requestData, HttpServletResponse httpstatus) {

		StatusResponse response = new StatusResponse();
		try {

			response = weeklyApprovalService.getWeeklyTasksForSubmission(requestData);
			

		} catch (Exception e) {
			ExceptionResponse exceptionresponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse("Failure", 500, exceptionresponse);
		}
		return response;
	}
	


}
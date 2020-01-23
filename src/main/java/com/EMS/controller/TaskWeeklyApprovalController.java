package com.EMS.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.dto.SaveWeeklyTasktrackWithTaskRequestDTO;
import com.EMS.dto.SaveWeeklyTasktrackWithTaskRequestDTO2;
import com.EMS.dto.SubmitWeeklyTasktrackWithTaskRequestDTO;
import com.EMS.dto.SubmitWeeklyTasktrackWithTaskRequestDTO2;
import com.EMS.dto.WeeklyTaskTrackWithTaskRequestDTO;
import com.EMS.dto.WeeklyTaskTrackWithoutTaskRequestDTO;
import com.EMS.model.ExceptionResponse;
import com.EMS.model.StatusResponse;
import com.EMS.service.TaskWeeklyApprovalService;
import com.EMS.utility.Constants;
import com.fasterxml.jackson.databind.JsonNode;


@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
@RequestMapping(value = { "/tasktrack" })
public class TaskWeeklyApprovalController {

	@Autowired
	private TaskWeeklyApprovalService weeklyApprovalService;

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
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, exceptionresponse);
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
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, exceptionresponse);
		}

		return response;
	}

	@PostMapping(value = "/get/weekly_tasktrack")
	public StatusResponse getWeeklyTasktrack(@RequestBody WeeklyTaskTrackWithoutTaskRequestDTO requestData) {
		StatusResponse response = new StatusResponse();
		try {
			response = weeklyApprovalService.getWeeklyTasktrack(requestData);
		} catch (Exception e) {
			ExceptionResponse exceptionResponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, exceptionResponse);
		}

		return response;
	}

	@PostMapping(value = "/get/tasktrack_with_task/weekly")
	public StatusResponse getWeeklyTasktrackWithTask(@RequestBody WeeklyTaskTrackWithTaskRequestDTO requestData) {
		StatusResponse response = new StatusResponse();
		try {
			response = new StatusResponse(Constants.SUCCESS,Constants.SUCCESS_CODE,weeklyApprovalService.getWeeklyTasktrackWithTask(requestData));

		} catch (Exception e) {
			ExceptionResponse exceptionResponse = new ExceptionResponse(1234, e.getMessage(), new Date());
			response = new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, exceptionResponse);
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
	
	@PostMapping(value = "/save_weekly_approval/with_task")
	public StatusResponse saveWeeklyTasktrackWithTask(@RequestBody SaveWeeklyTasktrackWithTaskRequestDTO2 requestData) {
		StatusResponse response = new StatusResponse();
		try {
			 response = weeklyApprovalService.saveWeeklyTasktrackWithTask(requestData);
			//weeklyApprovalService.saveOrSubmitWeeklyTasktrackWithTask(requestData, Boolean.TRUE);
			return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		} catch (Exception e) {
			return new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, response);
		}
		
	}


	
	@PostMapping(value = "/submit_weekly_approval/with_task")
	public StatusResponse submitWeeklyTasktrackWithTask(@RequestBody SubmitWeeklyTasktrackWithTaskRequestDTO2 requestData) {
		StatusResponse response = new StatusResponse();
		try {
//			weeklyApprovalService.saveOrSubmitWeeklyTasktrackWithTask(requestData, Boolean.FALSE);
			response = weeklyApprovalService.submitWeeklyTasktrackWithTask(requestData);
			return new StatusResponse(Constants.SUCCESS, Constants.SUCCESS_CODE, response);
		} catch (Exception e) {
			return new StatusResponse(Constants.FAILURE, Constants.ERROR_CODE, response);
		}
		
	}
	
}

package com.EMS.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EMS.model.ExceptionResponse;
import com.EMS.model.StatusResponse;
import com.EMS.service.TaskWeeklyApprovalService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/get/weekly_tasktrack")
	public ResponseEntity<Object> getWeeklyTasktrack(@RequestBody JSONObject requestData) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonResp = new JSONObject();
		try {
			JSONObject weeklyTasktrack = weeklyApprovalService.getWeeklyTasktrack(requestData);
			jsonResp.put("status", "Success");
			jsonResp.put("code", HttpServletResponse.SC_OK);
			jsonResp.put("data", weeklyTasktrack);
			response = new ResponseEntity<Object>(jsonResp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonResp.put("status", "Error");
			jsonResp.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonResp.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/get/tasktrack_with_task/weekly")
	public ResponseEntity<Object> getWeeklyTasktrackWithTask(@RequestBody JSONObject requestData) {
		ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.OK);
		JSONObject jsonResp = new JSONObject();
		try {
			JSONObject weeklyTasktrackWithTask = weeklyApprovalService.getWeeklyTasktrackWithTask(requestData);
			jsonResp.put("status", "Success");
			jsonResp.put("code", HttpServletResponse.SC_OK);
			jsonResp.put("data", weeklyTasktrackWithTask);
			response = new ResponseEntity<Object>(jsonResp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			jsonResp.put("status", "Error");
			jsonResp.put("code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonResp.put("message", e.getMessage());
			response = new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

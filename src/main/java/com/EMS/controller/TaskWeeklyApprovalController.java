package com.EMS.controller;

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

	@PostMapping(value = "/submit_weekly_approval")
	public JsonNode submitWeeklyApproval(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {

		int status = weeklyApprovalService.submitWeeklyApproval(requestData);

		ObjectNode responseData = objectmapper.createObjectNode();
		if (status == 0) {
			responseData.put("status", "success");
			responseData.put("message", "success. ");
			responseData.put("code", httpstatus.getStatus());
		} else {
			responseData.put("status", "success");
			responseData.put("message", "Failed due to invalid credentials ");
			responseData.put("code", httpstatus.getStatus());
		}

		return responseData;
	}

	@PostMapping(value = "/save_weekly_approval")
	public JsonNode saveWeeklyApproval(@RequestBody JSONObject requestData, HttpServletResponse httpstatus) {

		int status = weeklyApprovalService.saveWeeklyApproval(requestData);

		ObjectNode responseData = objectmapper.createObjectNode();
		if (status == 0) {
			responseData.put("status", "success");
			responseData.put("message", "success. ");
			responseData.put("code", httpstatus.getStatus());
		} else {
			responseData.put("status", "success");
			responseData.put("message", "Failed due to invalid credentials ");
			responseData.put("code", httpstatus.getStatus());
		}

		return responseData;
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

	@PostMapping(value = "/submitTasktrackWeekly")
	public JsonNode submitTasktrackWeekly(@RequestBody JsonNode requestData, HttpServletResponse httpstatus) {
		
		ObjectNode responseData = objectmapper.createObjectNode();
		try {
			
			int count=weeklyApprovalService.getWeeklyTasksForSubmission(requestData);
			if(count==0) {
				responseData.put("status", "success");
				responseData.put("code", httpstatus.getStatus());
				responseData.put("data", "");
			}else {
				responseData.put("status", "success");
				responseData.put("message", "Failed due to duplicate entry");
				responseData.put("code", httpstatus.getStatus());
				responseData.put("data", "");
			}
			
		} catch (Exception e) {
			responseData.put("status", "Failed");
			responseData.put("code", httpstatus.getStatus());
			responseData.put("message", "Exception "+e);
		}
		return responseData;
	}

}
